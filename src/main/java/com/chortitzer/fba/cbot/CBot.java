/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chortitzer.fba.cbot;

import com.chortitzer.fba.cbot.domain.TblBasPrecios;
import com.chortitzer.fba.cbot.domain.Tblproductos;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author adriang
 */
public class CBot {

    Properties mailServerProperties;
    Session getMailSession;
    MimeMessage generateMailMessage;

    Double priceCentUsdPerBushelMaiz = 0.0;
    Double priceCentUsdPerBushelSoja = 0.0;

    Double pygPorUSD = 0.0;

    int precioPygMaizSocios = 0;
    int precioPygSorgoSocios = 0;
    int precioPygSojaSocios = 0;

    int precioPygMaizNoSocios = 0;
    int precioPygSorgoNoSocios = 0;
    int precioPygSojaNoSocios = 0;

    Double premioUsdTonMaiz = 38.0;
    Double premioUsdTonSoja = 22.0;

    public void execute() {
        try {
            String urlSoja = "https://www.quandl.com/api/v3/datasets/CHRIS/CME_S1.json?api_key=BVdNGk1heNzSvQsCRBn8&start_date=2017-01-01";
            String urlMaiz = "https://www.quandl.com/api/v3/datasets/CHRIS/CME_C1.json?api_key=BVdNGk1heNzSvQsCRBn8&start_date=2017-01-01";

            JSONObject jsonObjMaiz = new JSONObject(readUrl(urlMaiz));
            JSONArray arrMaiz = jsonObjMaiz.getJSONObject("dataset").getJSONArray("data");

            JSONObject jsonObjSoja = new JSONObject(readUrl(urlSoja));
            JSONArray arrSoja = jsonObjSoja.getJSONObject("dataset").getJSONArray("data");

            for (int i = 0; i < 15; i++) {
                priceCentUsdPerBushelMaiz += arrMaiz.getJSONArray(i).getDouble(4);
                priceCentUsdPerBushelSoja += arrSoja.getJSONArray(i).getDouble(4);
            }

            if (priceCentUsdPerBushelMaiz == 0 || priceCentUsdPerBushelSoja == 0) {
                TimeUnit.MINUTES.sleep(10);
                execute();
                return;
            }

            priceCentUsdPerBushelMaiz = priceCentUsdPerBushelMaiz / 15;
            priceCentUsdPerBushelSoja = priceCentUsdPerBushelSoja / 15;

            //convertt from cUSD to USD
            Double priceUsdPerBushelMaiz = priceCentUsdPerBushelMaiz / 100;
            Double priceUsdPerBushelSoja = priceCentUsdPerBushelSoja / 100;

            Double kgPerBushelMaiz = 25.40;

            Double premioUsdPorKgMaiz = premioUsdTonMaiz / 1000.0;

            Double kgPerBushelSoja = 27.22;

            Double premioUsdPorKgSoja = premioUsdTonSoja / 1000.0;

            Double fleteSocios = 50.0;
            Double fleteNoSocios = 100.0;

            pygPorUSD = getPYGporUSD();

            while (pygPorUSD == 0) {
                TimeUnit.MINUTES.sleep(10);
                pygPorUSD = getPYGporUSD();
            }

            precioPygMaizSocios = (int) Math.round((priceUsdPerBushelMaiz / kgPerBushelMaiz - premioUsdPorKgMaiz) * pygPorUSD - fleteSocios);
            precioPygSorgoSocios = (int) Math.round((double) precioPygMaizSocios * 0.9);
            precioPygSojaSocios = (int) Math.round((priceUsdPerBushelSoja / kgPerBushelSoja - premioUsdPorKgSoja) * pygPorUSD - fleteSocios);

            precioPygMaizNoSocios = (int) Math.round((priceUsdPerBushelMaiz / kgPerBushelMaiz - premioUsdPorKgMaiz) * pygPorUSD - fleteNoSocios);
            precioPygSorgoNoSocios = (int) Math.round((double) precioPygMaizNoSocios * 0.9);
            precioPygSojaNoSocios = (int) Math.round((priceUsdPerBushelSoja / kgPerBushelSoja - premioUsdPorKgSoja) * pygPorUSD - fleteNoSocios);

            save();

            generateAndSendEmail();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void save() {
        try {
            EntityManagerFactory emf;
            EntityManager em;

            emf = Persistence.createEntityManagerFactory("CBOT_PU");
            em = emf.createEntityManager();

            em.getTransaction().begin();

            TblBasPrecios precio = new TblBasPrecios();
            em.persist(precio);
            precio.setFechahoraVigencia(LocalDateTime.now());
            precio.setIdProducto(em.find(Tblproductos.class, 69));
            precio.setValorGsPorKg(precioPygSojaSocios);

            em.getTransaction().commit();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void generateAndSendEmail() throws AddressException, MessagingException {

        final String username = "labindustria@chortitzer.com.py";
        final String password = "indu16labor";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("cbot.industria@chortitzer.com.py"));
            message.addRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("adriang@chortitzer.com.py"));
            message.addRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("christophh@chortitzer.com.py"));
            message.addRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("oliverw@chortitzer.com.py"));
            message.setSubject("Cotizacion Semanal de Granos - Balanceados Chortitzer");
            message.setText(
                    "<html><body>"
                    + "<h3>Estos precios serían los ideales para esta semana</h3>"
                    + "Fecha de vigencia: Desde " + LocalDate.now().toString() + " hasta " + LocalDate.now().plusDays(6).toString() + "<p>"
                    + "<b>Maiz:</b> <br />Socios: " + formatPyg(precioPygMaizSocios)
                    + "<br />No Socios: " + formatPyg(precioPygMaizNoSocios)
                    + "<p>"
                    + "<b>Soja:</b> <br />Socios: " + formatPyg(precioPygSojaSocios)
                    + "<br />No Socios: " + formatPyg(precioPygSojaNoSocios)
                    + "<p>"
                    + "<b>Sorgo:</b> <br />Socios: " + formatPyg(precioPygSorgoSocios)
                    + "<br />No Socios: " + formatPyg(precioPygSorgoNoSocios)
                    + "<p><p>"
                    + "<b>Referencias:</b> <br />"
                    + "Cotizacion USD: " + formatPygUsd(pygPorUSD.intValue()) + "<br />"
                    + "Maiz - Cotizacion Promedio Chicago - Ultimos 5 dias: " + formatUsd(priceCentUsdPerBushelMaiz.intValue()) + "<br />"
                    + "Maiz - Premio: " + formatUsd(premioUsdTonMaiz.intValue()) + " USD/ton<br />"
                    + "Soja - Cotizacion Promedio Chicago - Ultimos 5 dias: " + formatUsd(priceCentUsdPerBushelSoja.intValue()) + "<br />"
                    + "Soja - Premio: " + formatUsd(premioUsdTonSoja.intValue()) + " USD/ton<p>"
                    + "<code>Este menaje ha sido generado automaticamente por el sistema cbot.industria</code><p>"
                    + "</body></html>", "utf-8", "html");

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String formatPyg(Integer importe) {
        DecimalFormat formatter = new DecimalFormat("#,##0");
        return "<b>" + formatter.format(importe) + "</b> PYG/Kg";
    }

    private String formatPygUsd(Integer importe) {
        DecimalFormat formatter = new DecimalFormat("#,##0");
        return "<b>" + formatter.format(importe) + "</b> PYG/USD";
    }

    private String formatUsd(Integer importe) {
        DecimalFormat formatter = new DecimalFormat("#,##0");
        return "<b>" + formatter.format(importe) + "</b> cUSD/bushel";
    }

    private Double getPYGporUSD() {
        try {
            Document doc = Jsoup.connect("http://192.168.1.10/avisos/cinterno.php").get();
            Elements links = doc.getElementsByClass("labelCompraVentaCajeros");
            //links = links.get(0).getElementsByTag("td");
            //links = links.get(0).getElementsByTag("tr");

            String compra = links.get(0).text().split(" ")[1].replace(".", "");
            String venta = links.get(1).text().split(" ")[1].replace(".", "");

            return Double.parseDouble(venta);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0;
        }
    }

    private String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            return buffer.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}

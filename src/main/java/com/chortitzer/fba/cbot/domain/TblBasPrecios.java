/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chortitzer.fba.cbot.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author adriang
 */
@Entity
@Table(name = "tbl_bas_precios")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TblBasPrecios.findAll", query = "SELECT t FROM TblBasPrecios t")
    ,
    @NamedQuery(name = "TblBasPrecios.findByFechahoraVigencia", query = "SELECT t FROM TblBasPrecios t WHERE t.fechahoraVigencia = :fechahoraVigencia")
    ,
    @NamedQuery(name = "TblBasPrecios.findByValorGsPorKg", query = "SELECT t FROM TblBasPrecios t WHERE t.valorGsPorKg = :valorGsPorKg")})
public class TblBasPrecios implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ObjectProperty<LocalDateTime> fechahoraVigencia = new SimpleObjectProperty<>();

    @Id
    @Basic(optional = false)
    @Column(name = "fechahora_vigencia")
    public LocalDateTime getFechahoraVigencia() {
        return fechahoraVigencia.get();
    }

    public void setFechahoraVigencia(LocalDateTime fechahoraVigencia) {
        this.fechahoraVigencia.set(fechahoraVigencia);
    }

    public ObjectProperty<LocalDateTime> fechahoraVigenciaProperty() {
        return fechahoraVigencia;
    }

    private final ObjectProperty<Tblproductos> idProducto = new SimpleObjectProperty<>();

    @JoinColumn(name = "id_producto", referencedColumnName = "id")
    @ManyToOne(optional = false)
    public Tblproductos getIdProducto() {
        return idProducto.get();
    }

    public void setIdProducto(Tblproductos idProducto) {
        this.idProducto.set(idProducto);
    }

    public ObjectProperty<Tblproductos> idProductoProperty() {
        return idProducto;
    }

    private final IntegerProperty valorGsPorKg = new SimpleIntegerProperty();

    @Basic(optional = false)
    @Column(name = "valor_gs_por_kg")
    public int getValorGsPorKg() {
        return valorGsPorKg.get();
    }

    public void setValorGsPorKg(int valorGsPorKg) {
        this.valorGsPorKg.set(valorGsPorKg);
    }

    public IntegerProperty valorGsPorKgProperty() {
        return valorGsPorKg;
    }

    public TblBasPrecios() {
    }

    /*
    public TblBasPrecios(LocalDateTime fechahoraVigencia) {
        this.fechahoraVigencia = fechahoraVigencia;
    }

    public TblBasPrecios(LocalDateTime fechahoraVigencia, int valorGsPorKg) {
        this.fechahoraVigencia = fechahoraVigencia;
        this.valorGsPorKg = valorGsPorKg;
    }


/*
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fechahoraVigencia != null ? fechahoraVigencia.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TblBasPrecios)) {
            return false;
        }
        TblBasPrecios other = (TblBasPrecios) object;
        if ((this.fechahoraVigencia == null && other.fechahoraVigencia != null) || (this.fechahoraVigencia != null && !this.fechahoraVigencia.equals(other.fechahoraVigencia))) {
            return false;
        }
        return true;
    }*/

    @Override
    public String toString() {
        return "com.chortitzer.industria.bascula.domain.TblBasPrecios[ fechahoraVigencia=" + fechahoraVigencia + " ]";
    }

}

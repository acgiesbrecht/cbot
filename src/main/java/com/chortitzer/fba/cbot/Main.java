/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chortitzer.fba.cbot;

/**
 *
 * @author adriang
 */
public class Main {

    public static void main(String args[]) {
        try {
            CBot cbot = new CBot();
            cbot.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

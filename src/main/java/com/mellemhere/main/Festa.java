/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.main;

import java.util.Random;

/**
 *
 * @author MellemHere
 */
class Festa extends Thread {

    public Festa() {
        this.start();
    }

    long lasttime;
    boolean running = true;
    int speed = 300;
    
    
    Controller con;

    public Festa(Controller con) {
        this.con = con;
        lasttime = System.currentTimeMillis();
    }
    
    
    public void setRunning(boolean running) {
        this.running = running;
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();
        
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    @Override
    public void run() {
        while (running) {
            if (System.currentTimeMillis() - lasttime > speed) {
                lasttime = System.currentTimeMillis();
                int light = randInt(15, 19);
                con.getLight().turnLight(String.valueOf(light));
            }
        }
    }

}

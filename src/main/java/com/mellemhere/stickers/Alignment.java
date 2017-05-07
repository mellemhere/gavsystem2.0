/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.stickers;

import java.awt.Dimension;

/**
 *
 * @author MellemHere
 */
public class Alignment {

    private final int biggestLineLenght;
    private final Dimension init;

    private int finalHeight;

    public Alignment(int biggestLineLenght, Dimension init) {
        this.biggestLineLenght = biggestLineLenght;
        this.init = init;
        this.finalHeight = (int) init.getHeight();
    }

    public int getX() {
        return (int) init.getWidth();
    }

    public int getY() {
        return (int) init.getHeight();
    }

    public int getBiggestLineLenght() {
        return biggestLineLenght;
    }

    public int getCenterOffset(int stringSize) {

        if (stringSize != this.biggestLineLenght) {
            return ((this.biggestLineLenght - stringSize) / 2);
        } else {
            return 0;
        }

    }

    public int getFinalHeight() {
        return finalHeight;
    }

    public void setFinalHeight(int finalHeight) {
        this.finalHeight = finalHeight;
    }

}

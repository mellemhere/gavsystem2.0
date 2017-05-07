/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.stickers;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MellemHere
 */
public class TextHandler {

    final private String text;
    final private Graphics graphics;
    final private int boxWidth;

    List<String> lines = new ArrayList<>();

    private int biggestLineSize;

    final private FontMetrics font;

    private int finalWidth = 0;
    private int finalHeight;

    private Alignment align;

    public TextHandler(String text, int boxWidth, Dimension pointToDraw, Graphics g) {
        this.text = text;
        this.boxWidth = boxWidth;
        this.graphics = g;
        this.font = this.graphics.getFontMetrics();
        this.setup();
        this.align = new Alignment(getBiggestLineSize(), pointToDraw);
    }

    private int size(String s) {
        return font.stringWidth(s);
    }

    private void setup() {

        if (size(this.text) < this.boxWidth) {
            this.lines.add(this.text);
        } else {
            /*
             We need to break into lines
             */
            String[] words = this.text.split(" ");
            String currentLine = "";

            for (String word : words) {

                if (font.stringWidth(currentLine.concat(word + " ")) > this.boxWidth) {
                    this.lines.add(currentLine);
                    currentLine = word + " ";
                } else {
                    currentLine += word + " ";
                }
            }

            if (currentLine.trim().length() > 0) {
                this.lines.add(currentLine);
            }
        }

        this.biggestLineSize = getBiggestLineSize();
    }

    public List<String> getLines() {
        return this.lines;
    }

    private int getBiggestLineSize() {

        int biggestSize = 0;
        for (String line : this.lines) {
            if (size(line) > biggestSize) {
                biggestSize = size(line);
            }
        }

        return biggestSize;
    }

    public Alignment getAlignment() {
        return align;
    }

    public void draw() {

        int counter = 0;

        int x = this.align.getX();
        int y = this.align.getFinalHeight();

        for (String line : lines) {
            this.finalWidth = x + align.getCenterOffset(size(line));
            this.finalHeight = y + (font.getAscent() * counter);

            this.graphics.drawString(line, this.finalWidth, this.finalHeight);
            counter++;
        }

        this.align.setFinalHeight(y + (font.getAscent() * counter));
    }

    void useAlignment(Alignment alignment) {
        this.align = alignment;
    }

}

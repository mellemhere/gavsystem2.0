/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.stickers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author MellemHere
 */
public class Sticker {

    //private final float width = 788; //Or 66,72 MM
    //private final float height = 300;//Or 25,4 MM
    private final int maxLineWidth = 400;

    final private BufferedImage sticker;
    final private Graphics graphics;

    final private Dimension dimension;

    Alignment nameAlignment = null;

    public Sticker(int width, int height) {
        this.dimension = new Dimension(width, height);

        this.sticker = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.graphics = this.sticker.getGraphics();
        this.graphics.setColor(Color.WHITE);
        this.graphics.fillRect(0, 0, width, height);

    }

    public void setName(String name) {

        this.graphics.setColor(Color.BLACK);

        this.graphics.setFont(Font.getFont("Roboto"));
        Font fontx = new Font("Roboto", Font.PLAIN, 36);
        this.graphics.setFont(fontx);

        TextHandler thx = new TextHandler(name, maxLineWidth, new Dimension(380, 50), this.graphics);
        thx.draw();

        this.nameAlignment = thx.getAlignment();
    }

    public void setUnderText(String id) {
        this.graphics.setFont(Font.getFont("Roboto"));
        Font fontx = new Font("Roboto", Font.PLAIN, 26);
        this.graphics.setFont(fontx);

        FontMetrics fm = this.graphics.getFontMetrics();
        int xPosition = 280 - fm.stringWidth(id);

        this.graphics.drawString(id, 380 + xPosition, 280);
        
    }

    public void setCode(String code) {

        this.graphics.setColor(Color.BLACK);

        this.graphics.setFont(Font.getFont("Roboto"));
        Font fontx = new Font("Roboto", Font.BOLD, 24);
        this.graphics.setFont(fontx);

        TextHandler th = new TextHandler(code, maxLineWidth, new Dimension(446, 110), this.graphics);

        if (this.nameAlignment != null) {
            th.useAlignment(this.nameAlignment);
        }

        th.draw();

    }

    public void setLogo(BufferedImage logo) {
        this.graphics.drawImage(logo, 43, 40, null);
    }

    public void setQRCode(String code) {
        System.out.println(QRCode.draw(code).getWidth());
        System.out.println(QRCode.draw(code).getHeight());

        this.graphics.drawImage(QRCode.draw(code), 650, 183, Color.BLUE, null);
    }

    public BufferedImage getSticker() {
        return sticker;
    }

    public Dimension getDimension() {
        return dimension;
    }

    
}

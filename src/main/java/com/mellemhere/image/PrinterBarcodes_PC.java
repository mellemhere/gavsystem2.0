/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.image;

import com.mellemhere.prop.items.Item;
import com.mellemhere.util.BarCode;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author MellemHere
 */
public class PrinterBarcodes_PC {

    private final Graphics graphics;
    private final BufferedImage finalImage;
    private final List<Item> items;

    int marginX = 42;
    int marginY = 49;

    final int imgWidth = 134;
    final int imgHeight = 64;

    //final int imgWidth = 234;
    //final int imgHeight = 114;

    final int tagsPerLine = 3;

    public PrinterBarcodes_PC(List<Item> items) {
        this.items = items;
        this.finalImage = new BufferedImage(793, 1133, BufferedImage.TYPE_INT_ARGB);
        this.graphics = finalImage.getGraphics();
    }

    private BufferedImage generate() {

        int currentX;
        int currentY = this.marginY;

        int numberOfTags = 0;
        int numberOfLines = 1;

        for (Item item : items) {
            
           // BufferedImage img = this.generateTag(item.getName(), item.getBarCode());

            if ((numberOfTags % this.tagsPerLine) == 0 && (numberOfTags != 0)) {
                //We should go to the next line
                currentY = this.marginY + (this.imgHeight * numberOfLines);
                numberOfLines++;
                currentX = this.marginX; // Reset X
                numberOfTags = 1; //Resets the number of tags in the line because we created a new one
            } else {
                currentX = this.marginX + (this.imgWidth * numberOfTags);
                numberOfTags++;
            }

         //  graphics.drawImage(resize(img, 120, 120), currentX, currentY, null);
        }

        graphics.dispose();
        try {
            ImageIO.write(this.finalImage, "png", new File("Hellou.png"));
        } catch (IOException ex) {
            Logger.getLogger(PrinterBarcodes_PC.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public BufferedImage generateTag(String tagText, String QRCode) {
        try {
            ImageIcon img = new ImageIcon("src/backimage.png");

            BufferedImage image = new BufferedImage(
                    img.getIconWidth(),
                    img.getIconHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = image.createGraphics();

            img.paintIcon(null, g, 0, 0);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", 1, 16));
            int posicaoIncial = 315 - (tagText.length() / 2) * 9;

            g.drawString(tagText, posicaoIncial, 40);
            BufferedImage imged = resize(BarCode.generateQRCodeImage(QRCode), this.imgWidth, this.imgHeight);

            g.drawImage(imged, 265, 45, null);

            g.dispose();

            return image;

        } catch (Exception e) {
        }
        return null;
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }

    public static void main(String[] args) {
        List items = new ArrayList<Item>();
        Item i = new Item();

        i.setName("PC DA XUXA");
        //i.setBarCode("http://sisgav.com/codescan?code=1231321");
        items.add(i);

        /*Item x = new Item();
        x.setName("PC DA XUXA4");
        x.setBarCode("asdasdasd");
        items.add(x);

        Item y = new Item();
        y.setName("PC DA XUXA3");
        y.setBarCode("asdasdasd");
        items.add(y);

        Item z = new Item();
        z.setName("PC DA XUXA2");
        z.setBarCode("asdasdasd");
        items.add(z);

        Item t = new Item();

        t.setName("PC DA XUXA");
        t.setBarCode("asdasdasd");
        items.add(t);

        Item u = new Item();
        u.setName("PC DA XUXA4");
        u.setBarCode("asdasdasd");
        items.add(u);

        Item k = new Item();
        k.setName("PC DA XUXA3");
        k.setBarCode("asdasdasd");
        items.add(k);

        Item l = new Item();
        l.setName("PC DA XUXA2");
        l.setBarCode("asdasdasd");
        items.add(l);
        Item g = new Item();
        g.setName("PC DA XUXA2");
        g.setBarCode("asdasdasd");
        items.add(g);*/

        PrinterBarcodes_PC pc = new PrinterBarcodes_PC(items);
        pc.generate();
    }

}

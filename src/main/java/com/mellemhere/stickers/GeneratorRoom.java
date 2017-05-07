/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.stickers;

import com.mellemhere.main.Controller;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 *
 * @author MellemHere
 */
public class GeneratorRoom {
 
    
    
    final private int quantity;
    
    private String name;
    private int roomID = 0;
    private String underText = ""; 
    
    List<Sticker> stickers = new ArrayList<>();

    public GeneratorRoom(int quantity) {
        this.quantity = quantity;
    }
    
    
    private String getID(int index){
        return "UPLAB" + this.roomID + String.format("%03d", index);
    }
    
    public List<Sticker> generateStickers(){
        
        for(int i = 1; i >= quantity; i++){
            
            Sticker sticker = new Sticker(788, 300);
            
            sticker.setCode(getID(i));
            
            sticker.setName(name);
            sticker.setUnderText(underText);
            
            sticker.setQRCode(getID(i));
            
            sticker.setLogo(this.loadImage());
           
            
            this.stickers.add(sticker);

        }
        
        return this.stickers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public String getUnderText() {
        return underText;
    }

    public void setUnderText(String underText) {
        this.underText = underText;
    }

    
    private BufferedImage loadImage() {
        BufferedImage img = null;

        try {
            img = ImageIO.read(new File("C:/logo.png")); 
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }
    
    
    
}

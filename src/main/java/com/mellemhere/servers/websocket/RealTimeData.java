/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.servers.websocket;

import com.mellemhere.servers.connection.Connection;
import java.awt.Dimension;

import com.github.sarxos.webcam.Webcam;
import com.mellemhere.server.websocket.mObjects.LogObject;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author MellemHere
 */
public class RealTimeData {

    private final Connection con;

    private Webcam webcam;

    private final boolean flipImage = true;

    private final Timer timer;

    private final WebSocketController wcon;
    private final int doorID;

    private final boolean hasWebCam = false;

    public RealTimeData(Connection con) {

        this.con = con;
        if (hasWebCam) {
            this.webcam = Webcam.getDefault();
        }
        this.timer = new Timer();

        this.wcon = con.getCcon().getCon().getWebSocketController();
        this.doorID = con.getRoom().getDoorID();

        con.getCcon().getCon().log("RealTimeData", "Iniciando broadcast de dados para a porta " + this.doorID, null);

        if (hasWebCam) {

            if (this.webcam != null) {
                if (!webcam.isOpen()) {
                    webcam.setViewSize(new Dimension(320, 240));
                    webcam.open();
                } else {
                    this.webcam = null;
                }
            }
        }
    }

    public void start() {
        if (this.webcam != null) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    broadcastUptime();
                    broadcastStatus();
                }
            }, 0, 1000);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    broadcastWebcam();
                }
            }, 0, 60);
        }
    }

    private void broadcastStatus() {

    }

    private void broadcastUptime() {

    }

    private void broadcastLastEntry() {

    }

    private void broadcastEnergyPrice() {

    }

    private void broadcastEnergyConsumption() {

    }

    private void broadcastWebcam() {
        if (this.webcam == null) {
            return;
        }
        if(!webcam.isOpen()){
            return;
        }
        this.send("webcam", new LogObject(getWebCamPicture()));
    }

    private void send(String eventName, Object ob) {
        this.wcon.broadcastToRoomID(this.doorID, eventName, ob);
    }


    /*
    
     WEBCAM CODE
   
     */
    public String getWebCamPicture() {
        if (webcam == null) {
            return "";
        }
        if (this.webcam.isOpen()) {
            BufferedImage image = webcam.getImage();
            if (this.flipImage) {
                return encodeToString(getFlippedImage(image));
            } else {
                return encodeToString(image);
            }
        }
        System.out.println("Webcam not open!");
        return null;
    }

    public static String encodeToString(BufferedImage image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "JPEG", baos);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String data = DatatypeConverter.printBase64Binary(baos.toByteArray());
        return "data:image/png;base64," + data;
    }

    public static BufferedImage getFlippedImage(BufferedImage bi) {
        if (bi == null) {
            return null;
        }
        BufferedImage flipped = new BufferedImage(
                bi.getWidth(),
                bi.getHeight(),
                5);
        AffineTransform tran = AffineTransform.getTranslateInstance(0, bi.getHeight());
        AffineTransform flip = AffineTransform.getScaleInstance(1d, -1d);
        tran.concatenate(flip);

        Graphics2D g = flipped.createGraphics();
        g.setTransform(tran);
        g.drawImage(bi, 0, 0, null);
        g.dispose();

        return flipped;
    }

}

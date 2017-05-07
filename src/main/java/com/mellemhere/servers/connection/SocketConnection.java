/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.servers.connection;

import com.mellemhere.main.Controller;
import com.mellemhere.server.websocket.mObjects.RoomObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MellemHere
 */
public class SocketConnection extends Thread implements ConnectionInterface {

    private String area = "SocketConnection - ";

    private final Room room;
    private final ConnectionController controller;
    private final Controller con;

    private final RoomObject roomObj;

    private final Socket client;

    private final Commands cmd;

    private OutputStream out;
    private InputStream in;

    public SocketConnection(Room room, Socket client) {
        this.room = room;
        this.controller = room.getCcon();
        this.con = controller.getCon();
        this.roomObj = room.getRoom();
        this.client = client;
        this.cmd = new Commands(this);
        area += this.roomObj.getDoorID();
    }

    @Override
    public void run() {
        try {
            this.out = client.getOutputStream();
            this.in = client.getInputStream();
        } catch (IOException ex) {
            Logger.getLogger(SocketConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        String buffer = "";
        while (true) {

            try {
                int c = in.read();
                if (c != 10) {
                    buffer += (char) c;
                } else {
                    processCommand(buffer);
                    buffer = "";
                }
            } catch (IOException ex) {
                System.out.println("Coneccao com cliente perdida :) " + roomObj.getDoorID());
                break;
            }

        }
    }

    @Override
    public void processCommand(String message) {
        if (message.startsWith(" ")) {
            return;
        }
        if (message.contains(";")) {
            String[] commandArgs = message.split(";");
            sendMessage(cmd.command(commandArgs[0], commandArgs[1]));
        } else {
            this.con.log(area, "Out of normal command recived: " + message, null);
        }
    }

    @Override
    public Controller getController() {
        return this.controller.getCon();
    }

    @Override
    public Commands getCommandHandler() {
        return this.cmd;
    }

    @Override
    public Room getConnection() {
        return room;
    }

    public boolean isIsConnected() {
        return client.isConnected();
    }

    @Override
    public void startConnection() {
        this.start();
    }

    @Override
    public void stopConnection() {
        this.interrupt();
        try {
            this.client.close();
        } catch (IOException ex) {
            Logger.getLogger(SocketConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sendMessage(String message) {
        try {
            this.out.write(message.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(SocketConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

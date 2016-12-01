/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.servers.connection;

import com.fazecast.jSerialComm.SerialPort;
import com.mellemhere.main.Controller;
import com.mellemhere.server.websocket.mObjects.RoomObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MellemHere
 */
public class SocketConnection extends Thread implements ConnectionInterface {

    private final ConnectionController con;

    private final Room connection;

    private String area = "SOCKET-CONNECTION-";

    private final String IP;

    private final RoomObject room;

    private boolean running = false;

    private ServerSocket socketserver;
    
    Socket clientSocket;
    
    private OutputStream out;

    private final Commands cmd;

    private boolean isConnected = false;

    public SocketConnection(Room con, RoomObject room) throws ConnectionError {

        System.out.println("Server online");

        this.con = con.getCcon();
        this.connection = con;
        this.IP = room.getComID(); //IP
        this.room = room;
        this.area += room.getDoorID();

        this.cmd = new Commands(this);

        
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(6969);
            clientSocket = serverSocket.accept();
            this.start();
        } catch (IOException ex) {
            Logger.getLogger(SocketConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void stopConnection() {
    }

    @Override
    public void sendMessage(String message) {
        if (message.equalsIgnoreCase("")) {
            return;
        }
        
        
        message += '\n';

        if (!isConnected) {
            this.con.log(area, "Nao 'e possivel mandar mensagem para cliente nao conectado!", null);
            return;
        }

        try {
            out.write(message.getBytes());
        } catch (IOException ex) {
            this.con.log(area, "Nao foi possivel mandar mensagem para o cliente via serial!", ex);
        }
    }

    @Override
    public void run() {
        System.out.println("Novo cliente socket, esperando por msgs");
        InputStream in = null;
        try {
            this.running = true;
            in = clientSocket.getInputStream();
            this.out = clientSocket.getOutputStream();
            try {
                this.isConnected = true;
                String command = "";
                while (running) {
                    int bIn = in.read();
                    /*
                    This will wait for the next \n
                    */
                    if (bIn == 10) {
                        if (!command.equalsIgnoreCase("")) {
                            this.processCommand(command);
                        }
                        command = "";
                    } else {
                        command += (char) bIn;
                    }
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
                con.log(area, "Erro ao se comunicar com porta serial (" + this.IP + ")", e);
                this.isConnected = false;
            }
            con.log(area, "Fechando comunicacao com a porta (" + this.IP + ")", null);
            this.isConnected = false;
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(SocketConnection.class.getName()).log(Level.SEVERE, null,ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                Logger.getLogger(SocketConnection.class.getName()).log(Level.SEVERE, null, ex);
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
        return this.con.getCon();
    }

    @Override
    public Commands getCommandHandler() {
        return this.cmd;
    }

    @Override
    public Room getConnection() {
        return this.connection;
    }

    public boolean isIsConnected() {
        return isConnected;
    }

    @Override
    public void startConnection() {
        //TO-DO
    }

}

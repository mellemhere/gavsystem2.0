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

/**
 *
 * @author MellemHere
 */
public class SerialConnection extends Thread implements ConnectionInterface {

    private final ConnectionController con;
    
    private final Connection connection;
    
    private String area = "SERIAL-CONNECTION-";
    private final int COM_ID;
    private final RoomObject room;

    private boolean running = false;

    private SerialPort comPort;
    private OutputStream out;
    
    private final Commands cmd;
    

    public SerialConnection(Connection con, RoomObject room) throws ConnectionError {
        
        this.con = con.getCcon();
        this.connection = con;
        this.COM_ID = room.getComID();
        this.room = room;
        this.area += room.getDoorID();
        
        this.cmd = new Commands(this);
        
        if (SerialPort.getCommPorts().length == 0 || SerialPort.getCommPorts().length < this.COM_ID) {
            throw new ConnectionError("Porta COM nao existente! " + this.COM_ID);
        }
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void startConnection() {
        this.start();
    }

    @Override
    public void stopConnection() {
    }

    @Override
    public void sendMessage(String message) {
        try {
            out.write(message.getBytes());
        } catch (IOException ex) {
            this.con.log(area, "Nao foi possivel mandar mensagem para o cliente via serial!", ex);
        }
    }

    @Override
    public void run() {
        this.running = true;

        con.log(area, "Conectando a porta COM: " + this.COM_ID, null);
        comPort = SerialPort.getCommPorts()[this.COM_ID];
        comPort.setBaudRate(115200);
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
        InputStream in = comPort.getInputStream();
        this.out = comPort.getOutputStream();

        try {
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
            con.log(area, "Erro ao se comunicar com porta serial (COM" + this.COM_ID + ")", e);
        }

        con.log(area, "Fechando comunicacao com a porta (COM" + this.COM_ID + ")", null);
        comPort.closePort();
    }

    public void sendByteMessage(String message) {
        try {
            out.write(hexStringToByteArray(message));
        } catch (IOException ex) {
            this.con.log(area, "Nao foi possivel mandar mensagem para o cliente via serial!", ex);
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    @Override
    public void processCommand(String message) {
        if (message.startsWith(" ")) {
            return;
        }
        if (message.contains(";")) {
            String[] commandArgs = message.split(";");
            cmd.command(commandArgs[0], commandArgs[1]);
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
    public Connection getConnection() {
       return this.connection;
    }
}

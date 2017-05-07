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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MellemHere
 */
public class SerialConnection extends Thread implements ConnectionInterface {

    private final ConnectionController con;

    private final Room connection;

    private String area = "SERIAL-CONNECTION-";
    private final String COM_ID;
    private final int COM_INDEX;

    private final RoomObject room;
    
    private boolean running = false;

    private SerialPort comPort;
    private OutputStream out;

    private final Commands cmd;

    private boolean isConnected = false;

    private int getCOMPort(String nomeDaCom) {

        /*LinkedHashSet<String> ports = new LinkedHashSet<>();

        for (SerialPort serial : SerialPort.getCommPorts()) {
            ports.add(serial.getSystemPortName());
        }*/

        int index = 0;
        for (SerialPort serial : SerialPort.getCommPorts()) {
            if (serial.getSystemPortName().equalsIgnoreCase(nomeDaCom)) {
                return index;
            } else {
                index++;
            }
        }

        return -1;
    }

    public SerialConnection(Room con, RoomObject room) throws ConnectionError {

        this.con = con.getCcon();
        this.connection = con;
        this.COM_ID = room.getComID();
        
        this.area += room.getDoorID();
        
        this.room = room;
        
        this.cmd = new Commands(this);

        this.COM_INDEX = getCOMPort(this.COM_ID);

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
        this.running = false;
    }

    @Override
    public void sendMessage(String message) {
        if (message.equalsIgnoreCase("")) {
            return;
        }

        if (!isConnected) {
            this.con.log(area, "Nao 'e possivel mandar mensagem para cliente nao conectado!", null);
            this.restart();
            return;
        }

        try {
            out.write(message.getBytes());
        } catch (IOException ex) {
            this.con.log(area, "Nao foi possivel mandar mensagem para o cliente via serial!", ex);
            this.restart();
        }
    }

    @Override
    public void run() {
        this.running = true;
        
        con.log(area, "Conectando a porta " + this.COM_ID, null);
        if (this.COM_INDEX == -1) {
            con.log(area, "Porta " + this.COM_ID + " nao encontrada", null);
            connection.setStatus(ConnectionStatus.FAILED);
            return;
        }
        comPort = SerialPort.getCommPorts()[this.COM_INDEX];

        comPort.setBaudRate(115200);
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);

        InputStream in = comPort.getInputStream();
        this.out = comPort.getOutputStream();
        
        con.log(area, "Tendando abrir conexao - " + this.area, null);
        while (!comPort.isOpen()) {
            comPort.openPort();
        }
        con.log(area, "Conectado com - " + this.area, null);
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
            con.log(area, "Erro ao se comunicar com porta serial (" + this.COM_ID + ")", e);
            this.isConnected = false;
            this.restart();
        }

        con.log(area, "Fechando comunicacao com a porta (" + this.COM_ID + ")", null);
        this.isConnected = false;
        comPort.closePort();
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
    
    public void restart(){
        try {
            this.out.close();
        } catch (IOException ex) {
            Logger.getLogger(SerialConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.comPort.closePort();
        this.stopConnection();
        
        this.con.setDesconnected(room);
        
        this.con.reconnect(room);
        
    }
    
}

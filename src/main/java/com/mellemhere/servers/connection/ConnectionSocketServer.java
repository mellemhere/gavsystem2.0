/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.servers.connection;

import com.mellemhere.main.Controller;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 *
 * @author MellemHere
 */
public class ConnectionSocketServer extends Thread {

    private final String area = "ConnectionSocketServer";

    private final ConnectionController controller;

    private final Controller con;

    private final int port = 6969;

    private ServerSocket server;

    private void newConnection(Socket socket) {

        for (Room room : controller.getRooms().values()) {
            SocketConnection sc = new SocketConnection(room, socket);
            sc.startConnection();
            room.setConnection(sc);
            room.setStatus(ConnectionStatus.CONNECTED);
        }

        for (Room room : controller.getRooms().values()) {
            System.out.print("Door " + room.getRoom().getDoorID()+ " status:");
            switch (room.getStatus()) {
                case CONNECTED:
                    System.out.print("CONNECTED");
                    break;
                case CONNECTING:
                    System.out.print("CONNECTING");
                    break;
                case FAILED:
                    System.out.print("FAILED");
                    break;
            }
            System.out.println("--");
        }

    }

    public ConnectionSocketServer(ConnectionController controller) {
        this.controller = controller;
        this.con = controller.getCon();
        try {
            this.server = new ServerSocket(6969);
        } catch (IOException ex) {
            con.log(area, "Erro ao criar servidor socket", ex);
        }
        con.log(area, "Servidor socket iniciado.. esperando por clientes", null);
        this.start();
    }

    @Override
    public void run() {
        while (true) {

            try {
                Socket client = this.server.accept();
                this.newConnection(client);
            } catch (IOException ex) {
                con.log(area, "Erro", ex);
                break;
            }

        }
    }

    private String getIP(SocketAddress adress) {
        return adress.toString().replace("/", "").split(":")[0];
    }

}

package com.mellemhere.servers.websocket;

import com.mellemhere.main.Controller;
import com.mellemhere.server.websocket.mObjects.DoorOpenedObject;
import com.mellemhere.server.websocket.mObjects.EventObject;
import com.mellemhere.server.websocket.mObjects.LastEntryObject;
import com.mellemhere.servers.connection.Command;
import com.mellemhere.servers.connection.Connection;
import com.mellemhere.servers.connection.ConnectionStatus;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.websocket.api.Session;

public class WebSocketController {

    private final Controller con;
    private boolean isOnline = false;
    private WebSocketHandler sHandler;

    //SESSION ROOMID
    HashMap<Session, Integer> clients = new HashMap<>();

    private final String area = "WEBSOCKET";

    public WebSocketController(Controller con) {
        this.con = con;
    }

    public void start() {
        con.log(area, "Iniciando servidor socket...", null);
        this.isOnline = true;
    }

    public Controller getController() {
        return con;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void broadcastToAll(String eventName, Object message) {
        String finalMessage = con.toJSON(new EventObject(eventName, message)).toString();
        this.clients.forEach((client, doorid) -> {
            if (client.isOpen()) {
                try {
                    client.getRemote().sendString(finalMessage);
                } catch (IOException ex) {
                    con.log(area, "Nao foi possivel mandar mensagem a cliente: " + client.getRemoteAddress().toString(), ex);
                }
            }
        });
    }

    public void broadcastToRoomID(int roomID, String eventName, Object message) {
        String finalMessage = con.toJSON(new EventObject(eventName, message)).toString();

        try {
            this.clients.forEach((client, roomid) -> {
                if (roomID == roomid) {
                    if (client.isOpen()) {
                        try {
                            client.getRemote().sendString(finalMessage);
                        } catch (IOException ex) {
                            con.log(area, "Nao foi possivel mandar mensagem a cliente: " + client.getRemoteAddress().toString(), ex);
                        }
                    }
                }
            });
        } catch (Exception e) {
            System.out.println("Nao foi possivel mandar mensagem");
        }
    }

    public void sendMessage(Session client, String message) {

        try {
            client.getRemote().sendString(message);
        } catch (IOException ex) {
            Logger.getLogger(WebSocketController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void sendMessage(Session client, String eventName, Object message) {
        String finalMessage = con.toJSON(new EventObject(eventName, message)).toString();

        try {
            client.getRemote().sendString(finalMessage);
        } catch (IOException ex) {
            Logger.getLogger(WebSocketController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void updateLastEntry(int roomID, LastEntryObject lastEntry) {
        this.broadcastToRoomID(roomID, "last_entry", lastEntry);
    }

    public void updateDoorOpenedCounter(int roomID, DoorOpenedObject ob) {
        this.broadcastToRoomID(roomID, "dooropened_counter", ob);
    }

    public void sendLastScan(int roomID, LastEntryObject scan) {
        this.broadcastToRoomID(roomID, "last_scan", scan);
    }

    public String getWebCamPicture() {

        return null;
    }

    public void processCommand(Session user, String fullcmd) {
        String cmd = fullcmd.split(";")[0];
        String args = fullcmd.split(";")[1];

        if (isLogged(user)) {
            Connection c = this.getConnection(user);
            if (c.getStatus() == ConnectionStatus.CONNECTED) {
                switch (cmd) {
                    case "o":
                        c.getConnection().getCommandHandler().sendCommand(Command.OPEN_DOOR);
                        break;
                    case "b":
                        c.getConnection().getCommandHandler().sendCommand(Command.BEEP);
                        break;
                    case "l":
                        c.getLightControl().toogleLight(args);
                        break;
                }
            } else {
                this.sendMessage(user, "error", new EventObject("error", "Porta nao esta conectada!"));
            }
        } else {
            if (cmd.equalsIgnoreCase("id")) {
                try {
                    this.logClient(user, Integer.parseInt(args));
                    this.sendWelcomeData(user);
                } catch (Exception e) {
                    con.log(area, "Nao foi possivel identificar pagina do usuario! ID:" + args, e);
                }

            } else {
                this.sendMessage(user, "auth");
            }
        }
    }

    void logClient(Session user, int room) {
        this.clients.put(user, room);
    }

    void removeClient(Session user) {
        this.clients.remove(user);
    }

    boolean isLogged(Session user) {
        return this.clients.keySet().contains(user);
    }

    private Connection getConnection(Session user) {
        return con.getConnectionController().getConnection(this.clients.get(user));
    }

    private void sendWelcomeData(Session user) {
        if (this.clients.containsKey(user)) {
            Connection c = this.getConnection(user);
            if (c.getStatus() == ConnectionStatus.CONNECTED) {
                if (user.isOpen()) {
                    c.getLightControl().broadcastAllToUser(user);
                }
            }
        }
    }

}

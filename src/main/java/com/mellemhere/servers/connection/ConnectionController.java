/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.servers.connection;

import com.mellemhere.main.Controller;
import com.mellemhere.server.websocket.mObjects.RoomObject;
import java.util.HashMap;

/**
 *
 * @author MellemHere
 */
public class ConnectionController {

    private final String area = "CONNECTION-CONTROLER";
    private final Controller con;

    //ROOMID, CONNECTION
    HashMap<Integer, Room> rooms = new HashMap<>();

    public ConnectionController(Controller con) {
        this.con = con;
    }

    public void openAll() {
        con.log(area, "Abrindo todas as conecoes", null);

        con.getMysqlController().getRoomController().getRooms().forEach((room) -> {
            
            Room roomc = new Room(room, this);

            roomc.setStatus(ConnectionStatus.CONNECTING);

            roomc.start();
            
            try {
                
                //STARTS CONNECTION WITH THE DOOR
                SerialConnection serial = new SerialConnection(roomc, room);
                serial.startConnection();
                roomc.setConnection(serial);
                roomc.setStatus(ConnectionStatus.CONNECTED);

            } catch (Exception e) {
                con.log(area, "Nao foi possivel abrir conecao com a porta " + room.getDoorID(), e);
                roomc.setStatus(ConnectionStatus.FAILED);
            }
            this.rooms.put(room.getId(), roomc);
        });
    }

    public void reconnect(RoomObject room) {
        Room connection = new Room(room, this);
        
        connection.setStatus(ConnectionStatus.CONNECTING);

        try {
            SerialConnection serial = new SerialConnection(connection, room);
            serial.startConnection();
            connection.setConnection(serial);
            connection.setStatus(ConnectionStatus.CONNECTED);
        } catch (Exception e) {
            connection.setStatus(ConnectionStatus.FAILED);
        }

        this.rooms.put(room.getId(), connection);
    }

    void log(String area, String string, Exception object) {
        this.con.log(area, string, object);
    }

    public Controller getCon() {
        return con;
    }

    public void setDesconnected(RoomObject room) {
        Room c = this.rooms.get(room.getId());
        c.setStatus(ConnectionStatus.FAILED);
    }

    public Room getConnection(int doorID) {
        for (Room connection : this.rooms.values()) {
            if (connection.getRoom().getDoorID() == doorID) {
                return connection;
            }
        }
        return null;
    }

}

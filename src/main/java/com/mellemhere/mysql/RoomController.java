/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.mysql;

import com.mellemhere.server.websocket.mObjects.RoomObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MellemHere
 */
public class RoomController {

    private final String DB_NAME = "gav_rooms";
    MySQLController con;

    public RoomController(MySQLController con) {
        this.con = con;
    }

    public List<RoomObject> getRooms() {
        List<RoomObject> rooms = new ArrayList<>();

        try {
            ResultSet rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE 1");
            while (rs.next()) {

                RoomObject room = new RoomObject();

                room.setId(rs.getInt("id"));
                room.setDoorID(rs.getInt("doorID"));
                room.setName(rs.getString("name"));
                room.setDescription(rs.getString("description"));
                room.setComID(rs.getString("comID"));
                rooms.add(room);
            }
        } catch (SQLException ex) {
            
            con.getController().log(DB_NAME, "Erro", ex);
            return null;
        }

        return rooms;
    }
    
    
    

    public RoomObject getRoom(String id) {
        return getRoom(Integer.parseInt(id));
    }

    public RoomObject getRoom(int doorID) {

        try {
            ResultSet rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE `doorID`='" + doorID + "'");
            rs.next();

            RoomObject room = new RoomObject();

            room.setId(rs.getInt("id"));
            room.setDoorID(rs.getInt("doorID"));
            room.setName(rs.getString("name"));
            room.setDescription(rs.getString("description"));
            room.setComID(rs.getString("comID"));

            return room;
        } catch (SQLException ex) {
            
            con.getController().log(DB_NAME, "Erro", ex);
            return null;
        }

    }

}

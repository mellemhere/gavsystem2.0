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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MellemHere
 */
public class MySQLRoomController {

    private final String DB_NAME = "gav_rooms";
    MySQLController con;

    public MySQLRoomController(MySQLController con) {
        this.con = con;
    }

    public List<RoomObject> getRooms() {
        List<RoomObject> rooms = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE 1");
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
            con.getController().log(DB_NAME, "Erro1", ex);
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    con.getController().log(DB_NAME, "Erro com getRooms", ex);
                    Logger.getLogger(MySQLUserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return rooms;
    }

    public RoomObject getRoom(String id) {
        return getRoom(Integer.parseInt(id));
    }

    public RoomObject getRoom(int doorID) {
        ResultSet rs = null;
        try {
            rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE `doorID`='" + doorID + "'");
            rs.next();

            RoomObject room = new RoomObject();

            room.setId(rs.getInt("id"));
            room.setDoorID(rs.getInt("doorID"));
            room.setName(rs.getString("name"));
            room.setDescription(rs.getString("description"));
            room.setComID(rs.getString("comID"));

            return room;
        } catch (SQLException ex) {
            con.getController().log(DB_NAME, "Erro2", ex);
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    con.getController().log(DB_NAME, "Erro com getRooms", ex);
                    Logger.getLogger(MySQLUserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

}

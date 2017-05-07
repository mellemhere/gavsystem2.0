/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.mysql;

import com.mellemhere.patrimony.PatrimonyObject;
import com.mellemhere.server.websocket.mObjects.RoomObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author MellemHere
 */
public class MySQLPatrimonyController {

    private final String DB_NAME = "gav_patrimony";
    MySQLController con;

    public MySQLPatrimonyController(MySQLController con) {
        this.con = con;
    }

    public List<PatrimonyObject> getPatrimonies() {

        List<PatrimonyObject> patrimonies = new ArrayList<>();

        ResultSet rs = null;
        try {
            rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE 1");
            while (rs.next()) {

                PatrimonyObject patrimony = new PatrimonyObject();

                patrimony.setId(rs.getInt("id"));
                patrimony.setPatrimony(rs.getString("patrimony"));
                patrimony.setStand(rs.getInt("id"));
                patrimony.setType(rs.getString("type"));
                patrimony.setState(rs.getString("state"));
                patrimony.setLocation(rs.getString("location"));
                patrimony.setRoomID(rs.getInt("roomID"));
                patrimony.setTimeAdded(rs.getInt("timeAdded"));
                patrimony.setFather(rs.getInt("father"));

                patrimonies.add(patrimony);
            }
        } catch (SQLException ex) {
            con.getController().log(DB_NAME, "Erro1", ex);
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    con.getController().log(DB_NAME, "Erro com getPatrimonies", ex);
                    Logger.getLogger(MySQLUserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return patrimonies;
    }

    public PatrimonyObject getPatrimony(String id) {
        return getPatrimony(Integer.parseInt(id));
    }

    public PatrimonyObject getPatrimony(int id) {
        ResultSet rs = null;
        try {
            rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE `id`='" + id + "'");
            rs.next();

            PatrimonyObject patrimony = new PatrimonyObject();

            patrimony.setId(rs.getInt("id"));
            patrimony.setPatrimony(rs.getString("patrimony"));
            patrimony.setStand(rs.getInt("id"));
            patrimony.setType(rs.getString("type"));
            patrimony.setState(rs.getString("state"));
            patrimony.setLocation(rs.getString("location"));
            patrimony.setRoomID(rs.getInt("roomID"));
            patrimony.setTimeAdded(rs.getInt("timeAdded"));
            patrimony.setFather(rs.getInt("father"));

            return patrimony;
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

    public List<PatrimonyObject> getPatrimoniesFromRoom(int roomID) {
        List<PatrimonyObject> patrimonies = new ArrayList<>();

        ResultSet rs = null;
        try {
            rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE `roomID`='" + roomID + "'");
            while (rs.next()) {

                PatrimonyObject patrimony = new PatrimonyObject();

                patrimony.setId(rs.getInt("id"));
                patrimony.setPatrimony(rs.getString("patrimony"));
                patrimony.setStand(rs.getInt("id"));
                patrimony.setType(rs.getString("type"));
                patrimony.setState(rs.getString("state"));
                patrimony.setLocation(rs.getString("location"));
                patrimony.setRoomID(rs.getInt("roomID"));
                patrimony.setTimeAdded(rs.getInt("timeAdded"));
                patrimony.setFather(rs.getInt("father"));

                patrimonies.add(patrimony);
            }
        } catch (SQLException ex) {
            con.getController().log(DB_NAME, "Erro1", ex);
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    con.getController().log(DB_NAME, "Erro com getPatrimonies", ex);
                    Logger.getLogger(MySQLUserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return patrimonies;
    }

    public List<PatrimonyObject> getPatrimoniesWithState(String state) {
        List<PatrimonyObject> patrimonies = new ArrayList<>();

        ResultSet rs = null;
        try {
            rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE `state`='" + state + "'");
            while (rs.next()) {

                PatrimonyObject patrimony = new PatrimonyObject();

                patrimony.setId(rs.getInt("id"));
                patrimony.setPatrimony(rs.getString("patrimony"));
                patrimony.setStand(rs.getInt("id"));
                patrimony.setType(rs.getString("type"));
                patrimony.setState(rs.getString("state"));
                patrimony.setLocation(rs.getString("location"));
                patrimony.setRoomID(rs.getInt("roomID"));
                patrimony.setTimeAdded(rs.getInt("timeAdded"));
                patrimony.setFather(rs.getInt("father"));

                patrimonies.add(patrimony);
            }
        } catch (SQLException ex) {
            con.getController().log(DB_NAME, "Erro1", ex);
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    con.getController().log(DB_NAME, "Erro com getPatrimonies", ex);
                    Logger.getLogger(MySQLUserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return patrimonies;
    }

    public List<PatrimonyObject> getChildPatrimonies(int fatherID) {
        List<PatrimonyObject> patrimonies = new ArrayList<>();

        ResultSet rs = null;
        try {
            rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE `father`='" + fatherID + "'");
            while (rs.next()) {

                PatrimonyObject patrimony = new PatrimonyObject();

                patrimony.setId(rs.getInt("id"));
                patrimony.setPatrimony(rs.getString("patrimony"));
                patrimony.setStand(rs.getInt("id"));
                patrimony.setType(rs.getString("type"));
                patrimony.setState(rs.getString("state"));
                patrimony.setLocation(rs.getString("location"));
                patrimony.setRoomID(rs.getInt("roomID"));
                patrimony.setTimeAdded(rs.getInt("timeAdded"));
                patrimony.setFather(rs.getInt("father"));

                patrimonies.add(patrimony);
            }
        } catch (SQLException ex) {
            con.getController().log(DB_NAME, "Erro1", ex);
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    con.getController().log(DB_NAME, "Erro com getPatrimonies", ex);
                    Logger.getLogger(MySQLUserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return patrimonies;
    }

    public List<PatrimonyObject> search(JSONObject filters) {

        String where = "";

        if (filters.has("query")) {
            if (!filters.getString("query").trim().equalsIgnoreCase("")) {
                where += "`patrimony` LIKE '%" + filters.getString("query") + "%'";
            }
        }

        if (filters.has("select")) {
            if (!filters.getString("select").equalsIgnoreCase("Todas")) {
                if (where.equalsIgnoreCase("")) {
                    where += "`roomID`='" + filters.getString("select") + "'";
                } else {
                    where += " AND `roomID`='" + filters.getString("select") + "'";
                }
            }
        }
        
        if(where.equalsIgnoreCase("")){
            where = "1";
        }
        
        List<PatrimonyObject> patrimonies = new ArrayList<>();

        ResultSet rs = null;
        try {
            System.out.println("SELECT * FROM `" + DB_NAME + "` WHERE " + where);
            rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE " + where);
            while (rs.next()) {

                PatrimonyObject patrimony = new PatrimonyObject();

                patrimony.setId(rs.getInt("id"));
                patrimony.setPatrimony(rs.getString("patrimony"));
                patrimony.setStand(rs.getInt("id"));
                patrimony.setType(rs.getString("type"));
                patrimony.setState(rs.getString("state"));
                patrimony.setLocation(rs.getString("location"));
                patrimony.setRoomID(rs.getInt("roomID"));
                patrimony.setTimeAdded(rs.getInt("timeAdded"));
                patrimony.setFather(rs.getInt("father"));

                patrimonies.add(patrimony);
            }
        } catch (SQLException ex) {
            con.getController().log(DB_NAME, "Erro1", ex);
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    con.getController().log(DB_NAME, "Erro com getPatrimonies", ex);
                    Logger.getLogger(MySQLUserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return patrimonies;

    }

}

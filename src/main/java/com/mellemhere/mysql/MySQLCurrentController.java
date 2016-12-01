/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author MellemHere
 */
public class MySQLCurrentController {

    private final String DB_NAME = "gav_current";

    private final MySQLController con;

    public MySQLCurrentController(MySQLController con) {
        this.con = con;
    }

    public JSONArray getItems() {
        JSONArray data = new JSONArray();
        ResultSet rs = null;
        try {
            //SELECT * FROM pet WHERE name LIKE '%w%';
            rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE 1 ORDER BY time DESC");
            while (rs.next()) {
                JSONObject dataHolder = new JSONObject();
                dataHolder.put("x", rs.getTimestamp("time"));
                dataHolder.put("y", rs.getFloat("current"));
                data.put(dataHolder);
            }
        } catch (SQLException ex) {
            con.getController().log(DB_NAME, "Erro com getItems", ex);
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    con.getController().log(DB_NAME, "Erro com getItems", ex);
                    Logger.getLogger(MySQLUserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return data;
    }

    public JSONObject getItemsBad() {
        JSONObject data = new JSONObject();
        JSONArray x = new JSONArray();

        JSONArray y = new JSONArray();
        ResultSet rs = null;
        try {
            //SELECT * FROM pet WHERE name LIKE '%w%';
            rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE 1 ORDER BY time DESC");
            while (rs.next()) {
                x.put(rs.getTimestamp("time"));
                y.put(rs.getFloat("current"));
            }
        } catch (SQLException ex) {
            con.getController().log(DB_NAME, "Erro com getItems", ex);
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    con.getController().log(DB_NAME, "Erro com getItems", ex);
                    Logger.getLogger(MySQLUserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        data.put("x", x);
        data.put("y", y);
        
        return data;
    }

    public void put(String floats) {
        float data = Float.parseFloat(floats);
        con.update("INSERT INTO `gav_current`(`current`) VALUES ('" + data + "')");
    }

}

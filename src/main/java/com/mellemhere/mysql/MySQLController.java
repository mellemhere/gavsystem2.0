/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.mysql;

import com.mellemhere.main.Controller;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MellemHere
 */
public class MySQLController {

    private final String DB_NAME = "sisgav";
    private final String DB_USER = "root";

    private final Controller con;

    private Connection connection = null;

    //Instances
    private final MySQLUserController userController;
    private final MySQLLogController logController;
    private final MySQLRoomController roomController;
    private final MySQLItemsController itemsController;
    private final MySQLStatisticsController statisticsController;

    private final MySQLCurrentController current;
    
    private final MySQLPatrimonyController patrimonyController;
    
    public MySQLCurrentController getCurrent() {
        return current;
    }

    private Statement stmt;

    public MySQLController(Controller con) {
        this.con = con;

        if (!this.connect()) {
            System.out.println("NAO FOI POSSSIVEL CONECTAR AO SERVIDOR MYSQL");
            System.exit(0);
        } else {
            System.out.println("Conectado..");
        }

        this.userController = new MySQLUserController(this);
        this.logController = new MySQLLogController(this);
        this.roomController = new MySQLRoomController(this);
        this.itemsController = new MySQLItemsController(this);
        this.statisticsController = new MySQLStatisticsController(this);
        this.current = new MySQLCurrentController(this);
        this.patrimonyController = new MySQLPatrimonyController(this);
        
        try {
            this.stmt = this.connection.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(MySQLController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public boolean connect() {
        try {
            System.out.println("Tentando se conectar com o servidor MYSQL");
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost/" + this.DB_NAME + "?"
                    + "user=" + this.DB_USER);
            return true;
        } catch (SQLException ex) {
            con.log("MYSQL", "Erro ao tentar se conectar", ex);
            return false;
        }
    }

    public MySQLItemsController getItemsController() {
        return itemsController;
    }

    public MySQLStatisticsController getStatisticsController() {
        return statisticsController;
    }

    public MySQLRoomController getRoomController() {
        return roomController;
    }

    public Connection getConnection() {
        return connection;
    }

    public Controller getController() {
        return this.con;
    }

    public MySQLUserController getUserController() {
        return userController;
    }

    public MySQLLogController getLogController() {
        return logController;
    }

    public MySQLPatrimonyController getPatrimonyController() {
        return patrimonyController;
    }

    
    
    public ResultSet query(String query) {
        try {
            this.stmt = this.connection.createStatement(); //Memory leak?
            ResultSet rs = stmt.executeQuery(query);
            return rs;
        } catch (SQLException ex) {
            con.log("MYSQL", "Erro", ex);
        }
        return null;
    }

    public void update(String query) {
        try {
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            con.log("MYSQL", "Erro", ex);
        }
    }

    public void delete(String query) {
        try {
            stmt.execute(query);
        } catch (SQLException ex) {
            con.log("MYSQL", "Erro", ex);
        }
    }

    public String makeInsertQuery(String insertINTO, Object classT) {
        Map<String, Object> map = con.objectToMap(classT);
        return this.makeInsertQuery(insertINTO, map);
    }

    public String makeInsertQuery(String insertINTO, Map<String, Object> map) {
        String keys = "", values = "";

        for (Map.Entry<String, Object> entrySet : map.entrySet()) {
            String key = entrySet.getKey();
            Object value = entrySet.getValue();

            if (keys.equalsIgnoreCase("")) {
                keys += "`" + key + "`";
            } else {
                keys += ",`" + key + "`";
            }

            if (values.equalsIgnoreCase("")) {
                values += "'" + value + "'";
            } else {
                values += ",'" + value + "'";
            }

        }

        return "INSERT INTO `" + insertINTO + "`(" + keys + ") VALUES (" + values + ")";
    }

    public String makeUpdateQuery(String updateINTO, Object classT) {
        Map<String, Object> map = con.objectToMap(classT);
        return this.makeUpdateQuery(updateINTO, map);
    }

    public String makeUpdateQuery(String updateINTO, Map<String, Object> map) {
        String set = "", id = "";

        for (Map.Entry<String, Object> entrySet : map.entrySet()) {
            String key = entrySet.getKey();
            Object value = entrySet.getValue();

            if (key.equalsIgnoreCase("id")) {
                id = String.valueOf(value);
            } else {
                if (set.equalsIgnoreCase("")) {
                    set += "`" + key + "`='" + value + "'";
                } else {
                    set += ",`" + key + "`='" + value + "'";
                }
            }

        }

        return "UPDATE `" + updateINTO + "` SET " + set + " WHERE `id`='" + id + "'";
    }

}

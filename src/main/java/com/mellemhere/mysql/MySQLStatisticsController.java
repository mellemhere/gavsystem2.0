package com.mellemhere.mysql;

import com.mellemhere.prop.statistics.StatisticsObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQLStatisticsController {

    private final String DB_NAME = "gav_stats";

    private final MySQLController con;

    public MySQLStatisticsController(MySQLController con) {
        this.con = con;

    }

    public int getInt(int roomID, String key) {
        return Integer.parseInt(this.get(roomID, key).getValue());
    }

    public float getFloat(int roomID, String key) {
        return Float.parseFloat(this.get(roomID, key).getValue());
    }

    public long getLong(int roomID, String key) {
        return Long.parseLong(this.get(roomID, key).getValue());
    }

    public StatisticsObject get(int roomID, String key) {
        StatisticsObject so = new StatisticsObject();
        ResultSet rs = null;

        try {
            rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE `name`='" + key + "' AND `scope`='" + roomID + "'");
            rs.next();
            
            so.setId(rs.getInt("id"));
            so.setName(rs.getString("name"));
            if(rs.getString("name") == null){
                System.out.println("NULLO");
            }
            so.setName(rs.getString("name"));
            so.setScope(rs.getInt("scope"));
            so.setValue(rs.getString("value"));

            return so;
            
        } catch (SQLException ex) {
            con.getController().log(DB_NAME, "Erro1", ex);
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    con.getController().log(DB_NAME, "Erro com get4", ex);
                    Logger.getLogger(MySQLUserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    public void set(int roomID, String key, int value) {
        this.set(roomID, key, String.valueOf(value));
    }

    public void set(int roomID, String key, float value) {
        this.set(roomID, key, String.valueOf(value));
    }

    public void set(int roomID, String key, long value) {
        this.set(roomID, key, String.valueOf(value));
    }

    public void set(int roomID, String key, Object valueInsert) {
        String value;
        
        if (valueInsert instanceof String) {
            value = (String) valueInsert;
        } else {
            value = String.valueOf(valueInsert);
        }

        try {
            if (hasStatistic(roomID, key)) {
                //UPDATE
                StatisticsObject so = this.get(roomID, key);
                so.setValue(value);
                con.update(con.makeUpdateQuery(DB_NAME, so));
            } else {
                //INSERT
                StatisticsObject so = new StatisticsObject();
                so.setName(key);
                so.setValue(value);
                so.setScope(roomID);
                con.update(con.makeInsertQuery(DB_NAME, so));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            con.getController().log(DB_NAME, "Erro2 aqui", ex);
        }
    }

    public boolean hasStatistic(int roomID, String key) {
        ResultSet rs = null;
        try {
            rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE `scope`='" + roomID + "' AND `name`='" + key + "'");
            return rs.next() != false;
        } catch (SQLException ex) {
            con.getController().log(DB_NAME, "Erro com hasUserID3", ex);
            return false;
        }finally{
            if(rs != null){
                try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MySQLStatisticsController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}

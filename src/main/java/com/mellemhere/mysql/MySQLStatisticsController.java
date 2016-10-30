package com.mellemhere.mysql;

import com.mellemhere.prop.statistics.StatisticsObject;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        try {
            ResultSet rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE `name`='" + key + "' AND `scope`='" + roomID + "'");
            rs.next();

            so.setId(rs.getInt("id"));
            so.setName(rs.getString("name"));
            so.setScope(rs.getInt("scope"));
            so.setValue(rs.getString("value"));

            return so;
        } catch (SQLException ex) {
            con.getController().log(DB_NAME, "Erro", ex);

            return null;
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
            con.getController().log(DB_NAME, "Erro", ex);
        }
    }

    public boolean hasStatistic(int roomID, String key) {
        try {
            return con.query("SELECT * FROM `" + DB_NAME + "` WHERE `scope`='" + roomID + "' AND `name`='" + key + "'").next() != false;
        } catch (SQLException ex) {
            con.getController().log(DB_NAME, "Erro com hasUserID", ex);
            return false;
        }
    }

}

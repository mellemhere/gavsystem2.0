package com.mellemhere.mysql;

import com.mellemhere.prop.items.Item;
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
public class MySQLItemsController {

    private final String DB_NAME = "gav_items";

    private final MySQLController con;

    public MySQLItemsController(MySQLController con) {
        this.con = con;
        con.getRoomController().getRoom(1);

    }

    public Item queryToObject(ResultSet rs) {
        Item item = new Item();
        try {
            
            item.setId(rs.getInt("id"));
            item.setName(rs.getString("name"));
            item.setQRTags(rs.getString("barCode"));
            item.setPatrimony(rs.getString("patrimony"));
            item.setStateID(rs.getInt("state"));
            item.setRoomID(rs.getInt("room"));
            item.setComment(rs.getString("comment"));

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return item;
    }

    public List<Item> getItems(String query, int limit) {
        List<Item> items = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE `name` LIKE  '%" + query + "%' LIMIT " + limit);
            while (rs.next()) {
                Item item = queryToObject(rs);
                items.add(item);
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

        return items;
    }

    public List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE 1");

            while (rs.next()) {
                Item item = queryToObject(rs);
                items.add(item);
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

        return items;
    }
   
    
}

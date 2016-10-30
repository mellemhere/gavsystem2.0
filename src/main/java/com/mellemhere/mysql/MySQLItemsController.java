package com.mellemhere.mysql;

import com.mellemhere.server.websocket.mObjects.ItemObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author MellemHere
 */
public class MySQLItemsController {

    private final String DB_NAME = "gav_items";

    private final MySQLController con;

    public MySQLItemsController(MySQLController con) {
        this.con = con;

    }

    public JSONArray getItems(String query) {
        JSONArray items = new JSONArray();

        try {
            //SELECT * FROM pet WHERE name LIKE '%w%';
            ResultSet rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE `NOME` LIKE  '%" + query + "%' LIMIT 30");
            while (rs.next()) {
                JSONObject item = new JSONObject();
                item.put("name", rs.getString("NOME"));
                item.put("area", rs.getString("AREA"));
                item.put("box", rs.getString("CAIXA"));
                items.put(item);
            }
        } catch (SQLException ex) {

            con.getController().log(DB_NAME, "Erro com getItems", ex);
            return null;
        }

        return items;
    }

    public List<ItemObject> getItems() {
        List<ItemObject> items = new ArrayList<>();

        try {
            //SELECT * FROM pet WHERE name LIKE '%w%';
            ResultSet rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE 1");
            while (rs.next()) {

                ItemObject item = new ItemObject();

                item.setName(rs.getString("NOME"));
                item.setArea(rs.getString("AREA"));
                item.setBox(rs.getString("CAIXA"));
                items.add(item);
            }
        } catch (SQLException ex) {

            con.getController().log(DB_NAME, "Erro com getItems", ex);
            return null;
        }

        return items;
    }

}

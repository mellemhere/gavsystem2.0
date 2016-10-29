package com.mellemhere.mysql;

import com.mellemhere.server.websocket.mObjects.UserObject;
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
public class ItemsController {

    private final String DB_NAME = "gav_items";

    private final MySQLController con;

    public ItemsController(MySQLController con) {
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

}

package com.mellemhere.mysql;

import com.mellemhere.util.PasswordManager;
import com.mellemhere.server.websocket.mObjects.UserObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author MellemHere
 */
public class UserController {

    private final String DB_NAME = "gav_users";

    private final MySQLController con;

    private final PasswordManager pw;

    public UserController(MySQLController con) {
        this.con = con;
        this.pw = new PasswordManager();
    }

    public boolean hasUserUID(String UID) {
        return this.hasUserUID(Integer.parseInt(UID));
    }

    public boolean hasUserUID(int UID) {
        try {
            return con.query("SELECT * FROM `" + DB_NAME + "` WHERE `uID`='" + UID + "'").next() != false;
        } catch (SQLException ex) {
            return false;
        }
    }

    public boolean hasUserMID(int MID) {
        try {
            return con.query("SELECT * FROM `" + DB_NAME + "` WHERE `mID`='" + MID + "'").next() != false;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public List<UserObject> getUsers() {
        List<UserObject> users = new ArrayList<>();

        try {
            ResultSet rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE 1");
            while (rs.next()) {
                UserObject user = new UserObject();
                user.setID(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setmID(rs.getInt("mID"));
                user.setuID(rs.getInt("uID"));
                user.setPassword(rs.getString("password"));
                user.setLevel(rs.getInt("level"));
                user.setConfig(rs.getString("config"));

                users.add(user);
            }
        } catch (SQLException ex) {
            return null;
        }

        return users;
    }

    public UserObject[] getUsersFrom(int doorID) {
        return null;
    }

    public UserObject getUserByMID(String MID) {
        return this.getUserByMID(Integer.parseInt(MID));
    }

    public UserObject getUserByMID(int MID) {
        UserObject user = new UserObject();

        try {
            ResultSet rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE `mid`='" + MID + "'");
            rs.next();
            user.setID(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setmID(rs.getInt("mID"));
            user.setuID(rs.getInt("uID"));
            user.setPassword(rs.getString("password"));
            user.setLevel(rs.getInt("level"));
            user.setConfig(rs.getString("config"));
        } catch (SQLException ex) {
            return null;
        }

        return user;
    }

    public UserObject getUserByID(String ID) {
        return this.getUserByID(Integer.parseInt(ID));
    }

    public UserObject getUserByID(int ID) {
        UserObject user = new UserObject();

        try {
            ResultSet rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE `id`='" + ID + "'");
            rs.next();
            user.setID(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setmID(rs.getInt("mID"));
            user.setuID(rs.getInt("uID"));
            user.setPassword(rs.getString("password"));
            user.setLevel(rs.getInt("level"));
            user.setConfig(rs.getString("config"));
        } catch (SQLException ex) {
            return null;
        }

        return user;
    }

    public UserObject getUserByUID(int UID) {
        UserObject user = new UserObject();

        try {

            ResultSet rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE `uid`='" + UID + "'");
            rs.next();

            user.setID(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setmID(rs.getInt("mid"));
            user.setuID(rs.getInt("uid"));
            user.setPassword(rs.getString("password"));
            user.setLevel(rs.getInt("level"));
            user.setConfig(rs.getString("config"));

        } catch (SQLException ex) {
            return null;
        }

        return user;
    }

    public void deleteUser(int id) {
        con.update("DELETE FROM `" + DB_NAME + "` WHERE `id`='" + id + "'");
    }

    public void updateUser(UserObject user) {
        con.update(con.makeUpdateQuery(DB_NAME, user));
    }

    public void saveUser(UserObject user) {
        con.update(con.makeInsertQuery(DB_NAME, user));
    }

    public boolean checkCredentials(String mID, String password) {
        int mid = Integer.parseInt(mID);
        if (hasUserMID(mid)) {
            return PasswordManager.check(password, getUserByMID(mid).getPassword());
        }
        return false;
    }

}

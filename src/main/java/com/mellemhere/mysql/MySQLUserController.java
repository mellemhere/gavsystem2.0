package com.mellemhere.mysql;

import com.mellemhere.util.PasswordManager;
import com.mellemhere.server.websocket.mObjects.UserObject;
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
public class MySQLUserController {

    private final String DB_NAME = "gav_users";

    private final MySQLController con;

    private final PasswordManager pw;

    public MySQLUserController(MySQLController con) {
        this.con = con;
        this.pw = new PasswordManager();
    }

    public boolean hasUserUID(String UID) {
        return this.hasUserUID(Long.parseLong(UID));
    }

    public boolean hasUserUID(long UID) {
        try {
            return con.query("SELECT * FROM `" + DB_NAME + "` WHERE `uid`='" + UID + "'").next() != false;
        } catch (SQLException ex) {
            con.getController().log(DB_NAME, "Erro com hasUserID", ex);
            return false;
        }
    }

    public boolean hasUserMID(long MID) {
        try {
            return con.query("SELECT * FROM `" + DB_NAME + "` WHERE `mid`='" + MID + "'").next() != false;
        } catch (SQLException ex) {
            con.getController().log(DB_NAME, "Erro com hasUserMID", ex);
            return false;
        }
    }

    public List<UserObject> getUsers() {
        List<UserObject> users = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE 1");
            while (rs.next()) {
                UserObject user = new UserObject();
                user.setID(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setmID(rs.getLong("mid"));
                user.setuID(rs.getLong("uid"));
                user.setPassword(rs.getString("password"));
                user.setLevel(rs.getInt("level"));
                user.setConfig(rs.getString("config"));

                users.add(user);
            }
        } catch (SQLException ex) {

            con.getController().log(DB_NAME, "Erro com getUsers", ex);
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    con.getController().log(DB_NAME, "Erro com getUsers", ex);
                    Logger.getLogger(MySQLUserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return users;
    }

    public UserObject[] getUsersFrom(int doorID) {
        return null;
    }

    public UserObject getUserByMID(String MID) {
        return this.getUserByMID(Integer.parseInt(MID));
    }

    public UserObject getUserByMID(long MID) {
        UserObject user = new UserObject();
        ResultSet rs = null;
        try {
            rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE `mid`='" + MID + "'");
            rs.next();
            user.setID(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setmID(rs.getLong("mid"));
            user.setuID(rs.getLong("uid"));
            user.setPassword(rs.getString("password"));
            user.setLevel(rs.getInt("level"));
            user.setConfig(rs.getString("config"));
        } catch (SQLException ex) {
            con.getController().log(DB_NAME, "Erro com getUsersMID", ex);
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    con.getController().log(DB_NAME, "Erro com getUserByMID", ex);
                    Logger.getLogger(MySQLUserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return user;
    }

    public UserObject getUserByID(String ID) {
        return this.getUserByID(Integer.parseInt(ID));
    }

    public UserObject getUserByID(int ID) {
        UserObject user = new UserObject();
        ResultSet rs = null;
        try {
            rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE `id`='" + ID + "'");
            rs.next();
            user.setID(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setmID(rs.getLong("mid"));
            user.setuID(rs.getLong("uid"));
            user.setPassword(rs.getString("password"));
            user.setLevel(rs.getInt("level"));
            user.setConfig(rs.getString("config"));
        } catch (SQLException ex) {

            con.getController().log(DB_NAME, "Erro com getUsersID", ex);
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    con.getController().log(DB_NAME, "Erro com getUserByMID", ex);
                    Logger.getLogger(MySQLUserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return user;
    }

    public UserObject getUserByUID(int UID) {
        UserObject user = new UserObject();
        ResultSet rs = null;
        try {

            rs = con.query("SELECT * FROM `" + DB_NAME + "` WHERE `uid`='" + UID + "'");
            rs.next();

            user.setID(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setmID(rs.getLong("mid"));
            user.setuID(rs.getLong("uid"));
            user.setPassword(rs.getString("password"));
            user.setLevel(rs.getInt("level"));
            user.setConfig(rs.getString("config"));

        } catch (SQLException ex) {

            con.getController().log(DB_NAME, "Erro", ex);
            return null;
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    con.getController().log(DB_NAME, "Erro com getUserByUID", ex);
                    Logger.getLogger(MySQLUserController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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

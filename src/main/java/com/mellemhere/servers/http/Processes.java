/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.servers.http;

import com.mellemhere.util.PasswordManager;
import com.mellemhere.main.Controller;
import com.mellemhere.server.websocket.mObjects.UserObject;
import org.json.JSONObject;
import spark.Request;

/**
 *
 * @author MellemHere
 */
public class Processes {

    private static final String[] userFilds = {"name", "mid", "uid", "func"};

    public static boolean processNewUser(Controller con, Request request) throws Exception {

        for (String key : userFilds) {
            if (!request.queryParams().contains(key)) {
                throw new Exception("Preencha o campo: " + key);
            }
        }

        UserObject newUser = new UserObject();

        newUser.setName(request.queryParams("name"));
        newUser.setmID(Integer.parseInt(request.queryParams("mid")));
        newUser.setuID(Long.parseLong(request.queryParams("uid")));
        newUser.setLevel(Integer.parseInt(request.queryParams("func")));

        JSONObject config = new JSONObject();

        if (request.queryParams().contains("canpainel")) {
            config.put("painel_access", (Boolean.valueOf(request.queryParams("canpainel"))));
        } else {
            config.put("painel_access", false);
        }

        newUser.setConfig(config.toString());

        con.getMysqlController().getUserController().saveUser(newUser);

        return true;
    }

    public static boolean processUpdateUser(Controller con, Request request) throws Exception {

        for (String key : userFilds) {
            if (!request.queryParams().contains(key)) {
                throw new Exception("Preencha o campo: " + key);
            }
        }

        UserObject newUser = con.getMysqlController().getUserController().getUserByID(request.queryParams("id"));

        newUser.setID(Integer.parseInt(request.queryParams("id")));
        newUser.setName(request.queryParams("name"));
        newUser.setmID(Integer.parseInt(request.queryParams("mid")));
        newUser.setuID(Long.parseLong(request.queryParams("uid")));
        newUser.setLevel(Integer.parseInt(request.queryParams("func")));

        JSONObject config = new JSONObject(newUser.getConfig());

        if (request.queryParams().contains("canpainel")) {
            config.put("painel_access", (Boolean.valueOf(request.queryParams("canpainel"))));
        } else {
            config.put("painel_access", false);
        }

        newUser.setConfig(config.toString());

        con.getMysqlController().getUserController().updateUser(newUser);

        return true;
    }

    public static boolean processPasswordReset(Controller con, Request request, UserObject user) throws Exception {

        if (!request.queryParams().contains("password")) {
            throw new Exception("Preencha o campo: password");
        }

        user.setPassword(PasswordManager.getSaltedHash(request.queryParams("password")));

        con.getMysqlController().getUserController().updateUser(user);

        return true;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.servers.http;

import com.mellemhere.main.Controller;
import com.mellemhere.servers.websocket.WebSocketController;
import com.mellemhere.servers.websocket.WebSocketHandler;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import spark.Response;
import spark.Session;
import static spark.Spark.*;

import java.util.HashMap;
import java.util.Map;
import spark.Request;
import static spark.Spark.get;

/**
 *
 * @author MellemHere
 */
public class HTTPController {

    private final int HTTP_PORT = 4567;

    private final String RESOURCES_FOLDER = "D:/Games/GAVSystem2.0/files";

    private final String area = "HTTPSERVER";

    private Controller con;

    HashMap<String, Integer> loggedClients = new HashMap<>();

    Configuration config = new Configuration();

    private WebSocketController wcon;

    public HTTPController(Controller con, WebSocketController wcon) {
        this.con = con;
        this.wcon = wcon;
        
        port(HTTP_PORT);
        
        staticFiles.externalLocation(RESOURCES_FOLDER);
        try {
            config.setDirectoryForTemplateLoading(new File(RESOURCES_FOLDER));
        } catch (IOException ex) {
            this.con.log(area, "Pasta de arquivos nao encontrada", ex);
        }
    }

    public Template getTemplate(String name) {
        try {
            return config.getTemplate("/templates/" + name + ".html");
        } catch (Exception e) {
            this.con.log(area, "Template nao encontrado", e);
        }
        return null;
    }

    public String getHTML(Request r, String name) {
        Template tmp = this.getTemplate(name);
        StringWriter sw = new StringWriter();
        try {
            tmp.process(getMap(r), sw);
        } catch (Exception ex) {
            this.con.log(area, "Falha ao carregar template", ex);
        }
        return sw.toString();
    }

    public void open() {

        WebSocketHandler wb = new WebSocketHandler();
        wb.passController(this.wcon);

        webSocket("/room", wb);

        get("/", (request, response) -> {
            response.redirect("/painel");
            return "";
        });

        /*
            
         LOGIN
        
         */
        get("/entrar", (request, response) -> {

            if (isLogged(request.session().id())) {
                response.redirect("/painel");
            }

            return getHTML(request, "login");
        });

        post("/entrar", (request, response) -> {

            if (isLogged(request.session().id())) {
                response.redirect("/painel");
            }

            this.addToMap(request, "error", true);
            this.addToMap(request, "errortextmain", "Credenciais inválidas");
            this.addToMap(request, "errortextsecond", "tente novamente");

            if (request.queryParams().contains("login") && request.queryParams().contains("password")) {

                String password = request.queryParams("password");
                String login = request.queryParams("login");

                if (!password.equalsIgnoreCase("") && !login.equalsIgnoreCase("")) {
                    if (login(login, password, request.session(true))) {
                        response.redirect("/painel");
                    }
                } else if (password.equalsIgnoreCase("")) {
                    if (con.getMysqlController().getUserController().getUserByMID(login).getPassword().equalsIgnoreCase(" ")) {
                        forceLogin(login, request.session());
                        response.redirect("/resetar/senha");
                    }
                }

            }

            return getHTML(request, "login");
        });

        get("/resetar/senha", (request, response) -> {
            loggingNeeded(request.session().id(), response);
            return getHTML(request, "resetPassword");
        });

        post("/resetar/senha", (request, response) -> {
            loggingNeeded(request.session().id(), response);
            return getHTML(request, "resetPassword");
        });

        get("/sair", (request, response) -> {
            loggingNeeded(request.session().id(), response);
            logout(request.session());
            response.redirect("/entrar");
            return "";
        });

        /*
        
         PAINEL
        
         */
        //INDEX
        get("/painel", (request, response) -> {
            loggingNeeded(request.session().id(), response);
            response.redirect("/painel/208");
            return "";

        });

        //ROOM SITE
        get("/painel/:room", (request, response) -> {
            loggingNeeded(request.session().id(), response);
            this.addAllToMap(request, con.objectToMap(con.getMysqlController().getUserController().getUserByMID(getSessionmID(request.session()))));

            this.addAllToMap(request, con.addObjectAsChildMap(this.getMap(request),
                    "room",
                    con.getMysqlController().getRoomController().getRoom(Integer.parseInt(request.params(":room")))));

            this.addAllToMap(request, con.addObjectAsChildMap(this.getMap(request),
                    "rooms",
                    con.getMysqlController().getRoomController().getRooms().toArray()));

            return getHTML(request, "roomPainel");
        });

        //STATS
        get("/painel/:room/status", (request, response) -> {
            loggingNeeded(request.session().id(), response);
            return getHTML(request, "roomPainel");
        });

        //User list
        get("/painel/:room/lista-de-usuarios", (request, response) -> {
            loggingNeeded(request.session().id(), response);
            return getHTML(request, "roomPainel");
        });

        get("/painel/:room/agenda", (request, response) -> {
            loggingNeeded(request.session().id(), response);
            return getHTML(request, "painel");
        });

        get("/painel/:room/config", (request, response) -> {
            loggingNeeded(request.session().id(), response);
            this.addAllToMap(request, con.objectToMap(con.getMysqlController().getUserController().getUserByMID(getSessionmID(request.session()))));

            this.addAllToMap(request, con.addObjectAsChildMap(this.getMap(request),
                    "room",
                    con.getMysqlController().getRoomController().getRoom(Integer.parseInt(request.params(":room")))));

            this.addAllToMap(request, con.addObjectAsChildMap(this.getMap(request),
                    "rooms",
                    con.getMysqlController().getRoomController().getRooms().toArray()));

            return getHTML(request, "roomConfig");
        });
        /*
        
         USERS
        
         */
        //create
        get("/painel/sistema/novo-usuario", (request, response) -> {
            loggingNeeded(request.session().id(), response);

            this.addAllToMap(request, con.objectToMap(con.getMysqlController().getUserController().getUserByMID(getSessionmID(request.session()))));

            this.addToMap(request, "isEdit", false);

            this.addAllToMap(request, con.addObjectAsChildMap(this.getMap(request),
                    "rooms",
                    con.getMysqlController().getRoomController().getRooms().toArray()));

            return getHTML(request, "system/useredit");
        });

        get("/painel/sistema/deletar-usuario/:id", (request, response) -> {
            loggingNeeded(request.session().id(), response);

            con.getMysqlController().getUserController().deleteUser(Integer.parseInt(request.queryParams("id")));

            response.redirect("/painel/sistema/usuarios");
            return "";
        });

        post("/painel/sistema/novo-usuario", (request, response) -> {
            loggingNeeded(request.session().id(), response);

            try {
                Processes.processNewUser(con, request);
                response.redirect("/painel/sistema/usuarios");
                return "";
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return "Ocorreu um erro";
            }

        });

        //edit
        get("/painel/sistema/novo-usuario/:user-id", (request, response) -> {
            loggingNeeded(request.session().id(), response);

            this.addAllToMap(request, con.objectToMap(con.getMysqlController().getUserController().getUserByMID(getSessionmID(request.session()))));

            this.addAllToMap(request, con.addObjectAsChildMap(this.getMap(request),
                    "rooms",
                    con.getMysqlController().getRoomController().getRooms().toArray()));

            this.addToMap(request, "isEdit", true);

            this.addAllToMap(request, con.addObjectAsChildMap(this.getMap(request),
                    "user",
                    con.getMysqlController().getUserController().getUserByID(request.params(":user-id"))));

            return getHTML(request, "system/useredit");
        });

        //edit
        post("/painel/sistema/novo-usuario/:user-id", (request, response) -> {
            loggingNeeded(request.session().id(), response);

            try {
                Processes.processUpdateUser(con, request);
                response.redirect("/painel/sistema/usuarios");
                return "";
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return "Ocorreu um erro";
            }
        });

        get("/painel/sistema/usuarios", (request, response) -> {
            loggingNeeded(request.session().id(), response);

            this.addAllToMap(request, con.objectToMap(con.getMysqlController().getUserController().getUserByMID(getSessionmID(request.session()))));

            this.addAllToMap(request, con.addObjectAsChildMap(this.getMap(request),
                    "rooms",
                    con.getMysqlController().getRoomController().getRooms().toArray()));

            this.addAllToMap(request, con.addObjectAsChildMap(this.getMap(request),
                    "users",
                    con.getMysqlController().getUserController().getUsers().toArray()));

            return getHTML(request, "system/userlist");
        });

        get("/painel/sistema/config", (request, response) -> {
            this.resetMap(request);

            return "";
        });

        post("/painel/novo-usuario", (request, response) -> {
            loggingNeeded(request.session().id(), response);
            return getHTML(request, "painel");
        });

    }

    public Map<String, Object> getUserMap(int userID) {
        return null;
    }

    public Map<String, Object> getRoomMap(int userID) {
        return null;
    }

    private Map getMap(Request request) {
        if (request.attributes().contains("info_map")) {
            return request.attribute("info_map");
        } else {
            return new HashMap<>();
        }
    }

    private void setMap(Request request, Map map) {
        request.attribute("info_map", map);
    }

    private void addAllToMap(Request request, Map ob) {
        Map map = this.getMap(request);
        map.putAll(ob);
        this.setMap(request, map);
    }

    private void addToMap(Request request, String key, Object value) {
        Map map = this.getMap(request);
        map.put(key, value);
        this.setMap(request, map);
    }

    /*
        
     Session manager
    
     */
    public boolean isLogged(String id) {
        return loggedClients.containsKey(id);
    }

    public int getSessionmID(Session session) {
        return this.loggedClients.get(session.id());
    }

    public void logout(Session client) {
        this.loggedClients.remove(client.id());
    }

    public void loggingNeeded(String id, Response responce) {
        if (!isLogged(id)) {
            responce.redirect("/entrar");
        }
    }

    public void resetMap(Request request) {
        request.attributes().remove("info_map");
    }

    public boolean hasUserIP() {
        return false;
    }

    public boolean login(String login, String password, Session client) {
        if (this.con.getMysqlController().getUserController().checkCredentials(login, password)) {
            this.loggedClients.put(client.id(), this.con.getMysqlController().getUserController().getUserByMID(login).getmID());
            return true;
        }
        return false;
    }

    private void forceLogin(String login, Session client) {
        this.loggedClients.put(client.id(), this.con.getMysqlController().getUserController().getUserByMID(login).getmID());
    }

}

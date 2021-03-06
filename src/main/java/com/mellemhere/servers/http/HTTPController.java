/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.servers.http;

import com.fazecast.jSerialComm.SerialPort;
import com.mellemhere.main.Controller;
import com.mellemhere.servers.websocket.RealTimeData;
import com.mellemhere.servers.websocket.WebSocketController;
import com.mellemhere.servers.websocket.WebSocketHandler;
import com.mellemhere.stickers.Sticker;
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
import org.json.JSONObject;
import spark.Request;
import static spark.Spark.get;

/**
 *
 * @author MellemHere
 */
public class HTTPController {

    private final int HTTP_PORT = 80;
    private final int DEBUG_HTTP_PORT = 8080;

    private final String DEBUG_RESOURCES_FOLDER = "C:\\files\\";
    private final String RESOURCES_FOLDER = "C:\\files\\";

    private final String area = "HTTPSERVER";

    private Controller con;

    HashMap<String, Long> loggedClients = new HashMap<>();

    Configuration config = new Configuration();

    private WebSocketController wcon;

    public HTTPController(Controller con, WebSocketController wcon) {
        this.con = con;
        this.wcon = wcon;
        File dir;

        if (con.debug) {
            port(DEBUG_HTTP_PORT);
            dir = new File(DEBUG_RESOURCES_FOLDER);
            staticFiles.externalLocation(DEBUG_RESOURCES_FOLDER);
        } else {
            port(HTTP_PORT);
            dir = new File(RESOURCES_FOLDER);
            staticFiles.externalLocation(RESOURCES_FOLDER);
        }

        if (!dir.exists()) {
            System.out.println("A PASTAO RESOURCES NAO EXISTE :(");
            dir.mkdir();
            System.out.println(dir.getAbsolutePath());
            System.exit(0);
        } else {
            con.log(area, "Local dos arquivos: " + dir.getAbsolutePath(), null);
        }

        try {
            config.setDirectoryForTemplateLoading(dir);
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
            loggingNeeded(request.session().id(), response);
            GenerateMap.defaultMap(request, this);
            GenerateMap.allRoomsWithArgMap(request, this);
            return getHTML(request, "index");
        });

        get("/patrimonio", (request, response) -> {
            loggingNeeded(request.session().id(), response);

            GenerateMap.defaultMap(request, this);
            GenerateMap.allRoomsWithArgMap(request, this);
            GenerateMap.patrimonyMap(request, this);
            return getHTML(request, "system/items/listpatrimony");
        });

        post("/patrimonio/novo", (request, response) -> {
            loggingNeeded(request.session().id(), response);
            if (request.queryParams().contains("data")) {

            }
            return "oi";
        });

        post("/patrimonio/pesquisa", (request, response) -> {
            loggingNeeded(request.session().id(), response);
            addAllToMap(request, getCon().addObjectAsChildMap(getMap(request),
                    "patrimonies",
                    getCon().getMysqlController().getPatrimonyController().search(new JSONObject(request.queryParams("data"))).toArray()));

            GenerateMap.allRoomsWithArgMap(request, this);

            return getHTML(request, "system/items/tablepatrimonies");
        });

        get("/etiqueta", (request, response) -> {
            loggingNeeded(request.session().id(), response);

            GenerateMap.defaultMap(request, this);
            GenerateMap.allRoomsWithArgMap(request, this);

            return getHTML(request, "system/items/newSticker");
        });

        post("/etiqueta", (request, response) -> {

            loggingNeeded(request.session().id(), response);

            //INICIA CLASSE ETIQUETA COM TAMANHO 788 x 300
            Sticker sticker = new Sticker(788, 300);

            /*
             NOME DA SALA
             */
            if (request.queryParams().contains("name")) {
                String query = request.queryParams("name");
                sticker.setName(query);
            } else {
                sticker.setName("");
            }

            /*
             CODIGO ABAIXO DO NOME
             */
            if (request.queryParams().contains("sid")) {
                String query = request.queryParams("sid");
                if (query.equalsIgnoreCase("")) {

                    sticker.setCode("");
                } else {

                    sticker.setCode("UPLABELET03006");
                }
            } else {
                sticker.setCode("");
            }


            /*
             CODIGO A SER ESCANEADO PELO QR CODE
             */
            sticker.setQRCode("{TESTE TESTE}");


            /*
             DEFINE O TEXTO EM BAIXO
             */
            if (request.queryParams().contains("infratext")) {
                String query = request.queryParams("infratext");
                sticker.setUnderText(query);
            } else {
                sticker.setUnderText("");
            }

            return RealTimeData.encodeToString(sticker.getSticker());

        });

        /*
            
         LOGIN
        
         */
        get("/entrar", (request, response) -> {

            if (isLogged(request.session().id())) {
                response.redirect("/");
            }

            return getHTML(request, "login");
        });

        post("/entrar", (request, response) -> {

            if (isLogged(request.session().id())) {
                response.redirect("/");
            }

            this.addToMap(request, "error", true);
            this.addToMap(request, "errortextmain", "Credenciais inválidas");
            this.addToMap(request, "errortextsecond", "tente novamente");

            if (request.queryParams().contains("login")) {
                if (request.queryParams().contains("password")) {
                    String password = request.queryParams("password");
                    String login = request.queryParams("login");
                    long mid;
                    try {
                        mid = Long.valueOf(login);
                    } catch (Exception e) {
                        return getHTML(request, "login");
                    }
                    
                    if (!login.trim().equalsIgnoreCase("")) {

                        if (con.getMysqlController().getUserController().hasUserMID(mid)) {
                            if (!password.trim().equalsIgnoreCase("")) {
                                System.out.println("HERE");
                                if (login(login, password, request.session(true))) {
                                    System.out.println("HERE1");
                                    response.redirect("/painel");
                                }
                            } else {
                                if (con.getMysqlController().getUserController().getUserByMID(login).getPassword().equalsIgnoreCase(" ")) {
                                    forceLogin(login, request.session());
                                    response.redirect("/resetar/senha");
                                }
                            }
                        }

                    }

                }
            }

            return getHTML(request, "login");
        });

        post("/items", (request, response) -> {

            loggingNeeded(request.session().id(), response);

            if (request.queryParams().contains("query")) {
                String query = request.queryParams("query");

                return con.getMysqlController().getItemsController().getItems(query, 30).toString();
            }

            return getHTML(request, "login");
        });

        get("/sair", (request, response) -> {
            loggingNeeded(request.session().id(), response);
            logout(request.session());
            response.redirect("/entrar");
            return "";
        });

        get("/current", (request, response) -> {
            return con.getConnectionController().getCon().getMysqlController().getCurrent().getItemsBad().toString();
        });

        /*
        
         PAINEL
        
         */
        //INDEX
        get("/painel", (request, response) -> {
            loggingNeeded(request.session().id(), response);
            response.redirect("/");
            return "";

        });

        //ROOM SITE
        get("/painel/:room", (request, response) -> {
            loggingNeeded(request.session().id(), response);

            GenerateMap.defaultMap(request, this);
            GenerateMap.allRoomsWithArgMap(request, this);

            return getHTML(request, "room/roomPainel");
        });

        //STATS
        get("/painel/:room/status", (request, response) -> {
            loggingNeeded(request.session().id(), response);

            GenerateMap.defaultMap(request, this);
            GenerateMap.allRoomsWithArgMap(request, this);

            return getHTML(request, "room/roomStats");
        });

        //User list
        get("/painel/:room/lista-de-usuarios", (request, response) -> {
            loggingNeeded(request.session().id(), response);
            return getHTML(request, "room/roomPainel");
        });

        get("/painel/:room/agenda", (request, response) -> {
            loggingNeeded(request.session().id(), response);

            GenerateMap.defaultMap(request, this);
            GenerateMap.allRoomsWithArgMap(request, this);

            return getHTML(request, "room/roomCalendar");
        });

        get("/painel/:room/config", (request, response) -> {
            loggingNeeded(request.session().id(), response);

            GenerateMap.defaultMap(request, this);
            GenerateMap.allRoomsWithArgMap(request, this);

            this.addToMap(request, "isEdit", true);

            this.addToMap(request, "com", SerialPort.getCommPorts());

            return getHTML(request, "room/roomConfig");
        });

        get("/painel/sistema/nova-sala", (request, response) -> {
            loggingNeeded(request.session().id(), response);

            GenerateMap.defaultMap(request, this);
            GenerateMap.allRoomsMap(request, this);

            this.addToMap(request, "isEdit", false);

            this.addToMap(request, "com", SerialPort.getCommPorts());

            return getHTML(request, "room/roomConfig");
        });

        post("/painel/sistema/nova-sala", (request, response) -> {
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

        /*
        
         USERS
        
         */
        //create
        get("/painel/sistema/novo-usuario", (request, response) -> {
            loggingNeeded(request.session().id(), response);

            GenerateMap.defaultMap(request, this);
            GenerateMap.allRoomsMap(request, this);
            this.addToMap(request, "isEdit", false);

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

            GenerateMap.defaultMap(request, this);
            GenerateMap.allRoomsMap(request, this);
            GenerateMap.addUserCustomMap(request, this, "user", request.params(":user-id"));

            this.addToMap(request, "isEdit", true);

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

        get("/resetar/senha", (request, response) -> {
            loggingNeeded(request.session().id(), response);
            return getHTML(request, "resetPassword");
        });

        post("/resetar/senha", (request, response) -> {
            loggingNeeded(request.session().id(), response);
            try {
                Processes.processPasswordReset(con, request,
                        con.getMysqlController().getUserController().getUserByMID(getSessionmID(request.session())));
                response.redirect("/painel");
                return "";
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return "Ocorreu um erro";
            }
        });

        get("/painel/sistema/usuarios", (request, response) -> {
            loggingNeeded(request.session().id(), response);

            GenerateMap.defaultMap(request, this);
            GenerateMap.allRoomsMap(request, this);

            this.addAllToMap(request, con.addObjectAsChildMap(this.getMap(request),
                    "users",
                    con.getMysqlController().getUserController().getUsers().toArray()));

            return getHTML(request, "system/userlist");
        });

        get("/painel/sistema/items", (request, response) -> {
            loggingNeeded(request.session().id(), response);

            GenerateMap.defaultMap(request, this);
            GenerateMap.allRoomsMap(request, this);

            this.addAllToMap(request, con.addObjectAsChildMap(this.getMap(request),
                    "items",
                    con.getMysqlController().getItemsController().getItems().toArray()));

            return getHTML(request, "system/items");
        });

        get("/painel/sistema/salas", (request, response) -> {
            loggingNeeded(request.session().id(), response);

            GenerateMap.defaultMap(request, this);
            GenerateMap.allRoomsMap(request, this);

            return getHTML(request, "system/roomlist");
        });

    }

    public Map<String, Object> getUserMap(int userID) {
        return null;
    }

    public Map<String, Object> getRoomMap(int userID) {
        return null;
    }

    public Map getMap(Request request) {
        if (request.attributes().contains("info_map")) {
            return request.attribute("info_map");
        } else {
            return new HashMap<>();
        }
    }

    private void setMap(Request request, Map map) {
        request.attribute("info_map", map);
    }

    public void addAllToMap(Request request, Map ob) {
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

    public String getSessionmID(Session session) {
        return this.loggedClients.get(session.id()).toString();
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

    public Controller getCon() {
        return con;
    }

}

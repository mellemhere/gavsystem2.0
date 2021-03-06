/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.servers.connection;

import com.mellemhere.prop.lights.LightState;
import com.mellemhere.main.Controller;

/**
 *
 * @author aluno
 */
public class Commands {

    private final String area = "COMMANDS";

    private final Controller con;
    private final ConnectionInterface connection;

    public Commands(ConnectionInterface connection) {
        this.con = connection.getController();
        this.connection = connection;
    }

    public boolean entry(String ID) {
        this.con.log(area, "Verificando o UID: " + ID, null);
        return this.con.getMysqlController().getUserController().hasUserUID(ID);
    }

    public String command(String command, String args) {
        con.log(area, "Novo comando para processar: " + command, null);
        
        switch (command) {
            case "e":
                /*
                 Normal entry
                 */
                if (entry(args)) {
                    /*
                     SE ESTIVER CORRETO ABRE A PORTA
                     */
                    return "o;o";
                }
                break;

            case "fo":
                return "o;o";
            case "c":
                con.getConnectionController().getCon().getMysqlController().getCurrent().put(args);
                con.getConnectionController().getCon().getWebSocketController().calcularPreco(Float.parseFloat(args));
                con.getConnectionController().getCon().getWebSocketController().calcularWVH(Float.parseFloat(args));
                break;
            case "l":
                String id = args.split("")[0];
                if (args.split("")[1].equalsIgnoreCase("1")) {
                    this.connection.getConnection().getLightControl().toogleLightNonBroadcastBack(id, LightState.ON);
                }else{
                    this.connection.getConnection().getLightControl().toogleLightNonBroadcastBack(id, LightState.OFF);
                }
                break;
            default:
                break;
        }

        return "";
    }

    public void sendCommand(Command cmd) {
        switch (cmd) {
            case CLOSE_DOOR:
                this.connection.sendMessage("d;c");
                break;
            case OPEN_DOOR:
                this.connection.sendMessage("o;o");
                break;
            case LOCK_DOOR:
                this.connection.sendMessage("d;l");
                break;
            case BEEP_ON:
                this.connection.sendMessage("b;o");
                break;
            case BEEP_OFF:
                this.connection.sendMessage("b;f");
                break;
        }
    }

    public void sendCommand(String cmd) {
        this.connection.sendMessage(cmd);
    }

}

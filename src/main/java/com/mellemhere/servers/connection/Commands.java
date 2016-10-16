/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.servers.connection;

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
        this.con.getStatistics().setLastScan(ID);
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
                    this.con.getStatistics().doorOpened();
                    this.con.getStatistics().setLastEntry(args);
                    return "o";
                }
                break;

            case "fo":

                this.con.getStatistics().doorOpened();
                return "o";

            case "c":
                /*
                 Door closed
                 */
                break;
            case "l":

                String id = args.split("-")[0];
                int state = 0;
                if (args.split("-")[1].equalsIgnoreCase("on")) {
                    state = 1;
                }

                //this.con.getLight().lightChanged(id, state);
                break;
            default:
                break;
        }

        return null;
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
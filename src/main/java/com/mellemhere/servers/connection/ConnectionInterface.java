/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.servers.connection;

import com.mellemhere.main.Controller;

/**
 *
 * @author MellemHere
 */
public interface ConnectionInterface {
    
    public void startConnection();
    public void stopConnection();
    public Controller getController();
    public Room getConnection();
    public void sendMessage(String message);
    public void processCommand(String cmd);
    public Commands getCommandHandler();
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.servers.websocket;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

@WebSocket
public class WebSocketHandler {

    WebSocketController wcon;

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
        //DO-NOTHING
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        wcon.removeClient(user);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
        wcon.processCommand(user, message);
    }

    public void passController(WebSocketController wcon) {
        this.wcon = wcon;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.servers.connection;

import com.mellemhere.server.websocket.mObjects.RoomObject;
import com.mellemhere.servers.websocket.*;

/**
 *
 * @author MellemHere
 */
public class Connection {
    
    private ConnectionStatus status;
    private ConnectionInterface connection;
    private RoomObject room;
   
    private RealTimeData rtd;
    
    private final ConnectionController ccon;

    public Connection(ConnectionController ccon) {
        this.ccon = ccon;
    }

    public ConnectionController getCcon() {
        return ccon;
    }

    public RealTimeData getRtd() {
        return rtd;
    }
    
    
    public ConnectionStatus getStatus() {
        return status;
    }

    public void setStatus(ConnectionStatus status) {
        this.status = status;
    }

    public ConnectionInterface getConnection() {
        return connection;
    }

    public void setConnection(ConnectionInterface connection) {
        this.connection = connection;
    }

    public RoomObject getRoom() {
        return room;
    }

    public void setRoom(RoomObject room) {
        this.room = room;
    }
    
    
    public void startRealTimeData(){
        this.rtd = new RealTimeData(this);
        this.rtd.start();
    }
    
    public void stopRealTimeData(){
        //TO-DO
    }
    
    
    
    
}

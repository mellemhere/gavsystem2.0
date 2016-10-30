/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.servers.connection;

import com.mellemhere.prop.lights.LightControl;
import com.mellemhere.prop.statistics.Statistics;
import com.mellemhere.server.websocket.mObjects.RoomObject;
import com.mellemhere.servers.websocket.*;

/**
 *
 * @author MellemHere
 */
public class Room {

    private ConnectionStatus status;
    private ConnectionInterface connection;
    private final RoomObject room;

    private RealTimeData rtd;

    private final ConnectionController ccon;

    private LightControl lightControl;

    private Statistics statistics;
    
    private final long ROOM_STARTUP_TIME;
    
    public Room(RoomObject room,ConnectionController ccon) {
        this.ROOM_STARTUP_TIME = System.currentTimeMillis();
        
        this.ccon = ccon;
        this.room = room;
    }
    
    /*
        GETTERS AND SETTERS
    */

    public Statistics getStatistics() {
        return statistics;
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
    
    public RoomObject getRoom() {
        return room;
    }

    public void setStatus(ConnectionStatus status) {
        this.status = status;
    }

    public ConnectionInterface getConnection() {
        return connection;
    }

    public LightControl getLightControl() {
        return lightControl;
    }

    public void setConnection(ConnectionInterface connection) {
        this.connection = connection;
    }

    public long getUptime(){
        return System.currentTimeMillis() - ROOM_STARTUP_TIME;
    }
    
    public String getFormatedUptime(){
        return ccon.getCon().formatTime(this.getUptime());
    }
    
    /*
        START MODULES
    */
    public void start(){

        this.startStatistics();
        this.startRealTimeData();
        this.startLightsManagement();
    }
    
    public void startRealTimeData() {
        this.rtd = new RealTimeData(this);
        this.rtd.start();
    }

    void startLightsManagement() {
        this.lightControl = new LightControl(this);
    }

    void startStatistics() {
        this.statistics = new Statistics(this);
    }

}

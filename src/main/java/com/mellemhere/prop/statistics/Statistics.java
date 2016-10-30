/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.prop.statistics;

import com.mellemhere.mysql.MySQLStatisticsController;
import com.mellemhere.servers.connection.Room;

public class Statistics {

    private final Room room;
    private final MySQLStatisticsController scon;
    private final int roomID;

    public Statistics(Room room) {
        this.room = room;
        this.roomID = room.getRoom().getId();
        this.scon = room.getCcon().getCon().getMysqlController().getStatisticsController();
    }

    public void doorOpened() {
        if (this.scon.hasStatistic(roomID, "doorOpened")) {
            this.scon.set(roomID, "doorOpened", (this.scon.getInt(roomID, "doorOpened") + 1));
        } else {
            this.scon.set(roomID, "doorOpened", 1);
        }
    }

    public void newEntry(int userID) {
        this.scon.set(roomID, "lastEntry", userID);
    }

    public void uptime() {
        this.scon.set(roomID, "uptime", room.getUptime());
    }

    public void energyConsumption() {
        /*
         TO-UPDATE
         return lightForce * con.getLight().getLightTimes().size();
         */
    }

    public void energyConsumptionCost() {
        /*
        
         TO-UPDATE
        
         con.getLight().getLightTimes().forEach((id, tempo) -> {
         consumptionPriceTotal += (float) ((float) 240 / (float) 1000) * (((float) (System.currentTimeMillis() - tempo) / (float) (1000 * 60 * 60)) % (float) 24);
         con.getLight().getLightTimes().put(id, System.currentTimeMillis());
         });
         return BigDecimal.valueOf((consumptionPriceTotal * 0.24)).setScale(5, RoundingMode.CEILING);
        
         */
    }

    public void energyConsumptionCostSaved() {
        //TO-DO
    }

}

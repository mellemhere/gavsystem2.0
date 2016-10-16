/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.main;

import com.mellemhere.server.websocket.mObjects.GraphObject;
import com.mellemhere.server.websocket.mObjects.LightConObject;
import com.mellemhere.server.websocket.mObjects.LightObject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;

/**
 *
 * @author MellemHere
 */
public class LightCon {

    Controller con;

    HashMap<Integer, Integer> consumptionByHour = new HashMap<>();

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private final int lightForce = 240; //Watts
    private float consumptionPriceTotal = 0; //Reais
    private int totalFromDay = 0;
    
    private int fallBack = 0;//If the program shuts down in the middle of day, the value that it was is going to be stored here
    
    String todayKey = "";

    JSONObject lightData;

    public LightCon(Controller con) {
        this.con = con;
        
        this.lightData = con.getStatistics().getLightStatsData();
        
        Timer timer = new Timer();

        this.todayKey = this.getTodaysKey();
        
        if(this.lightData.has(todayKey)){
            JSONObject today = this.lightData.getJSONObject(todayKey);
            this.consumptionPriceTotal = Float.parseFloat(today.get("consumptionPrice").toString());
            this.fallBack = today.getInt("consumptionWatts");
        }
        
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
               /* if (con.webSocket != null) {
                  //  con.webSocket.broadcast("lightStats", new LightObject("lightConsumptionPrice", getLightConsumptionPriceMessage()));
                  //  con.webSocket.broadcast("lightStats", new LightObject("lightConsumption", getLightConsumptionMessage()));
                }*/
                refreshDataJSON(); //Bad?
            }
        }, 0, 1000);
    }

    public int getConsumption() {
        return lightForce * con.getLight().getLightTimes().size();
    }

    public String getLightConsumptionMessage() {
        return getConsumption() + " Watts/Hora";
    }

    public BigDecimal getConsumptionPrice() {
        con.getLight().getLightTimes().forEach((id, tempo) -> {
            consumptionPriceTotal += (float) ((float) 240 / (float) 1000) * (((float) (System.currentTimeMillis() - tempo) / (float) (1000 * 60 * 60)) % (float) 24);
            con.getLight().getLightTimes().put(id, System.currentTimeMillis());
        });
        return BigDecimal.valueOf((consumptionPriceTotal * 0.24)).setScale(5, RoundingMode.CEILING);
    }

    public String getLightConsumptionPriceMessage() {
        return "R$ " + getConsumptionPrice();
    }

    private String getTodaysKey() {
        Date date = new Date();
        return dateFormat.format(date);
    }

    private int getHour() {
        Calendar rightNow = Calendar.getInstance();
        return rightNow.get(Calendar.HOUR_OF_DAY);
    }

    /*
     DATA SAVER
     */
    private void refreshDataJSON() {
        if (!todayKey.equalsIgnoreCase(getTodaysKey())) {
            con.log("DATA", "Novo dia detectado!", null);
            todayKey = getTodaysKey();
            consumptionPriceTotal = 0; //Reseta pelo dia
            fallBack = 0;
            consumptionByHour = new HashMap<>();
        }

        this.consumptionByHour.put(getHour(), getConsumption());

        JSONObject todaysData = new JSONObject();
        todaysData.put("consumptionPrice", getConsumptionPrice());
        todaysData.put("consumptionWatts", getConsumptionForTheDay());

        this.lightData.put(getTodaysKey(), todaysData);
        con.getStatistics().setLightStats(this.lightData);
    }

    public LightConObject getDataFrom(String date) {
        LightConObject returner = new LightConObject();
        if (this.lightData.has(date)) {
            JSONObject data = this.lightData.getJSONObject(date);
            returner.setConsumptionPrice(data.getBigDecimal("consumptionPrice"));
            returner.setConsumptionWatts(data.getInt("consumptionWatts"));
            returner.setDate(date);
            return returner;
        } else {
            return returner;
        }
    }

    public GraphObject getConsumptionHourGraph() {
        GraphObject ob = new GraphObject();
        this.consumptionByHour.forEach((time, cons) -> {
            ob.add(time + "H", cons);
        });
        return ob;
    }

    public GraphObject getConsumptionMonthGraph() {
        GraphObject ob = new GraphObject();
        lightData.keySet().forEach(date -> {
            ob.add(date, lightData.getJSONObject(date).getInt("consumptionWatts"));
        });
        return ob;
    }

    private int getConsumptionForTheDay() {
        totalFromDay = 0 + fallBack;
        
        this.consumptionByHour.forEach((hour, usage) -> {
            totalFromDay += usage;
        });

        return totalFromDay;
    }

}

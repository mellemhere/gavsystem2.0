/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.server.websocket.mObjects;

import java.math.BigDecimal;

/**
 *
 * @author MellemHere
 */
public class LightConObject {
    
    
    private String date;
    private BigDecimal consumptionPrice;
    private int consumptionWatts;

    public BigDecimal getConsumptionPrice() {
        return consumptionPrice;
    }

    public void setConsumptionPrice(BigDecimal consumptionPrice) {
        this.consumptionPrice = consumptionPrice;
    }

    public int getConsumptionWatts() {
        return consumptionWatts;
    }

    public void setConsumptionWatts(int consumptionWatts) {
        this.consumptionWatts = consumptionWatts;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    
    
    
    
    
}

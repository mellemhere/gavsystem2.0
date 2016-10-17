/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.servers.connection;

/**
 *
 * @author MellemHere
 */
public enum ConnectionStatus {
    
    FAILED(0, "DESCONECTADO"), CONNECTING(1, "CONECTANDO"), CONNECTED(2, "CONECTADO");
    
    private Integer id;
    private String name;

    private ConnectionStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    
}

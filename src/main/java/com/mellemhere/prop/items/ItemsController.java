/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.prop.items;

import com.mellemhere.main.Controller;
import com.mellemhere.servers.connection.Room;
import java.util.List;

/**
 *
 * @author MellemHere
 */
public class ItemsController {
    
    
    
    private final Controller con;

    public ItemsController(Controller con) {
        this.con = con;
    }
    
    
    public void hasItem(Item item){
        
    }
    
    /*
        Database functions 
    */
    public void saveItem(Item item){
        
    }
    
    public void newItem(Item item){
        
    }
    
    
    public void updateItem(Item item){
        
    }
    
    public void deleteItem(Item item){
        
    }
    
    
    /*
    
        Search functions
    
    */
    
    public List<Item> searchByName(String query){
        return null;
    }
    
    public List<Item> searchByID(String id){
        return null;
    }
    
    public List<Item> searchByRoom(Room room, String query){
        return null;
    }
    
    
    
    
}

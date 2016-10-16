/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.mysql;


/**
 *
 * @author MellemHere
 */
public class LogController {

    private final String DB_NAME = "gav_log";

    private MySQLController con;

    public LogController(MySQLController con) {
        this.con = con;
    }
}

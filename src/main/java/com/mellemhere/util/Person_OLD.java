/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.util;

import org.json.JSONObject;

/**
 *
 * @author aluno
 */
public class Person_OLD {

    JSONObject info;

    // UID CODE
    private String cardID;

    // Persons name
    private String name;

    // Registration code (Matricula)
    private String registration;

    //Position (Aluno/Professor/Fucnionario)
    private Position_OLD position;

    private String password;

    public Person_OLD(JSONObject info) {
        this.setInfo(info);
    }

    public Person_OLD() {
        this.info = new JSONObject();
    }

    /*
     Set information
     */
    public void setInfo(JSONObject info) {
        this.info = new JSONObject();

        this.setCardID(info.getString("UID"));
        this.setPosition(info.getString("POSITION"));
        this.setName(info.getString("NAME"));
        if (info.has("ID")) {
            this.setRegistration(info.getString("ID"));
        }
        if (info.has("password")) {
            this.setPassword(info.getString("password"));
        }
    }

    public String getCardID() {
        return cardID;
    }

    public void setCardID(String cardID) {
        this.cardID = cardID;
        info.put("UID", cardID);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        info.put("NAME", name);
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
        info.put("ID", registration);
    }

    public Position_OLD getPosition() {
        return position;
    }

    public JSONObject getInfo() {
        return info;
    }

    public void setPosition(String position) {
        /*
         1 -> EMPLOYEE
         2 -> TEACHER
         3 -> STUDENT
         */
        switch (position) {
            case "Funcionario":
                this.position = Position_OLD.EMPLOYEE;
                break;
            case "Professor":
                this.position = Position_OLD.TEACHER;
                break;
            case "Aluno":
                this.position = Position_OLD.STUDENT;
                break;
            default:
                this.position = Position_OLD.TEACHER;
                break;
        }

        info.put("POSITION", position);
    }

    public void setPassword(String pass) {
        this.password = pass;
        info.put("password", pass);
    }

    public String getPasword() {
        return this.password;
    }

}

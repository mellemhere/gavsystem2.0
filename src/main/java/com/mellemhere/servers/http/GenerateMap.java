/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mellemhere.servers.http;

import spark.Request;

/**
 *
 * @author MellemHere
 */
public class GenerateMap {

    static void defaultMap(Request request, HTTPController controller) {
        addUserMap(request, controller);
    }

    /*
        USER
     */
    static void addUserMap(Request request, HTTPController controller) {
        controller.addAllToMap(request, controller.getCon().objectToMap(
                controller.getCon().getMysqlController().getUserController().getUserByMID(controller.getSessionmID(request.session()))));
    }

    static void addUserCustomMap(Request request, HTTPController controller, String key, String MID) {
        if (key == null) {
            controller.addAllToMap(request, controller.getCon().objectToMap(
                    controller.getCon().getMysqlController().getUserController().getUserByMID(MID)));
        } else {
            controller.addAllToMap(request, controller.getCon().addObjectAsChildMap(controller.getMap(request),
                    key,
                    controller.getCon().getMysqlController().getUserController().getUserByID(request.params(":user-id"))));
        }
    }

    /* 
        ROOM
     */
    static void roomMap(Request request, HTTPController controller, String roomID) {
        controller.addAllToMap(request, controller.getCon().addObjectAsChildMap(controller.getMap(request),
                "room",
                controller.getCon().getMysqlController().getRoomController().getRoom(Integer.parseInt(roomID))));
    }

    //Returns room and all oth
    static void allRoomsWithArgMap(Request request, HTTPController controller) {
        roomMap(request, controller, request.params(":room"));

        controller.addAllToMap(request, controller.getCon().addObjectAsChildMap(controller.getMap(request),
                "rooms",
                controller.getCon().getMysqlController().getRoomController().getRooms().toArray()));
    }

    static void allRoomsMap(Request request, HTTPController controller) {
        controller.addAllToMap(request, controller.getCon().addObjectAsChildMap(controller.getMap(request),
                "rooms",
                controller.getCon().getMysqlController().getRoomController().getRooms().toArray()));
    }

}

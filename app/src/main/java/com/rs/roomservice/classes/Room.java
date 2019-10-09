package com.rs.roomservice.classes;

/* Room.java
 * Created by Wojciech Krajewski
 * 2019
 */public class Room {

     private String roomNumber;
     private boolean roomOccupied;
     private String roomID;


    // State of the item
    private boolean expanded = false;

     public Room() {
        //empty constructor needed for Firebase
    }

    public Room(String roomID, String roomNumber, boolean roomOccupied) {
         this.roomNumber = roomNumber;
         this.roomOccupied = roomOccupied;
         this.roomID = roomID;

     }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }


    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getRoomNumber() {
        return roomNumber;
    }


    public String getRoomID() {
        return roomID;
    }


    public Boolean isRoomOccupied() {
        return roomOccupied;
    }

    public void setRoomOccupied(boolean roomOccupied) {
        this.roomOccupied = roomOccupied;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }



}

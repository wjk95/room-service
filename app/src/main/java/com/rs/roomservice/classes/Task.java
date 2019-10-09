package com.rs.roomservice.classes;

import com.google.firebase.Timestamp;

/* Task.java
 * Created by Wojciech Krajewski
 * 2019
 */public class Task {

    private Timestamp date;
    private int roomNumber;
    private String taskType;
    private String taskStatus; //pending, todo

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    private String workerId;


    public Task() {
        //empty constructor needed for Firebase

    }


    public Task(int roomNumber, String taskType, String taskStatus, Timestamp date, String workerId) {
        this.roomNumber = roomNumber;
        this.taskType = taskType;
        this.taskStatus = taskStatus;
        this.date = date;
        this.workerId = workerId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}

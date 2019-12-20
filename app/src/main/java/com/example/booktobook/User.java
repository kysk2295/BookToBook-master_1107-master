package com.example.booktobook;


import java.util.List;

public class User {
    private String id;
    private String password;
    private List<Alert> alert;
    private String place;
    private String time;

    public User() {
    }

    public User(String id, String password, String place, String time) {
        this.id = id;
        this.password = password;
        this.place = place;
        this.time = time;
    }

    public User(String id, String password, List<Alert> alert, String place, String time) {
        this.id = id;
        this.password = password;
        this.alert = alert;
        this.place = place;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public List<Alert> getAlert() {
        return alert;
    }

    public void setAlert(List<Alert> alert) {
        this.alert = alert;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
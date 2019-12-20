package com.example.booktobook;

public class Alert{
    private String status;
    private String place;
    private String time;
    private String book_title;
    private String who;

    public Alert() {

    }

    public Alert(String place, String time, String status,
                 String book_title, String who) {
        this.place = place;
        this.time = time;
        this.status = status;
        this.book_title = book_title;
        this.who = who;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBook_title() {
        return book_title;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }
}
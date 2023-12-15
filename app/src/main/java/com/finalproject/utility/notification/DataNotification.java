package com.finalproject.utility.notification;

public class DataNotification {
    private int number;
    private String text;

    public DataNotification(int number) {
        this.number = number;
    }

    public DataNotification(String text) {
        this.text = text;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

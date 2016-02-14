package com.thevarunshah.backend;

public class Event {

    public int id;
    public String admin_id;
    public String name;
    public String date;
    public String time;
    public String description;

    public Event(int id, String admin_id, String name, String dateTime, String description){
        this.id = id;
        this.admin_id = admin_id;
        this.name = name;
        this.date = dateTime.substring(0, dateTime.indexOf(' '));
        this.time = dateTime.substring(dateTime.indexOf(' ')+1, dateTime.length()-3);
        this.description = description;
    }
}

package com.example.melochat.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.GregorianCalendar;

@IgnoreExtraProperties
public class Event {
    private String name;
    private String location;
    private GregorianCalendar date;

    public Event() {
    }

    public Event(String name, String location, int year, int month, int day, int hour, int min) {
        this.name = name;
        this.location = location;
        this.date = new GregorianCalendar(year, month, day, hour, min);
    }

    public String getName() { return name; }

    public String getLocation() { return location; }

    public GregorianCalendar getDate() {return date; }
}

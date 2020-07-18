package com.stefanlippl.hangover.events.datecards;

import com.stefanlippl.hangover.events.EventItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateCard {

    private ArrayList<EventItem> events = new ArrayList<>();
    private GregorianCalendar date;
    private String weekDay;

    public DateCard(GregorianCalendar date, ArrayList<EventItem> allEvents) {
        this.date = date;
        for(EventItem item : allEvents){
            if(this.date.get(Calendar.YEAR) == item.getCalendar().get(Calendar.YEAR) && this.date.get(Calendar.MONTH) == item.getCalendar().get(Calendar.MONTH) && this.date.get(Calendar.DAY_OF_MONTH) == item.getCalendar().get(Calendar.DAY_OF_MONTH)){
                events.add(item);
            }
        }
        weekDay = calcWeekDay();
    }

    private String calcWeekDay(){

        GregorianCalendar today = new GregorianCalendar();
        if(date.get(Calendar.YEAR) == today.get(Calendar.YEAR) && date.get(Calendar.MONTH) == today.get(Calendar.MONTH) && date.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)){
            return "Heute";
        }

        switch (date.get(Calendar.DAY_OF_WEEK)){
            case 1: return "Sonntag";
            case 2: return "Montag";
            case 3: return "Dienstag";
            case 4: return "Mittwoch";
            case 5: return "Donnerstag";
            case 6: return "Freitag";
            case 7: return "Samstag";
            default: return "Irgendwas läuft falsch";
        }
    }

    public String getDateString(){
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH) +1;
        int day = date.get(Calendar.DAY_OF_MONTH);
        return String.format("%02d.%02d.%d",day,month,year);
    }

    public String getWeekDay() {
        return weekDay;
    }

    public ArrayList<EventItem> getEvents() {
        return events;
    }

    public GregorianCalendar getDate() {
        return date;
    }
}

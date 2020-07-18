package com.stefanlippl.hangover.events;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.stefanlippl.hangover.database.Database;

import java.util.Calendar;
import java.util.GregorianCalendar;

@Entity
public class EventItem implements Comparable<EventItem>, Parcelable {

    @PrimaryKey
    @NonNull
    private String id;
    private int imageResource;
    private String headline;
    private String date;
    private String time;
    private String locationId;
    private String ima;
    private String info;
    private double price;
    private String music;
    private String uri;
    String locationName;
    private boolean isSaved;

    @Ignore
    private GregorianCalendar calendar = new GregorianCalendar();

    @Ignore
    public EventItem(Context context, String id, int imageResource, String headline, String date, String time, String locationId, String ima, String info, double price, String music, String uri) {
        this.id = id;
        this.imageResource = imageResource;
        this.headline = headline;
        this.date = date;
        this.time = time;
        this.locationId = locationId;
        this.ima = ima;
        this.info = info;
        this.price = price;
        this.music = music;
        this.uri = uri;
        isSaved = false;

        calendar = initializeCalendar(date, time);
    }

    public EventItem(String id, int imageResource, String headline, String date, String time, String ima, String info, String music, double price, String locationId, String uri, boolean isSaved, String locationName) {
        this.id = id;
        this.imageResource = imageResource;
        this.headline = headline;
        this.date = date;
        this.time = time;
        this.locationId = locationId;
        this.ima = ima;
        this.music = music;
        this.info = info;
        this.price = price;
        this.isSaved = isSaved;
        this.uri = uri;
        this.locationName = locationName;

        calendar = initializeCalendar(date, time);
    }

    /**
     * Getter
     */

    public int getImageResource() {
        return imageResource;
    }

    public String getHeadline() {
        return headline;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getIma() {
        return ima;
    }

    public String getInfo() {
        return info;
    }

    public double getPrice() {
        return price;
    }

    public String getMusic() {
        return music;
    }

    public String getId() {
        return id;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public GregorianCalendar getCalendar() {
        return calendar;
    }

    public String getUri() {
        return uri;
    }

    public String getLocationName() {
        return locationName;
    }

    /**
     * Setter
     */

    public void setId(String id) {
        this.id = id;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public void setIma(String ima) {
        this.ima = ima;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public void setCalendar(GregorianCalendar calendar) {
        this.calendar = calendar;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /**
     * Andere Methoden
     */

    @Override
    public int compareTo(EventItem another) {
        return calendar.compareTo(another.calendar);
    }

    private GregorianCalendar initializeCalendar(String date, String time) {
        GregorianCalendar calendar = new GregorianCalendar();
        char[] date1 = date.toCharArray();
        char[] time1 = time.toCharArray();
        int year = Integer.parseInt(String.format("%c%c%c%c", date1[6], date1[7], date1[8], date1[9]));
        int month = Integer.parseInt(String.format("%c%c", date1[3], date1[4]));
        int day = Integer.parseInt(String.format("%c%c", date1[0], date1[1]));
        int hour = Integer.parseInt(String.format("%c%c", time1[0], time1[1]));
        int minute = Integer.parseInt(String.format("%c%c", time1[3], time1[4]));
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(imageResource);
        dest.writeString(headline);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(music);
        dest.writeDouble(price);
        dest.writeString(locationId);
        dest.writeString(ima);
        dest.writeString(info);
        dest.writeByte((byte) (isSaved ? 1 : 0));
        dest.writeString(uri);
        dest.writeString(locationName);
    }

    public static final Parcelable.Creator<EventItem> CREATOR = new Parcelable.Creator<EventItem>() {
        public EventItem createFromParcel(Parcel in) {
            return new EventItem(in);
        }

        public EventItem[] newArray(int size) {
            return new EventItem[size];
        }
    };

    private EventItem(Parcel in) {
        id = in.readString();
        imageResource = in.readInt();
        headline = in.readString();
        date = in.readString();
        time = in.readString();
        music = in.readString();
        price = in.readDouble();
        locationId = in.readString();
        ima = in.readString();
        info = in.readString();
        isSaved = in.readByte() != 0;
        uri = in.readString();
        locationName = in.readString();
    }

}
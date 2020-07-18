package com.stefanlippl.hangover.database;

import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.stefanlippl.hangover.events.EventItem;
import com.stefanlippl.hangover.locations.LocationItem;

@android.arch.persistence.room.Database(entities = {LocationItem.class, EventItem.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class Database extends RoomDatabase {

    private static Database INSTANCE;
    public static final String DATABSE_NAME = "database";

    public abstract AccessDao accessDao();

    public static Database getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), Database.class, DATABSE_NAME).build();
        }
        return INSTANCE;
    }

}
package com.stefanlippl.hangover.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.GregorianCalendar;

public class Converters {

    @TypeConverter
    public static String[] fromStringToStringArray(String value){
        Type listType = new TypeToken<String[]>(){}.getType();
        return new Gson().fromJson(value,listType);
    }

    @TypeConverter
    public static String fromStringArray(String[] array){
        Gson gson = new Gson();
        String json = gson.toJson(array);
        return json;
    }
}
package com.stefanlippl.hangover.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.stefanlippl.hangover.events.EventItem;
import com.stefanlippl.hangover.locations.LocationItem;
import java.util.List;

@Dao
public interface AccessDao {

    /**
     * Location Queries
     */

    //QUERIES ______________________________________________________________________________________

    @Query("SELECT * FROM LocationItem")
    List<LocationItem> getAllLocations();

    @Query("SELECT locationName FROM LocationItem WHERE id = :id")
    String getLocationName(String id);

    @Query("SELECT * FROM LocationItem WHERE id= :id")
    LocationItem getSingleLocation(String id);

    @Query("SELECT * FROM LocationItem WHERE locationName LIKE :startingletter || '%'")
    List<LocationItem> getLocationsWithSameStartingLetter(String startingletter);

    @Query("SELECT * FROM EventItem WHERE locationId = :locationId")
    List<EventItem> getAllEventsFromSingleLocation(String locationId);

    @Query("SELECT * FROM LocationItem WHERE isSaved = :value")
    List<LocationItem> getSavedLocations(boolean value);

    @Query("SELECT DISTINCT locationType FROM LocationItem")
    String[] getLocationTypes();

    @Query("SELECT * FROM LocationItem WHERE locationType = :type AND locationName LIKE :startingletter || '%'")
    List<LocationItem> getAllLocationsFromAType(String type, String startingletter);

    //INSERT _______________________________________________________________________________________

    @Insert
    void insertSingleLocation(LocationItem locationItem);

    //UPDATE________________________________________________________________________________________

    @Query("UPDATE LocationItem SET isSaved = :value WHERE id = :id")
    void saveLocation(String id, boolean value);

    @Query("UPDATE LocationItem SET isRated = :value WHERE id = :id")
    void rateLocation(String id, boolean value);

    @Query("UPDATE LocationItem SET rating = :rating WHERE id = :id")
    void updateRating(String id, float rating);

    @Query("UPDATE LocationItem SET ratingCount = :ratingCount WHERE id = :id")
    void updateRatingCount(String id, int ratingCount);

    /**
     * Event Queries
     */

    //QUERIES ______________________________________________________________________________________


    @Query("SELECT * FROM EventItem")
    List<EventItem> getAllEvents();

    @Query("SELECT * FROM EventItem WHERE isSaved = :value")
    List<EventItem> getSavedEvents(boolean value);

    @Query("SELECT DISTINCT date FROM EventItem")
    List<String> getAllEventDates();

    @Query("SELECT DISTINCT locationType FROM LocationItem INNER JOIN EventItem ON LocationItem.id = EventItem.locationId")
    String[] getAllLocationTypesWithEvents();

    @Query("SELECT DISTINCT music FROM EventItem")
    String[] getMusic();

    @Query("SELECT * FROM EventItem WHERE price <= :price")
    List<EventItem> getFilteredEventsByPrice(int price);

    //INSERT _______________________________________________________________________________________

    @Insert
    void insertSingleEvent(EventItem eventItem);

    @Query("DELETE FROM EventItem WHERE id = :id")
    void deleteSingleEvent(String id);

    //UPDATE _______________________________________________________________________________________

    @Query("UPDATE EventItem SET isSaved = :value WHERE id = :id")
    void saveEvent(String id, boolean value);
}

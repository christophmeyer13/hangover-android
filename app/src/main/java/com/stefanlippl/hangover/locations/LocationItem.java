package com.stefanlippl.hangover.locations;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

@Entity
public class LocationItem implements Comparable<LocationItem>, Parcelable {

    /**
     * Instanzmember für alle Attribute aus der Firebase
     * id ist primary key der Datenbank und wird automatisch generiert
     */

    @PrimaryKey
    @NonNull
    private String id;
    private int imageResource;
    private String locationName;
    private String locationStreet;
    private String houseNumber;
    private String locationZip;
    private String locationCity;
    private String locationType;
    private String locationInfo;
    private String[] locationOpenFrom;
    private String[] locationOpenUntil;
    private double lat;
    private double lng;
    private String url;

    @Ignore
    private Bitmap locationLogo = null;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] image = null;

    private int bufferSize;
    private boolean isSaved;
    private boolean isRated;
    private float rating;
    private int ratingCount;

    public LocationItem(String id, int imageResource, String locationName, String locationStreet, String houseNumber, String locationZip, String locationCity, String locationType, String url, String locationInfo, String[] locationOpenFrom, String[] locationOpenUntil, Context context, Bitmap locationLogo) {
        this.id = id;
        this.imageResource = imageResource;
        this.locationName = locationName;
        this.locationStreet = locationStreet;
        this.houseNumber = houseNumber;
        this.locationZip = locationZip;
        this.locationCity = locationCity;
        this.locationType = locationType;
        this.url = url;
        this.locationInfo = locationInfo;
        this.locationOpenFrom = locationOpenFrom;
        this.locationOpenUntil = locationOpenUntil;
        this.locationLogo = locationLogo;
        isSaved = false;
        isRated = false;

        //calculatee buffer size for locationlogo and save image into byte array
        if(locationLogo != null) {
            bufferSize = locationLogo.getWidth() * locationLogo.getHeight() * 4;
            ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
            locationLogo.copyPixelsToBuffer(byteBuffer);
            image = byteBuffer.array();
        }
        reverseGeoCoder(context);
    }

    public LocationItem(@NonNull String id, int imageResource, String locationName, String locationStreet, String houseNumber, String locationZip, String locationCity, String locationType, String locationInfo, String[] locationOpenFrom, String[] locationOpenUntil, double lat, double lng, String url, byte[] image, int bufferSize, boolean isSaved, boolean isRated, float rating, int ratingCount) {
        this.id = id;
        this.imageResource = imageResource;
        this.locationName = locationName;
        this.locationStreet = locationStreet;
        this.houseNumber = houseNumber;
        this.locationZip = locationZip;
        this.locationCity = locationCity;
        this.locationType = locationType;
        this.locationInfo = locationInfo;
        this.locationOpenFrom = locationOpenFrom;
        this.locationOpenUntil = locationOpenUntil;
        this.lat = lat;
        this.lng = lng;
        this.url = url;
        this.image = image;
        this.isSaved = isSaved;
        this.isRated = isRated;
        this.rating = rating;
        this.ratingCount = ratingCount;
        //byte array to bitmap
        if(image != null) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(image);
            Bitmap bmp = Bitmap.createBitmap(100, bufferSize/4/100, Bitmap.Config.ARGB_8888);
            bmp.copyPixelsFromBuffer(byteBuffer);
            locationLogo = bmp;
        }
        else{
            locationLogo = null;
        }
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getLocationStreet() {
        return locationStreet;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getLocationZip() {
        return locationZip;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public String getLocationType() {
        return locationType;
    }

    public String getUrl() {
        return url;
    }

    public String getLocationInfo() {
        return locationInfo;
    }

    public String[] getLocationOpenFrom() {
        return locationOpenFrom;
    }

    public String[] getLocationOpenUntil() {
        return locationOpenUntil;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getId() {
        return id;
    }

    public Bitmap getLocationLogo() {
        return locationLogo;
    }

    public byte[] getImage() {
        return image;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public boolean isRated() {
        return isRated;
    }

    public float getRating() {
        return rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public String getLocationOpening(){
        String result = "";
        for (int i = 0; i < locationOpenFrom.length; i++) {
            switch (i){
                case 0:
                    result += "Montag: \t\t\t";
                    break;
                case 1:
                    result += "Dienstag: \t\t";
                    break;
                case 2:
                    result += "Mittwoch: \t\t";
                    break;
                case 3:
                    result += "Donnerstag: \t";
                    break;
                case 4:
                    result +=  "Freitag: \t\t\t";
                    break;
                case 5:
                    result += "Samstag: \t\t";
                    break;
                case 6:
                    result += "Sonntag: \t\t";
            }
            if(locationOpenFrom[i].equals("")){
                result += "geschlossen\n";
            }
            else {
                result += locationOpenFrom[i] + " - " + locationOpenUntil[i] + "\n";
            }
        }
        return result;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setLocationStreet(String locationStreet) {
        this.locationStreet = locationStreet;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public void setLocationZip(String locationZip) {
        this.locationZip = locationZip;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public void setLocationInfo(String locationInfo) {
        this.locationInfo = locationInfo;
    }

    public void setLocationOpenFrom(String[] locationOpenFrom) {
        this.locationOpenFrom = locationOpenFrom;
    }

    public void setLocationOpenUntil(String[] locationOpenUntil) {
        this.locationOpenUntil = locationOpenUntil;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLocationLogo(Bitmap locationLogo) {
        this.locationLogo = locationLogo;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public void setRated(boolean rated) {
        isRated = rated;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }


    @Override
    public int compareTo(LocationItem another) {
        return locationName.toLowerCase().compareTo(another.getLocationName().toLowerCase());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(imageResource);
        dest.writeString(locationName);
        dest.writeString(locationStreet);
        dest.writeString(houseNumber);
        dest.writeString(locationZip);
        dest.writeString(locationCity);
        dest.writeString(locationType);
        dest.writeString(url);
        dest.writeString(locationInfo);
        dest.writeStringArray(locationOpenFrom);
        dest.writeStringArray(locationOpenUntil);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeParcelable(locationLogo,0);
        dest.writeByte((byte) (isSaved ? 1 : 0));
        dest.writeByte((byte) (isRated ? 1 : 0));
        dest.writeFloat(rating);
        dest.writeInt(ratingCount);
    }

    private LocationItem(Parcel in) {
        id = in.readString();
        imageResource = in.readInt();
        locationName = in.readString();
        locationStreet = in.readString();
        houseNumber = in.readString();
        locationZip = in.readString();
        locationCity = in.readString();
        locationType = in.readString();
        url = in.readString();
        locationInfo = in.readString();
        locationOpenFrom = in.createStringArray();
        locationOpenUntil = in.createStringArray();
        lat = in.readDouble();
        lng = in.readDouble();
        locationLogo = in.readParcelable(Bitmap.class.getClassLoader());
        isSaved = in.readByte() != 0;
        isRated = in.readByte() != 0;
        rating = in.readFloat();
        ratingCount = in.readInt();
    }

    public static final Parcelable.Creator<LocationItem> CREATOR = new Parcelable.Creator<LocationItem>() {
        public LocationItem createFromParcel(Parcel in) {
            return new LocationItem(in);
        }

        public LocationItem[] newArray(int size) {
            return new LocationItem[size];
        }
    };

    /**
     * calculate Latitude and Longitude from location addrress
     * @param context context
     */
    private void reverseGeoCoder(Context context) {
        List<Address> location = null;
        Address result = null;
        Geocoder geocoder = new Geocoder(context);
        try {
            location = geocoder.getFromLocationName(locationStreet + " " + houseNumber + " " + locationZip + " " + locationCity, 1);
        } catch (IOException e) {
        }
        if (location != null && location.size() >= 1) {
            if (location.get(0) != null) {
                result = location.get(0);
                lat = result.getLatitude();
                lng = result.getLongitude();
            }
        }
    }
}
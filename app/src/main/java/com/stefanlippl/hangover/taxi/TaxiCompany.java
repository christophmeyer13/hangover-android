package com.stefanlippl.hangover.taxi;

import android.os.Parcel;
import android.os.Parcelable;

import com.stefanlippl.hangover.R;

public class TaxiCompany implements Parcelable {

    private String name;
    private String telNum;
    private static int imageResource = R.drawable.call_a_taxi;
    private String info;
    private String street;
    private String houseNumber;
    private String zip;
    private String city;
    private String prices;
    private String agb;

    public TaxiCompany(String name, String telNum, String info, String street, String houseNumber, String zip, String city, String prices, String agb) {
        this.name = name;
        this.telNum = telNum;
        this.info = info;
        this.street = street;
        this.zip = zip;
        this.city = city;
        this.prices = prices;
        this.agb = agb;
        this.houseNumber = houseNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTelNum() {
        return telNum;
    }

    public void setTelNum(String telNum) {
        this.telNum = telNum;
    }

    public static int getImageResource() {
        return imageResource;
    }

    public static void setImageResource(int imageResource) {
        TaxiCompany.imageResource = imageResource;
    }

    public void setAgb(String agb) {
        this.agb = agb;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPrices() {
        return prices;
    }

    public void setPrices(String prices) {
        this.prices = prices;
    }

    public String getFullAdress(){
        return String.format("%s %s, %s %s",street,houseNumber,zip,city);
    }

    public String getAgb() {
        return agb;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(telNum);
        dest.writeInt(imageResource);
        dest.writeString(info);
        dest.writeString(street);
        dest.writeString(zip);
        dest.writeString(city);
        dest.writeString(prices);
        dest.writeString(agb);
    }

    private TaxiCompany(Parcel in) {
        name = in.readString();
        telNum = in.readString();
        imageResource = in.readInt();
        info = in.readString();
        street = in.readString();
        zip = in.readString();
        city = in.readString();
        prices = in.readString();
        agb = in.readString();
    }

    public static final Parcelable.Creator<TaxiCompany> CREATOR = new Parcelable.Creator<TaxiCompany>() {
        public TaxiCompany createFromParcel(Parcel in) {
            return new TaxiCompany(in);
        }

        public TaxiCompany[] newArray(int size) {
            return new TaxiCompany[size];
        }
    };
}

package com.innotech.imap_taxi.datamodel;

/**
 * Created by Sergey on 13.11.2015.
 */
public class Address {

    private String streetName;
    private String streetID;
    private String house;
    private String houseID;
    private String geoX;
    private String geoY;
    private String entrance;
    private String flat;


    public Address() {
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getEntrance() {
        return entrance;
    }

    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetID() {
        return streetID;
    }

    public void setStreetID(String streetID) {
        this.streetID = streetID;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getHouseID() {
        return houseID;
    }

    public void setHouseID(String houseID) {
        this.houseID = houseID;
    }

    public String getGeoX() {
        return geoX;
    }

    public void setGeoX(String geoX) {
        this.geoX = geoX;
    }

    public String getGeoY() {
        return geoY;
    }

    public void setGeoY(String geoY) {
        this.geoY = geoY;
    }
}

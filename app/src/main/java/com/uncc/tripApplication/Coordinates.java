/*
 * Coordinates class
 * Samba Diagne
 */
package com.uncc.tripApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Coordinates implements Serializable {
    public double latitude;
    public double longitude;

    public Coordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Coordinates() {
    }
    public Coordinates(JSONObject json) throws JSONException {
        this.latitude = json.getDouble("latitude");
        this.longitude = json.getDouble("longitute");

    }

    @Override
    public String toString() {
        return "Cordinates{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

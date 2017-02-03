package co.bongga.touristeando.models;

import io.realm.RealmObject;

/**
 * Created by bongga on 12/23/16.
 */

public class Coordinate extends RealmObject {
    private double latitude;
    private double longitude;

    public Coordinate(){

    }

    public Coordinate(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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

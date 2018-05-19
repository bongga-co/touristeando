package co.bongga.touristeando.models;

/**
 * Created by bongga on 3/25/17.
 */

public class TouryCoordinate {
    private double latitude;
    private double longitude;

    public TouryCoordinate(){

    }

    public TouryCoordinate(double latitude, double longitude) {
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

package co.bongga.touristeando.models;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by bongga on 2/25/17.
 */

public class WiFiLocation extends RealmObject {
    private String type;
    private RealmList<RealmDouble> coordinates;

    public WiFiLocation() {
    }

    public WiFiLocation(String type, RealmList<RealmDouble> coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RealmList<RealmDouble> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(RealmList<RealmDouble> coordinates) {
        this.coordinates = coordinates;
    }
}
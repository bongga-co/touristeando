package co.bongga.toury.models;

import io.realm.RealmObject;

/**
 * Created by bongga on 1/23/17.
 */

public class Place extends RealmObject {
    private String name;
    private String category;
    private String thumbnail;

    public Place(){

    }

    public Place(String name, String category, String thumbnail) {
        this.name = name;
        this.category = category;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}

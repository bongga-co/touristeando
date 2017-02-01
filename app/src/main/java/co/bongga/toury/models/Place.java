package co.bongga.toury.models;

import io.realm.RealmObject;

/**
 * Created by bongga on 1/23/17.
 */

public class Place extends RealmObject {
    private String name;
    private String category;
    private String thumbnail;
    private float rating;
    private Price price;
    private Coordinate coordinates;

    public Place(){

    }

    public Place(String name, String category, String thumbnail, float rating, Price price, Coordinate coordinates) {
        this.name = name;
        this.category = category;
        this.thumbnail = thumbnail;
        this.rating = rating;
        this.price = price;
        this.coordinates = coordinates;
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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Coordinate getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinate coordinates) {
        this.coordinates = coordinates;
    }
}

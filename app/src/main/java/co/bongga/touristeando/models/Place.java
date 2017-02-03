package co.bongga.touristeando.models;

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
    private String description;
    private String city;
    private String country;
    private String address;
    private String place;
    private Phone phone;
    private boolean outstanding;

    public Place(){

    }

    public Place(String name, String category, String thumbnail, float rating, Price price,
                 Coordinate coordinates, String description, String city, String country,
                 String address, String place, Phone phone, boolean outstanding) {
        this.name = name;
        this.category = category;
        this.thumbnail = thumbnail;
        this.rating = rating;
        this.price = price;
        this.coordinates = coordinates;
        this.description = description;
        this.city = city;
        this.country = country;
        this.address = address;
        this.place = place;
        this.phone = phone;
        this.outstanding = outstanding;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public boolean isOutstanding() {
        return outstanding;
    }

    public void setOutstanding(boolean outstanding) {
        this.outstanding = outstanding;
    }
}

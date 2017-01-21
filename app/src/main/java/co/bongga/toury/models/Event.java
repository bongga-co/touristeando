package co.bongga.toury.models;

import io.realm.RealmObject;

/**
 * Created by spval on 14/01/2017.
 */

public class Event extends RealmObject {
    private String id;
    private String thumbnail;
    private String name;
    private String city;
    private String country;
    private String description;
    private Price price;
    private String startDate;
    private String endDate;
    private Double distance;
    private String category;
    private boolean outstanding;
    private Double rating;
    private Coordinate coordinates;
    private String place;

    public Event(){

    }

    public Event(String thumbnail, String name, String city, String country, String description, Price price, String startDate, String endDate, Double distance, String category, boolean outstanding, Double rating, Coordinate coordinates, String place) {
        this.thumbnail = thumbnail;
        this.name = name;
        this.city = city;
        this.country = country;
        this.description = description;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.distance = distance;
        this.category = category;
        this.outstanding = outstanding;
        this.rating = rating;
        this.place = place;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isOutstanding() {
        return outstanding;
    }

    public void setOutstanding(boolean outstanding) {
        this.outstanding = outstanding;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
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

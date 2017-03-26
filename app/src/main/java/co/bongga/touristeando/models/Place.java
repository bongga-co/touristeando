package co.bongga.touristeando.models;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by bongga on 1/23/17.
 */

public class Place extends RealmObject {
    public String getId() {
        return id;
    }

    private String id;
    private String name;
    private String category;
    private String thumbnail;
    private float rating;
    private Price price;
    private Coordinate coordinates;
    private String description;
    private String city;
    private String email;
    private String country;
    private String address;
    private String place;
    private String thing_to_do;
    private Phone phone;
    private boolean outstanding;
    private double distance;
    private RealmList<Service> services;
    private String user;

    public Place(){

    }

    public Place(String id, String name, String category, String thumbnail, String email, float rating, Price price,
                 Coordinate coordinates, double distance, String description, String city, String country, String thing,
                 String address, String place, Phone phone, boolean outstanding) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.thumbnail = thumbnail;
        this.rating = rating;
        this.price = price;
        this.email = email;
        this.coordinates = coordinates;
        this.description = description;
        this.city = city;
        this.country = country;
        this.address = address;
        this.thing_to_do = thing;
        this.place = place;
        this.phone = phone;
        this.outstanding = outstanding;
        this.distance = distance;
    }

    public void setId(String id) {
        this.id = id;
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

    public RealmList<Service> getServices() {
        return services;
    }

    public void setServices(RealmList<Service> services) {
        this.services = services;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getThing() {
        return thing_to_do;
    }

    public void setThing(String thing) {
        this.thing_to_do = thing;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}

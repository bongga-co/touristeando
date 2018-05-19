package co.bongga.touristeando.models;

/**
 * Created by bongga on 3/25/17.
 */

public class TouryPlace {
    private String name;
    private String category;
    private String thumbnail;
    private float rating;
    private TouryPrice price;
    private TouryCoordinate coordinates;
    private String description;
    private String city;
    private String email;
    private String country;
    private String address;
    private String place;
    private String thing_to_do;
    private TouryPhone phone;
    private boolean outstanding;
    private long count;
    private String user;

    public TouryPlace(){

    }

    public TouryPlace(String name, String category, String thumbnail, String email, float rating, TouryPrice price,
                      TouryCoordinate coordinates, String description, String city, String country, String thing,
                 String address, String place, TouryPhone phone, boolean outstanding, String user) {
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
        this.user = user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setPrice(TouryPrice price) {
        this.price = price;
    }

    public void setCoordinates(TouryCoordinate coordinates) {
        this.coordinates = coordinates;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setPhone(TouryPhone phone) {
        this.phone = phone;
    }

    public void setOutstanding(boolean outstanding) {
        this.outstanding = outstanding;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public float getRating() {
        return rating;
    }

    public TouryPrice getPrice() {
        return price;
    }

    public TouryCoordinate getCoordinates() {
        return coordinates;
    }

    public String getDescription() {
        return description;
    }

    public String getCity() {
        return city;
    }

    public String getEmail() {
        return email;
    }

    public String getCountry() {
        return country;
    }

    public String getAddress() {
        return address;
    }

    public String getPlace() {
        return place;
    }

    public String getThing_to_do() {
        return thing_to_do;
    }

    public void setThing_to_do(String thing_to_do) {
        this.thing_to_do = thing_to_do;
    }

    public TouryPhone getPhone() {
        return phone;
    }

    public boolean isOutstanding() {
        return outstanding;
    }

    public String getUser() {
        return user;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}

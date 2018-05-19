package co.bongga.touristeando.models;

/**
 * Created by bongga on 3/22/17.
 */

public class PlaceCategory {
    private String name;
    private String thing;

    public PlaceCategory() {
    }

    public PlaceCategory(String name, String thing) {
        this.name = name;
        this.thing = thing;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThing() {
        return thing;
    }

    public void setThing(String thing) {
        this.thing = thing;
    }
}

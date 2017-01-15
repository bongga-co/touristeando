package co.bongga.toury.models;

/**
 * Created by spval on 14/01/2017.
 */

public class Event {
    private String name;

    public Event(){

    }

    public Event(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package co.bongga.toury.models;

/**
 * Created by spval on 14/01/2017.
 */

public class Event {
    private String name;
    private boolean isSelf;

    public Event(){

    }

    public Event(String name, boolean isSelf){
        this.name = name;
        this.isSelf = isSelf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }
}

package co.bongga.toury.models;

import java.util.List;

/**
 * Created by bongga on 1/16/17.
 */

public class ChatMessage {
    public static final int TEXT_TYPE = 0;
    public static final int IMAGE_TYPE = 1;
    public static final int MAP_TYPE = 2;
    public static final int EVENT_TYPE = 3;
    public static final int PLACES_TYPE = 4;

    private boolean isSelf;
    private String message;
    private int layout_type;
    private List<Event> event;

    public ChatMessage(){

    }

    public ChatMessage(String message, boolean isSelf, int layout_type){
        this.message = message;
        this.isSelf = isSelf;
        this.layout_type = layout_type;
    }

    public ChatMessage(List<Event> event, boolean isSelf, int layout_type){
        this.event = event;
        this.isSelf = isSelf;
        this.layout_type = layout_type;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setSelf(boolean self) {
        isSelf = self;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLayout_type() {
        return layout_type;
    }

    public void setLayout_type(int layout_type) {
        this.layout_type = layout_type;
    }

    public List<Event> getEvent() {
        return event;
    }

    public void setEvent(List<Event> event) {
        this.event = event;
    }
}

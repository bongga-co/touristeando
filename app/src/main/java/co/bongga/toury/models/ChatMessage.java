package co.bongga.toury.models;
import java.util.UUID;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by bongga on 1/16/17.
 */

public class ChatMessage extends RealmObject {
    public static final int TEXT_TYPE = 0;
    public static final int IMAGE_TYPE = 1;
    public static final int MAP_TYPE = 2;
    public static final int EVENT_TYPE = 3;
    public static final int PLACES_TYPE = 4;

    @PrimaryKey
    private String id;
    private boolean isSelf;
    private String message;
    private int layout_type;
    private RealmList<Event> event;

    public ChatMessage(){

    }

    public ChatMessage(String message, boolean isSelf, int layout_type){
        this.id = UUID.randomUUID().toString();
        this.message = message;
        this.isSelf = isSelf;
        this.layout_type = layout_type;
    }

    public ChatMessage(RealmList<Event> event, boolean isSelf, int layout_type){
        this.id = UUID.randomUUID().toString();
        this.event = event;
        this.isSelf = isSelf;
        this.layout_type = layout_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public RealmList<Event> getEvent() {
        return event;
    }

    public void setEvent(RealmList<Event> event) {
        this.event = event;
    }
}

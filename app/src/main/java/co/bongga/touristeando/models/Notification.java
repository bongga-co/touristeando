package co.bongga.touristeando.models;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by bongga on 2/13/17.
 */

public class Notification extends RealmObject {
    public static final int OFFER_TYPE = 1;
    public static final int GNAL_TYPE = 2;

    @PrimaryKey
    private String id;
    private String title;
    private String description;
    private String expityDate;
    private float discount;
    private String image;
    private int type;

    public Notification() {
        this.id = UUID.randomUUID().toString();
    }

    public Notification(String title, String description, String expityDate, float discount, int type) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.expityDate = expityDate;
        this.discount = discount;
        this.type = type;
    }

    public Notification(String title, String description, String image, int type) {
        this.id = UUID.randomUUID().toString();
        this.image = image;
        this.title = title;
        this.description = description;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpityDate() {
        return expityDate;
    }

    public void setExpityDate(String expityDate) {
        this.expityDate = expityDate;
    }

    public float getDiscount() {
        return discount;
    }

    public void setDiscount(float discount) {
        this.discount = discount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

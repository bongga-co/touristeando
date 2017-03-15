package co.bongga.touristeando.models;

import io.realm.RealmObject;

/**
 * Created by bongga on 3/15/17.
 */

public class Gallery {
    private String urlSmall;
    private String urlLarge;
    private long publishedDate;

    public Gallery() {
    }

    public Gallery(String urlSmall, String urlLarge, long publishedDate) {
        this.urlSmall = urlSmall;
        this.urlLarge = urlLarge;
        this.publishedDate = publishedDate;
    }

    public String getUrlSmall() {
        return urlSmall;
    }

    public void setUrlSmall(String urlSmall) {
        this.urlSmall = urlSmall;
    }

    public String getUrlLarge() {
        return urlLarge;
    }

    public void setUrlLarge(String urlLarge) {
        this.urlLarge = urlLarge;
    }

    public long getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(long publishedDate) {
        this.publishedDate = publishedDate;
    }
}

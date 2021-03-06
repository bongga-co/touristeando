package co.bongga.touristeando.models;

import android.graphics.Bitmap;

import io.realm.RealmObject;

/**
 * Created by bongga on 3/15/17.
 */

public class GalleryItem {
    private String urlSmall;
    private String urlLarge;
    private long publishedDate;
    private Bitmap bitmap;
    private String user;

    public GalleryItem() {
    }

    public GalleryItem(String urlSmall, String urlLarge, long publishedDate) {
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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}

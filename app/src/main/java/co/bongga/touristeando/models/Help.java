package co.bongga.touristeando.models;

import io.realm.RealmObject;

/**
 * Created by bongga on 3/3/17.
 */

public class Help extends RealmObject {
    private String icon;
    private String title;
    private String example;

    public Help() {
    }

    public Help(String icon, String title, String example) {
        this.icon = icon;
        this.title = title;
        this.example = example;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }
}

package co.bongga.touristeando.models;

import io.realm.RealmObject;

/**
 * Created by bongga on 2/8/17.
 */

public class Service extends RealmObject {
    private String name;

    public Service() {
    }

    public Service(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

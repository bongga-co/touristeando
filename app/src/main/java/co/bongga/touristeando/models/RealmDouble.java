package co.bongga.touristeando.models;

import io.realm.RealmObject;

/**
 * Created by bongga on 2/25/17.
 */

public class RealmDouble extends RealmObject {
    private Double val;

    public RealmDouble() {
    }

    public RealmDouble(Double val) {
        this.val = val;
    }

    public Double getVal() {
        return val;
    }

    public void setVal(Double val) {
        this.val = val;
    }
}
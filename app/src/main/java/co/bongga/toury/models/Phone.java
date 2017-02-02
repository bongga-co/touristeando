package co.bongga.toury.models;

import io.realm.RealmObject;

/**
 * Created by bongga on 2/2/17.
 */

public class Phone extends RealmObject {
    private long cell;
    private long phone;

    public Phone() {
    }

    public Phone(long cell, long phone) {
        this.cell = cell;
        this.phone = phone;
    }

    public long getCell() {
        return cell;
    }

    public void setCell(long cell) {
        this.cell = cell;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }
}

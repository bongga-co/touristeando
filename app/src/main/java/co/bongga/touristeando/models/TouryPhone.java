package co.bongga.touristeando.models;

/**
 * Created by bongga on 3/25/17.
 */

public class TouryPhone {
    private long cell;
    private String phone;

    public TouryPhone() {
    }

    public TouryPhone(long cell, String phone) {
        this.cell = cell;
        this.phone = phone;
    }

    public long getCell() {
        return cell;
    }

    public void setCell(long cell) {
        this.cell = cell;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

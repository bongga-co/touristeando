package co.bongga.toury.models;

import io.realm.RealmObject;

/**
 * Created by bongga on 12/23/16.
 */

public class Price extends RealmObject {
    private String currency;
    private Long value;

    public Price(){

    }

    public Price(String currency, Long value) {
        this.currency = currency;
        this.value = value;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}

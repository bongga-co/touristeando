package co.bongga.touristeando.models;

import io.realm.RealmObject;

/**
 * Created by bongga on 12/23/16.
 */

public class Price extends RealmObject {
    private String currency;
    private Long amount;

    public Price(){

    }

    public Price(String currency, Long amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }
}

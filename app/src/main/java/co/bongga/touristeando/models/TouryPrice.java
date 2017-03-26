package co.bongga.touristeando.models;

/**
 * Created by bongga on 3/25/17.
 */

public class TouryPrice {
    private String currency;
    private Long amount;

    public TouryPrice(){

    }

    public TouryPrice(String currency, Long amount) {
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

package co.bongga.toury.activities;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;

import co.bongga.toury.R;
import co.bongga.toury.models.Place;
import co.bongga.toury.models.Price;
import co.bongga.toury.utils.Globals;

public class PlaceDetail extends AppCompatActivity {
    private ImageView headerImg;
    private TextView placeName;
    private TextView placeCategory;
    private TextView placePrice;
    private RatingBar placeRating;
    private ImageView placeLocationBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        Place place = Globals.currentPlace;
        setTitle(place.getName());

        headerImg = (ImageView) findViewById(R.id.dt_header_img);
        placeName = (TextView) findViewById(R.id.dt_place_name);
        placeCategory = (TextView) findViewById(R.id.dt_place_category);
        placeRating = (RatingBar) findViewById(R.id.dt_rating);
        placePrice = (TextView) findViewById(R.id.dt_price);
        placeLocationBg = (ImageView) findViewById(R.id.dt_location_bg);

        setupUI(place);
    }

    private void setupUI(Place place){
        Glide.with(this).load(place.getThumbnail())
            .crossFade()
            .placeholder(R.drawable.placeholder_img)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(headerImg);

        placeName.setText(place.getName());
        placeCategory.setText(place.getCategory());
        placeRating.setRating(place.getRating());
        placePrice.setText(getString(R.string.dt_place_price, setCurrencySymbol(place.getPrice().getCurrency()), setCurrencyFormat(place.getPrice())));

        String staticMapImageUrl = "https://maps.googleapis.com/maps/api/staticmap?center=" +
                place.getCoordinates().getLatitude()+"," +place.getCoordinates().getLongitude() +
                "&zoom=17&size=640x640&scale=2";

        Glide.with(this).load(staticMapImageUrl)
                .crossFade()
                .placeholder(R.drawable.placeholder_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(placeLocationBg);
    }

    private String setCurrencyFormat(Price price){
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(0);
        Currency currency = Currency.getInstance(price.getCurrency());
        format.setCurrency(currency);

        String amountFormatted = format.format(price.getAmount());

        return amountFormatted;
    }

    private String setCurrencySymbol(String currencySymbol){
        String symbol = "";

        switch (currencySymbol){
            case "COP":
                symbol = "$";
                break;

            case "USD":
                symbol = "U$";
                break;

            case "EUR":
                symbol = "€";
                break;
        }

        return symbol;
    }
}

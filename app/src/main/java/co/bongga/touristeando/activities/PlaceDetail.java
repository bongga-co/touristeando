package co.bongga.touristeando.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import co.bongga.touristeando.R;
import co.bongga.touristeando.models.Coordinate;
import co.bongga.touristeando.models.Place;
import co.bongga.touristeando.models.Price;
import co.bongga.touristeando.utils.Constants;
import co.bongga.touristeando.utils.Globals;
import co.bongga.touristeando.utils.PreferencesManager;

public class PlaceDetail extends AppCompatActivity implements View.OnClickListener {
    private ScrollView scrollView;
    private ImageView headerImg;
    private TextView placeName;
    private TextView placeCategory;
    private TextView placePrice;
    private RatingBar placeRating;
    private ImageView placeLocationBg;
    private TextView placeDescription;
    private Button btnToggleDescription;
    private View descriptionSeparator;
    private TextView placeCity;
    private  TextView placeCityPlace;
    private TextView placeAddress;
    private TextView placeDistance;
    private TextView placePhone;

    private static int isExpanded = 0;
    private static String shortenDescription;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        preferencesManager = new PreferencesManager(this);

        Place place = Globals.currentPlace;
        setTitle(place.getName());

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        headerImg = (ImageView) findViewById(R.id.dt_header_img);
        placeName = (TextView) findViewById(R.id.dt_place_name);
        placeCategory = (TextView) findViewById(R.id.dt_place_category);
        placeRating = (RatingBar) findViewById(R.id.dt_rating);
        placePrice = (TextView) findViewById(R.id.dt_price);
        placeLocationBg = (ImageView) findViewById(R.id.dt_location_bg);
        placeDescription = (TextView) findViewById(R.id.dt_place_description);
        btnToggleDescription = (Button) findViewById(R.id.btn_toggle_description);
        btnToggleDescription.setOnClickListener(this);
        descriptionSeparator = findViewById(R.id.desc_separator);
        placeCity = (TextView) findViewById(R.id.dt_place_city);
        placeAddress = (TextView) findViewById(R.id.dt_place_address);
        placeCityPlace = (TextView) findViewById(R.id.dt_place_city_place);
        placeDistance = (TextView) findViewById(R.id.dt_place_distance);
        placePhone = (TextView) findViewById(R.id.dt_place_phone);

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

        placeDescription.setText(checkDescriptionLength(place.getDescription()));
        placeCity.setText(place.getCity() + ", " + place.getCountry());
        placeAddress.setText(place.getAddress());
        placeCityPlace.setText(place.getPlace());

        Coordinate userLocation = preferencesManager.getCurrentLocation();
        if(userLocation != null){
            Coordinate placeLocation = place.getCoordinates();
            double distance = calculateDistance(userLocation, placeLocation);

            if(distance < 1){
                placeDistance.setText(String.format(Locale.getDefault(), "%.2f m", distance*1000));
            }
            else{
                placeDistance.setText(String.format(Locale.getDefault(), "%.2f kms", distance));
            }
        }
        else{
            placeDistance.setVisibility(View.GONE);
        }

        long cell = place.getPhone().getCell();
        long phone = place.getPhone().getPhone();

        if(cell == 0 && phone != 0){
            placePhone.setText(String.format(Locale.getDefault(), "%d", phone));
        }
        else if(cell != 0 && phone == 0){
            placePhone.setText(String.format(Locale.getDefault(), "%d", cell));
        }
        else{
            placePhone.setText(String.format(Locale.getDefault(), "%d - %d", cell, phone));
        }
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

    private String checkDescriptionLength(String desc){
        String finalText = null;

        if(desc.length() > Constants.DESCRIPTION_LENGHT){
            finalText = desc.substring(0, Constants.DESCRIPTION_LENGHT) + "...";
            shortenDescription = finalText;

            descriptionSeparator.setVisibility(View.VISIBLE);
            btnToggleDescription.setVisibility(View.VISIBLE);

            //TODO: check
            //scrollView.scrollTo(0, scrollView.getBottom());
        }
        else{
            finalText = desc;
            
            descriptionSeparator.setVisibility(View.GONE);
            btnToggleDescription.setVisibility(View.GONE);
        }

        return finalText;
    }

    private void toggleDescription(){
        switch (isExpanded){
            case 0:
                placeDescription.setText(Globals.currentPlace.getDescription());
                btnToggleDescription.setText(getString(R.string.btn_toggle_place_description_minus));
                isExpanded++;
                break;

            case 1:
                placeDescription.setText(shortenDescription);
                btnToggleDescription.setText(getString(R.string.btn_toggle_place_description));
                isExpanded=0;
                break;
        }
    }

    private double calculateDistance(Coordinate origin, Coordinate destination){
        double R = 6371000f; // Radius of the earth in m
        double dLat = (origin.getLatitude() - destination.getLatitude()) * Math.PI / 180f;
        double dLon = (origin.getLongitude() - destination.getLongitude()) * Math.PI / 180f;

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(origin.getLatitude() * Math.PI / 180f) * Math.cos(destination.getLatitude() * Math.PI / 180f) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2f * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;

        return d/1000f;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_toggle_description:
                toggleDescription();
                break;
        }
    }
}

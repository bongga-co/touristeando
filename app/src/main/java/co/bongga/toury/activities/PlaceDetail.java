package co.bongga.toury.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import co.bongga.toury.R;
import co.bongga.toury.models.Place;
import co.bongga.toury.utils.Globals;

public class PlaceDetail extends AppCompatActivity {
    private ImageView headerImg;
    private TextView placeName;
    private TextView placeCategory;
    private RatingBar placeRating;

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
    }
}

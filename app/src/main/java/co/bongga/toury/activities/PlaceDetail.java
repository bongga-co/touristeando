package co.bongga.toury.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import co.bongga.toury.R;
import co.bongga.toury.models.Event;
import co.bongga.toury.models.Place;
import co.bongga.toury.utils.Globals;

public class PlaceDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        Place place = Globals.currentPlace;

        setTitle(place.getName());
        setupUI(place);
    }

    private void setupUI(Place place){

    }
}

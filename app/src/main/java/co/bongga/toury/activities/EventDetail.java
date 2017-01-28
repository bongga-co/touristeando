package co.bongga.toury.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import co.bongga.toury.R;
import co.bongga.toury.models.Event;
import co.bongga.toury.utils.Globals;

public class EventDetail extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Event event = Globals.currentEvent;
        setTitle(event.getName());

        setupUI(event);
    }

    private void setupUI(Event event){

    }
}

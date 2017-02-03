package co.bongga.touristeando.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import co.bongga.touristeando.R;
import co.bongga.touristeando.models.Event;
import co.bongga.touristeando.utils.Globals;

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

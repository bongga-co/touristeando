package co.bongga.toury.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import co.bongga.toury.R;

public class Splash extends AppCompatActivity {
    private final int TIMER = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setupDelay();
    }

    private void setupDelay(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Splash.this, Main.class));
                finish();
            }
        }, TIMER);
    }
}

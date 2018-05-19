package co.bongga.touristeando.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import co.bongga.touristeando.R;
import co.bongga.touristeando.utils.CircleTransform;
import co.bongga.touristeando.utils.Constants;
import co.bongga.touristeando.utils.Globals;
import co.bongga.touristeando.utils.PreferencesManager;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private ImageView profileImage;
    private TextView profileName, profileEmail;
    private Button btnLogout;

    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencesManager = new PreferencesManager(this);

        if(Globals.loggedUser == null){
            startActivityForResult(new Intent(Profile.this, Login.class), Constants.REQUEST_USER_LOGIN);
        }
        else{
            setupUI();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.REQUEST_USER_LOGIN && resultCode == RESULT_OK){
            if(Globals.loggedUser != null){
                setupUI();
            }
            else{
                closeProfile();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
            closeProfile();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_logout:
                didLogout();
                break;
        }
    }

    private void setupUI(){
        setContentView(R.layout.activity_profile);

        profileImage = (ImageView) findViewById(R.id.profile_image);
        profileName = (TextView) findViewById(R.id.profile_name);
        profileEmail = (TextView) findViewById(R.id.profile_email);

        btnLogout = (Button) findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(this);

        getUserData();
    }

    private void getUserData(){
        Glide.with(this).load(Globals.loggedUser.getPhotoUrl())
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.nav_placeholder_profile)
                .into(profileImage);

        profileName.setText(String.format(Locale.getDefault(), "%s %s", Globals.loggedUser.getFirstName(),
                                                                        Globals.loggedUser.getLastName()));
        profileEmail.setText(Globals.loggedUser.getEmail());
    }

    private void didLogout(){
        FirebaseAuth.getInstance().signOut();

        preferencesManager.setLoggedUser(null);
        Globals.loggedUser = null;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                closeProfile();
            }
        }, 2000);
    }

    private void closeProfile(){
        startActivity(new Intent(Profile.this, Main.class));
        finish();
    }
}

package co.bongga.touristeando.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import co.bongga.touristeando.R;
import co.bongga.touristeando.adapters.GalleryAdapter;
import co.bongga.touristeando.adapters.ServicesAdapter;
import co.bongga.touristeando.interfaces.DataCallback;
import co.bongga.touristeando.models.Coordinate;
import co.bongga.touristeando.models.Gallery;
import co.bongga.touristeando.models.Place;
import co.bongga.touristeando.models.Service;
import co.bongga.touristeando.utils.Constants;
import co.bongga.touristeando.utils.DataManager;
import co.bongga.touristeando.utils.Globals;
import co.bongga.touristeando.utils.PreferencesManager;
import co.bongga.touristeando.utils.UtilityManager;
import io.realm.RealmList;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

public class PlaceDetail extends AppCompatActivity implements View.OnClickListener {
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
    private TextView placeCityPlace;
    private TextView placeAddress;
    private TextView placeDistance;
    private TextView placePhone;
    private ImageButton btnTakeMe;
    private ImageButton btnCallToPlace;

    private LinearLayout servicesLabelWrapper;
    private LinearLayout servicesWrapper;
    private LinearLayout phoneWrapper;
    private LinearLayout locationWrapper;

    private RecyclerView serviceList;

    private ArrayList<Gallery> imageList;
    private GalleryAdapter galleryAdapter;
    private RecyclerView galleryRecycler;
    private TextView emptyGallery;

    private static int isExpanded = 0;
    private static String shortenDescription;
    private PreferencesManager preferencesManager;
    private ServicesAdapter servicesAdapter;
    private RealmList<Service> serviceItems = new RealmList<>();

    private Place place;
    private SessionConfiguration configUber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        preferencesManager = new PreferencesManager(this);

        place = Globals.currentPlace;
        setTitle(place.getName());

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

        btnTakeMe = (ImageButton) findViewById(R.id.btn_take_me_to_place);
        btnTakeMe.setOnClickListener(this);

        btnCallToPlace = (ImageButton) findViewById(R.id.btn_call_to_place);
        btnCallToPlace.setOnClickListener(this);

        servicesLabelWrapper = (LinearLayout) findViewById(R.id.dt_place_services_label_wrapper);
        servicesWrapper = (LinearLayout) findViewById(R.id.dt_place_services_wrapper);

        phoneWrapper = (LinearLayout) findViewById(R.id.phoneWrapper);
        locationWrapper = (LinearLayout) findViewById(R.id.locationWrapper);

        serviceList = (RecyclerView) findViewById(R.id.dt_services_list);
        serviceList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        serviceList.setItemAnimator(new DefaultItemAnimator());

        //Gallery Section
        galleryRecycler = (RecyclerView) findViewById(R.id.dt_gallery_list);

        imageList = new ArrayList<>();
        galleryAdapter = new GalleryAdapter(getApplicationContext(), imageList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        galleryRecycler.setLayoutManager(mLayoutManager);
        galleryRecycler.setItemAnimator(new DefaultItemAnimator());
        galleryRecycler.setAdapter(galleryAdapter);

        emptyGallery = (TextView) findViewById(R.id.dt_place_gallery_empty);

        //Services Section
        servicesAdapter = new ServicesAdapter(this, serviceItems);
        serviceList.setAdapter(servicesAdapter);

        setupUI();
        setupUber();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.place_detail, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ic_action_invite_friend) {
            return false;
        }
        else if (id == R.id.ic_action_share) {
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_toggle_description:
                toggleDescription();
                break;
            case R.id.btn_take_me_to_place:
                takeMeToThePlace();
                break;
            case R.id.btn_call_to_place:
                requestPhoneCallPermission();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case Constants.REQUEST_CALL_PHONE_PERMISSION:
                    callToPlace();
                    break;
            }
        } else {
            UtilityManager.showMessage(btnCallToPlace, getString(R.string.permission_denied));
        }
    }

    private void setupUI() {
        System.out.println(place.getId());

        Glide.with(this).load(place.getThumbnail())
                .crossFade()
                .placeholder(R.drawable.placeholder_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(headerImg);

        placeName.setText(place.getName());
        placeCategory.setText(place.getCategory());
        placeRating.setRating(place.getRating());

        if (place.getPrice() == null || place.getPrice().getAmount() == 0) {
            placePrice.setText(getString(R.string.free_label));
        }
        else if (place.getPrice().getAmount() < 0) {
            placePrice.setText(getString(R.string.undefined_price));
        }
        else {
            placePrice.setText(getString(R.string.dt_place_price, UtilityManager.setCurrencySymbol(place.getPrice().getCurrency()), UtilityManager.setCurrencyFormat(place.getPrice())));
        }

        String staticMapImageUrl = "https://maps.googleapis.com/maps/api/staticmap?center=" +
                place.getCoordinates().getLatitude() + "," + place.getCoordinates().getLongitude() +
                "&zoom=19&size=1200x450&scale=2";

        Glide.with(this).load(staticMapImageUrl)
                .crossFade()
                .placeholder(R.drawable.placeholder_img)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(placeLocationBg);

        placeDescription.setText(checkDescriptionLength(place.getDescription()));
        placeCity.setText(place.getCity() + ", " + place.getCountry());
        placeAddress.setText(place.getAddress());

        if (place.getPlace() != null && !place.getPlace().isEmpty()) {
            placeCityPlace.setVisibility(View.VISIBLE);
            placeCityPlace.setText(place.getPlace());
        }
        else {
            placeCityPlace.setVisibility(View.GONE);
        }

        //Show distance
        if(place.getDistance() != 0){
            double distance = place.getDistance();
            if (distance < 1) {
                placeDistance.setText(String.format(Locale.getDefault(), "%.0f mts", distance * 1000));
            }
            else {
                placeDistance.setText(String.format(Locale.getDefault(), "%.2f kms", distance));
            }
        }
        else {
            locationWrapper.setVisibility(View.GONE);
            placeDistance.setVisibility(View.GONE);
        }

        long cell = place.getPhone().getCell();
        String phone = place.getPhone().getPhone();

        if (cell == 0 && phone.equals("0")) {
            phoneWrapper.setVisibility(View.GONE);
            placePhone.setVisibility(View.GONE);
        }
        else if (cell == 0 && !phone.equals("0")) {
            placePhone.setText(String.format(Locale.getDefault(), "%s", phone));
        }
        else if (cell != 0 && phone.equals("0")) {
            placePhone.setText(String.format(Locale.getDefault(), "%s", cell));
        }
        else {
            placePhone.setText(String.format(Locale.getDefault(), "%s - %s", cell, phone));
        }

        if (place.getServices() != null && place.getServices().size() > 0) {
            servicesLabelWrapper.setVisibility(View.VISIBLE);
            servicesWrapper.setVisibility(View.VISIBLE);
            for (Service service : place.getServices()) {
                serviceItems.add(service);
            }
            servicesAdapter.notifyDataSetChanged();
        }

        fetchGalleryImages();
    }

    private void takeMeToThePlace() {
        final double lat = place.getCoordinates().getLatitude();
        final double lng = place.getCoordinates().getLongitude();
        Coordinate cLocation = preferencesManager.getCurrentLocation();

        if (cLocation != null) {
            final double currentLat = cLocation.getLatitude();
            final double currentLng = cLocation.getLongitude();

            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getResources().getString(R.string.show_route_title));
            dialog.setMessage(getResources().getString(R.string.show_route_msg));
            dialog.setPositiveButton(getResources().getString(R.string.take_me), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=" +
                                    currentLat + "," +
                                    currentLng +
                                    "&daddr=" + lat + "," + lng + "&mode=driving"));
                    startActivity(intent);
                }
            });
            /*dialog.setNeutralButton(getResources().getString(R.string.dt_request_uber), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    showUberView();
                }
            });*/
            dialog.setNegativeButton(getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            });

            final AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        }
    }

    private void requestPhoneCallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.CALL_PHONE
                }, Constants.REQUEST_CALL_PHONE_PERMISSION);
            }
            else {
                callToPlace();
            }
        }
        else {
            callToPlace();
        }
    }

    private void callToPlace() {
        List<Object> phones = new ArrayList<>();

        final String phone = place.getPhone().getPhone();
        final long cell = place.getPhone().getCell();

        if (!phone.equals("0")) {
            phones.add(phone);
        }

        if (cell != 0) {
            phones.add(String.valueOf(cell));
        }

        final CharSequence[] phoneItems = phones.toArray(new CharSequence[phones.size()]);
        if (phoneItems.length > 0) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(String.format(Locale.getDefault(), "%s %s", getString(R.string.call_phone_msg), place.getName()))
                    .setItems(phoneItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + phoneItems[i]));

                            if (ActivityCompat.checkSelfPermission(PlaceDetail.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            startActivity(intent);

                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton(getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

            final AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private String checkDescriptionLength(String desc){
        String finalText;

        if(desc.length() > Constants.DESCRIPTION_LENGHT){
            finalText = desc.substring(0, Constants.DESCRIPTION_LENGHT) + "...";
            shortenDescription = finalText;

            descriptionSeparator.setVisibility(View.VISIBLE);
            btnToggleDescription.setVisibility(View.VISIBLE);
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

    private void fetchGalleryImages(){
        String id = place.getId();
        DataManager.willGetPlaceGallery(id, new DataCallback() {
            @Override
            public void didReceiveData(List<Object> response) {
            if(response != null){
                List<Gallery> data = UtilityManager.objectFilter(response, Gallery.class);
                if(data.size() > 0){
                    emptyGallery.setVisibility(View.GONE);
                    galleryRecycler.setVisibility(View.VISIBLE);

                    for(Gallery galleryItem : data){
                        imageList.add(galleryItem);
                    }

                    galleryAdapter.notifyDataSetChanged();
                }
                else{
                    emptyGallery.setVisibility(View.VISIBLE);
                    galleryRecycler.setVisibility(View.GONE);
                }
            }
            else{
                emptyGallery.setVisibility(View.VISIBLE);
                galleryRecycler.setVisibility(View.GONE);
            }
            }
        });
    }

    private void setupUber(){
        configUber = new SessionConfiguration.Builder()
                .setClientId(Constants.UBER_CLIENT_ID)
                .setRedirectUri(Constants.UBER_REDIRECT_URL)
                .setScopes(Arrays.asList(Scope.PROFILE, Scope.RIDE_WIDGETS))
                .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                .build();

        UberSdk.initialize(configUber);
    }

    private void showUberView(){

    }
}

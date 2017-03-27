package co.bongga.touristeando.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import co.bongga.touristeando.R;
import co.bongga.touristeando.models.Coordinate;
import co.bongga.touristeando.models.PlaceCategory;
import co.bongga.touristeando.models.TouryCoordinate;
import co.bongga.touristeando.models.TouryPhone;
import co.bongga.touristeando.models.TouryPlace;
import co.bongga.touristeando.models.TouryPrice;
import co.bongga.touristeando.utils.Constants;
import co.bongga.touristeando.utils.Globals;
import co.bongga.touristeando.utils.InputFilterForEditText;
import co.bongga.touristeando.utils.LocManager;
import co.bongga.touristeando.utils.PreferencesManager;
import co.bongga.touristeando.utils.UtilityManager;

public class Recommend extends AppCompatActivity implements OnMapReadyCallback, View.OnFocusChangeListener {
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;

    private ProgressDialog loader;

    private List<PlaceCategory> categoryList = new ArrayList<>();

    private Spinner spCategory, spCurrency;
    private EditText etName, etDescription, etAddress, etCity, etLandmark,
            etCountry, etPhone, etCell, etEmail, etThumbLink, etPrice;
    private Switch checkThumb, checkPrice;
    private ImageView imThumb;
    private TextInputLayout linkWrapper;
    private TextView tvPrice;
    private SeekBar skPrice;
    private ScrollView scrollView;
    private ImageButton toggleLocation;

    private GoogleMap map;
    private LatLng placeCoordinate;
    private static Bitmap headerImage;
    private static long price = 0;

    private LocManager locManager;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        setupMap();

        loader = UtilityManager.showLoader(this, getString(R.string.loader_message));
        locManager = new LocManager(this);
        preferencesManager = new PreferencesManager(this);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storage = FirebaseStorage.getInstance();

        scrollView = (ScrollView) findViewById(R.id.rc_scroll);

        spCategory = (Spinner) findViewById(R.id.rc_place_category);
        spCurrency = (Spinner) findViewById(R.id.rc_place_currency);

        etAddress = (EditText) findViewById(R.id.rc_place_address);
        etAddress.setOnFocusChangeListener(this);

        etCity = (EditText) findViewById(R.id.rc_place_city);
        etCity.setOnFocusChangeListener(this);

        etName = (EditText) findViewById(R.id.rc_place_name);
        etName.setOnFocusChangeListener(this);

        etDescription = (EditText) findViewById(R.id.rc_place_description);
        etLandmark = (EditText) findViewById(R.id.rc_place_landmark);
        etPhone = (EditText) findViewById(R.id.rc_place_phone);
        etCell = (EditText) findViewById(R.id.rc_place_cell);
        etEmail = (EditText) findViewById(R.id.rc_place_email);

        checkPrice = (Switch) findViewById(R.id.rc_check_price);
        checkPrice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    etPrice.setEnabled(false);
                    tvPrice.setText(getString(R.string.undefined_price));
                    price = -1;
                }
                else{
                    etPrice.setEnabled(true);
                    if(!etPrice.getText().toString().isEmpty()){
                        price = Integer.parseInt(etPrice.getText().toString());

                        if(price == 0){
                            tvPrice.setText(getString(R.string.free_label));
                        }
                        else{
                            tvPrice.setText(String.format(Locale.getDefault(), "$ %s", price));
                        }
                    }
                    else{
                        price = 0;
                        tvPrice.setText(getString(R.string.free_label));
                    }
                }
            }
        });

        tvPrice = (TextView) findViewById(R.id.rc_place_price);

        //TODO: Hidden
        skPrice = (SeekBar) findViewById(R.id.rc_place_price_range);
        skPrice.incrementProgressBy(50);
        skPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = progress / 50;
                progress = progress * 50;

                if(progress == 0){
                    tvPrice.setText(getString(R.string.free_label));
                }
                else{
                    tvPrice.setText(String.format(Locale.getDefault(), "$ %s", progress));
                }

                price = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        etPrice = (EditText) findViewById(R.id.rc_place_price_box);
        etPrice.setFilters(new InputFilter[]{ new InputFilterForEditText("0", "1500000")});
        etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!etPrice.getText().toString().isEmpty()){
                    int price = Integer.parseInt(etPrice.getText().toString());

                    if(price == 0){
                        tvPrice.setText(getString(R.string.free_label));
                    }
                    else{
                        tvPrice.setText(String.format(Locale.getDefault(), "$ %s", price));
                    }
                }
                else{
                    price = 0;
                    tvPrice.setText(getString(R.string.free_label));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCountry = (EditText) findViewById(R.id.rc_place_country);
        etCountry.setOnFocusChangeListener(this);

        etThumbLink = (EditText) findViewById(R.id.rc_place_link_thumb);
        etThumbLink.setOnFocusChangeListener(this);

        linkWrapper = (TextInputLayout) findViewById(R.id.rc_place_link_thumb_wrapper);

        imThumb = (ImageView) findViewById(R.id.rc_place_thumb);
        imThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseGetImageOption();
            }
        });

        checkThumb = (Switch) findViewById(R.id.rc_check_thumb);
        checkThumb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    imThumb.setEnabled(false);
                    linkWrapper.setVisibility(View.VISIBLE);
                }
                else{
                    imThumb.setEnabled(true);
                    linkWrapper.setVisibility(View.GONE);
                }
            }
        });

        toggleLocation = (ImageButton) findViewById(R.id.rc_place_toggle_location);
        toggleLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestForLocationPermission();

                toggleLocation.setEnabled(false);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       toggleLocation.setEnabled(true);
                    }
                }, 3000);
            }
        });

        getCategories();
    }

    @Override
    protected void onPause() {
        super.onPause();
        loader.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recommend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.ic_action_save_place:
                savePlace();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        EditText field = (EditText) v;

        switch (id){
            case R.id.rc_place_address:
            case R.id.rc_place_city:
                setPlaceLocation(hasFocus);
                break;
            case R.id.rc_place_link_thumb:
                String url = etThumbLink.getText().toString().trim();
                if(!url.isEmpty() && Patterns.WEB_URL.matcher(url).matches()){
                    new ImagePreview().execute(url);
                }
                break;
            case R.id.rc_place_name:
            case R.id.rc_place_country:
                break;
        }

        if(!field.getText().toString().trim().isEmpty()){
            field.setError(null);
        }
        else {
            field.setError(getString(R.string.missing_info));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case Constants.REQUEST_PICK_IMAGE_GALLERY:
                    chooseFromGallery();
                    break;
                case Constants.REQUEST_COARSE_LOCATION_PERMISSION:
                case Constants.REQUEST_FINE_LOCATION_PERMISSION:
                    requestForLocationSettings();
                    break;
            }
        }
        else {
            UtilityManager.showMessage(imThumb, getString(R.string.permission_denied));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.REQUEST_PICK_IMAGE_GALLERY && resultCode == RESULT_OK){
            if(data != null){
                try {
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                    if(selectedImage != null){
                        headerImage = UtilityManager.resizeImage(selectedImage);
                        imThumb.setImageBitmap(headerImage);
                    }
                    else{
                        UtilityManager.showMessage(imThumb, getString(R.string.not_file_found));
                    }
                }
                catch (FileNotFoundException e) {
                    UtilityManager.showMessage(imThumb, getString(R.string.not_file_found));
                }
            }
        }
        else if(requestCode == Constants.REQUEST_PICK_IMAGE_CAMERA && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap capturedPicture = (Bitmap) extras.get("data");

            if(capturedPicture != null){
                headerImage = UtilityManager.resizeImage(capturedPicture);
                imThumb.setImageBitmap(headerImage);
            }
        }
        else if(requestCode == Constants.REQUEST_CHECK_SETTINGS){
            loader.show();

            switch (resultCode) {
                case Activity.RESULT_OK:
                    waitingForLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    loader.dismiss();
                    didShowLocationError();
                    break;
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setupMap(){
        final SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.rc_place_map);
        mapFragment.getMapAsync(this);
    }

    private void getCategories(){
        loader.show();
        DatabaseReference categoriesRef = databaseReference.child("place_categories");
        categoriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                categoryList.clear();
                final List<String> categoryName = new ArrayList<>();

                for(DataSnapshot item : dataSnapshot.getChildren()){
                    PlaceCategory category = item.getValue(PlaceCategory.class);
                    categoryList.add(category);
                    categoryName.add(category.getName());
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Recommend.this,
                        android.R.layout.simple_spinner_item, categoryName);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCategory.setAdapter(dataAdapter);

                loader.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loader.dismiss();
                UtilityManager.showMessage(spCategory, getString(R.string.no_categories_data));
            }
        });
    }

    private void setPlaceLocation(boolean hasFocus){
        if(!hasFocus){
            loader.show();

            String city = etCity.getText().toString();
            String address = etAddress.getText().toString();

            if(!address.isEmpty() && !city.isEmpty()){
                if(address != null){
                    Address currentAddress = UtilityManager.getLocationFromAddress(getApplicationContext(),
                            String.format(Locale.getDefault(), "%s, %s", address, city));

                    LatLng location = new LatLng(currentAddress.getLatitude(), currentAddress.getLongitude());

                    if(map != null){
                        map.clear();

                        map.addMarker(new MarkerOptions().position(location));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));

                        placeCoordinate = location;
                        etCountry.setText(currentAddress.getCountryName());
                    }
                }
            }

            loader.dismiss();
        }
    }

    private void chooseGetImageOption(){
        CharSequence itemsDialog[];

        if(headerImage != null){
            itemsDialog = new CharSequence[]{
                    getString(R.string.choose_from_camera),
                    getString(R.string.choose_from_library),
                    getString(R.string.remove_image)};
        }
        else{
            itemsDialog = new CharSequence[]{
                    getString(R.string.choose_from_camera),
                    getString(R.string.choose_from_library)};
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.choose_image_lbl))
                .setIcon(R.mipmap.ic_launcher)
                .setItems(itemsDialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 0){
                            requestCameraPermission();
                        }
                        else if(which == 1){
                            chooseFromGallery();
                        }
                        else {
                            headerImage = null;
                            imThumb.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.placeholder_img));
                        }
                    }
                });

        dialog.show();
    }

    private void chooseFromGallery(){
        Intent pickerImage = new Intent(Intent.ACTION_GET_CONTENT);
        pickerImage.setType("image/*");
        startActivityForResult(pickerImage, Constants.REQUEST_PICK_IMAGE_GALLERY);
    }

    private void chooseFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, Constants.REQUEST_PICK_IMAGE_CAMERA);
        }
    }

    private void requestCameraPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, 0);
            }
            else {
                chooseFromCamera();
            }
        }
        else{
            chooseFromCamera();
        }
    }

    private void savePlace() {
        String name = etName.getText().toString().trim();
        if(name.isEmpty()){
            etName.setError(getString(R.string.missing_info));
            return;
        }
        else{
            etName.setError(null);
        }

        String description = etDescription.getText().toString().trim();
        if(description.isEmpty()){
            description = null;
        }

        String category = spCategory.getSelectedItem().toString();
        String thing_to_do = setThingToDo(category);

        String address = etAddress.getText().toString().trim();
        if(address.isEmpty()){
            etAddress.setError(getString(R.string.missing_info));
            return;
        }
        else{
            etAddress.setError(null);
        }

        String landmark = etLandmark.getText().toString().trim();
        if(landmark.isEmpty()){
            landmark = null;
        }

        String city = etCity.getText().toString().trim();
        if(city.isEmpty()){
            etCity.setError(getString(R.string.missing_info));
            return;
        }
        else{
            etCity.setError(null);
        }

        String country = etCountry.getText().toString().trim();
        if(country.isEmpty()){
            etCountry.setError(getString(R.string.missing_info));
            return;
        }
        else{
            etCountry.setError(null);
        }

        String phone = etPhone.getText().toString().trim();
        if(phone.isEmpty()){
            phone = "0";
        }

        String cellString = etCell.getText().toString().trim();
        long cell = 0;

        if(!cellString.isEmpty()){
            cell = Long.valueOf(cellString);
        }

        String email = etEmail.getText().toString().trim();
        if(email.isEmpty()){
            email = null;
        }
        else{
            email = UtilityManager.removeAccents(email);
        }

        String currency = spCurrency.getSelectedItem().toString();

        String thumb = null;
        if(checkThumb.isChecked() && !etThumbLink.getText().toString().isEmpty()){
            thumb = etThumbLink.getText().toString().trim();
        }

        if(headerImage == null){
            UtilityManager.showMessage(imThumb, getString(R.string.choose_img_header));
            return;
        }

        TouryPlace place = new TouryPlace(
            name,
            category,
            thumb,
            email,
            0,
            new TouryPrice(currency, price),
            new TouryCoordinate(placeCoordinate.latitude, placeCoordinate.longitude),
            description,
            city,
            country,
            thing_to_do,
            address,
            landmark,
            new TouryPhone(cell, phone),
            false,
            Globals.loggedUser.getId()
        );

        loader.show();
        databaseReference.child("places").push().setValue(place, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                loader.dismiss();

                if(databaseError == null){
                    if(!checkThumb.isChecked()){
                        String key = databaseReference.getKey();
                        uploadPicture(headerImage, key);
                    }
                    else{
                        UtilityManager.showMessage(imThumb, getString(R.string.place_saved));
                        clearFields();
                    }
                }
                else{
                    UtilityManager.showMessage(etName, getString(R.string.place_not_saved));
                }
            }
        });
    }

    private void uploadPicture(Bitmap original, final String key){
        if(!UtilityManager.isConnected(this)){
            UtilityManager.showMessage(imThumb, getString(R.string.no_network_connection));
            return;
        }

        loader.show();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        original.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageRef = storage.getReferenceFromUrl(Constants.FB_STORAGE_URL);
        StorageReference imageRef = storageRef.child("places/" + key + "/" + UUID.randomUUID()+".jpg");

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                loader.dismiss();
                UtilityManager.showMessage(imThumb, getString(R.string.image_upload_error));
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                if(downloadUrl != null){
                    String url = downloadUrl.toString();
                    databaseReference.child("places").child(key).child("thumbnail").setValue(url, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null){
                                UtilityManager.showMessage(imThumb, getString(R.string.place_not_saved));
                            }
                            else{
                                UtilityManager.showMessage(imThumb, getString(R.string.place_saved));
                                clearFields();
                            }
                        }
                    });
                }
                else{
                    UtilityManager.showMessage(imThumb, getString(R.string.generic_unexpected_error));
                }

                loader.dismiss();
            }
        });
    }

    private String setThingToDo(String category){
        String thing = null;

        for(PlaceCategory item : categoryList){
            if(item.getName().equals(category)){
                thing = item.getThing();
            }
        }

        return thing;
    }

    private void clearFields(){
        etName.setText(null);
        spCategory.setSelection(0, true);
        etDescription.setText(null);
        checkThumb.setChecked(false);
        imThumb.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.placeholder_img));
        etAddress.setText(null);
        etLandmark.setText(null);
        etCity.setText(null);
        etCountry.setText(null);
        map.clear();
        etPhone.setText(null);
        etCell.setText(null);
        etEmail.setText(null);
        spCurrency.setSelection(0, true);
        checkPrice.setChecked(false);
        skPrice.setProgress(0);
        headerImage = null;
        price = 0;
        placeCoordinate = null;

        scrollView.smoothScrollTo(0, 0);
    }

    private void requestForLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                }, Constants.REQUEST_FINE_LOCATION_PERMISSION);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                }, Constants.REQUEST_COARSE_LOCATION_PERMISSION);
            } else {
                requestForLocationSettings();
            }
        } else {
            requestForLocationSettings();
        }
    }

    protected void requestForLocationSettings() {
        if(!locManager.setupLocation()){
            UtilityManager.showMessage(toggleLocation, getString(R.string.no_location_services));
            locManager.deleteLocation();
        }
        else {
            PendingResult<LocationSettingsResult> result = locManager.buildLocationSetting();
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                    final Status status = locationSettingsResult.getStatus();

                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            getUserLocation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                status.startResolutionForResult(Recommend.this, Constants.REQUEST_CHECK_SETTINGS);
                            }
                            catch (IntentSender.SendIntentException e) {
                                didShowLocationError();
                            }
                            break;
                        default:
                            didShowLocationError();
                            break;
                    }
                }
            });
        }
    }

    private void waitingForLocation(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loader.dismiss();
                getUserLocation();
            }
        }, 3000);
    }

    private void getUserLocation(){
        final Coordinate location = preferencesManager.getCurrentLocation();
        if(location != null){
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            placeCoordinate = latLng;

            Address address = UtilityManager.getAddressFromLocation(this, location.getLatitude(), location.getLongitude());

            if(address != null){
                etCity.setText(address.getLocality());
                etCountry.setText(address.getCountryName());

                if(address.getMaxAddressLineIndex() > 0){
                    etAddress.setText(address.getAddressLine(0));
                }

                map.clear();

                map.addMarker(new MarkerOptions().position(latLng));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        }
        else {
            didShowLocationError();
        }
    }

    private void didShowLocationError(){
        UtilityManager.showMessage(toggleLocation, getString(R.string.no_location_found));
    }

    private class ImagePreview extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loader.show();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            return UtilityManager.getBitmapFromURL(url);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            loader.dismiss();
            imThumb.setImageBitmap(bitmap);
            headerImage = bitmap;
        }
    }
}

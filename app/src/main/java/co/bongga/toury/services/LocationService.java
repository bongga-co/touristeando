package co.bongga.toury.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import co.bongga.toury.models.Coordinate;
import co.bongga.toury.utils.PreferencesManager;

/**
 * Created by bongga on 1/23/17.
 */

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final IBinder mBinder = new LocalBinder();

    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private LocationSettingsRequest locationSettingsRequest;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private PreferencesManager preferencesManager;
    private int counter = 0;

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferencesManager = new PreferencesManager(this);

        if(isPlayServicesEnable()){
            buildGoogleApiClient();
            createLocationRequest();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        connectAPIClient();
        return mBinder;
    }

    @Override
    public void onDestroy() {
        if(googleApiClient != null){
            googleApiClient.unregisterConnectionCallbacks(this);
            googleApiClient.unregisterConnectionFailedListener(this);
            googleApiClient.disconnect();
            googleApiClient = null;
        }

        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        stopLocationUpdates();
        googleApiClient = null;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(connectionResult.hasResolution()){

        }
        else{

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if(lastLocation != null){
            saveCurrentLocation();
        }
    }

    private boolean isPlayServicesEnable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient,
                    locationRequest,
                    this
            );
        }
    }

    private void connectAPIClient(){
        if(googleApiClient != null){
            if(!googleApiClient.isConnected()){
                googleApiClient.connect();
            }
        }
    }

    public void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient,
                this
        );
    }

    private void saveCurrentLocation(){
        Coordinate coordinate = new Coordinate();

        coordinate.setLatitude(lastLocation.getLatitude());
        coordinate.setLongitude(lastLocation.getLongitude());

        preferencesManager.setCurrentLocation(coordinate);

        if(counter >= 4){
            stopLocationUpdates();
        }

        counter++;
    }

    public LocationSettingsRequest buildLocationSetting() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);
        locationSettingsRequest = builder.build();

        return locationSettingsRequest;
    }

    public GoogleApiClient getGoogleApiClient(){
        return googleApiClient;
    }
}

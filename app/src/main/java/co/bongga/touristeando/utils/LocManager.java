package co.bongga.touristeando.utils;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import co.bongga.touristeando.models.Coordinate;

/**
 * Created by bongga on 2/18/17.
 */

public class LocManager implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Activity context;

    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private PreferencesManager preferencesManager;
    private Coordinate coordinate;
    private int counter = 0;

    public LocManager(Activity context){
        this.context = context;

        this.preferencesManager = new PreferencesManager(context);
        coordinate = new Coordinate();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if(connectionResult.hasResolution()){}
        else{}
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if(lastLocation != null){
            saveCurrentLocation();
        }
    }

    public boolean setupLocation(){
        boolean isEnabled = isPlayServicesEnable();

        if(isEnabled){
            buildGoogleApiClient();
            createLocationRequest();
        }

        return isEnabled;
    }

    public void disconnectLocation(){
        if(googleApiClient != null && googleApiClient.isConnected()){
            googleApiClient.disconnect();
            stopLocationUpdates();
        }
    }

    public void deleteLocation(){
        if(googleApiClient != null){
            googleApiClient.unregisterConnectionCallbacks(this);
            googleApiClient.unregisterConnectionFailedListener(this);
            googleApiClient.disconnect();
            googleApiClient = null;
        }
    }

    private boolean isPlayServicesEnable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(context);
        if (result != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        }

        googleApiClient.connect();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    public PendingResult<LocationSettingsResult> buildLocationSetting() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
                .setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
            LocationServices.SettingsApi.checkLocationSettings(
                    googleApiClient,
                    builder.build()
            );

        return result;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(googleApiClient.isConnected()){
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        googleApiClient,
                        locationRequest,
                        this
                );
            }
        }
    }

    private void stopLocationUpdates(){
        if(googleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient,
                this
            );
            googleApiClient.disconnect();
        }
    }

    private void saveCurrentLocation(){
        coordinate.setLatitude(lastLocation.getLatitude());
        coordinate.setLongitude(lastLocation.getLongitude());

        preferencesManager.setCurrentLocation(coordinate);

        if(counter >= 4){
            stopLocationUpdates();
        }

        counter++;
    }
}

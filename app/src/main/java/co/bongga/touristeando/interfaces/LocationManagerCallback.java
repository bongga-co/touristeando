package co.bongga.touristeando.interfaces;

import android.location.Location;

/**
 * Created by bongga on 3/27/17.
 */

public interface LocationManagerCallback {
    void didRetrieveLocation(Location location);
}

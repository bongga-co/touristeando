package co.bongga.touristeando.interfaces;

import java.util.List;
import co.bongga.touristeando.models.Place;
import co.bongga.touristeando.models.PublicWiFi;

/**
 * Created by bongga on 1/21/17.
 */

public interface DataCallback {
    void didReceivePoints(List<PublicWiFi> data);
    void didReceivePlace(List<Place> data);
}

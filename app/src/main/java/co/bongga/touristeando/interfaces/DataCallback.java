package co.bongga.touristeando.interfaces;

import java.util.List;
import co.bongga.touristeando.models.Event;
import co.bongga.touristeando.models.Place;

/**
 * Created by bongga on 1/21/17.
 */

public interface DataCallback {
    void didReceiveEvent(List<Event> data);
    void didReceivePlace(List<Place> data);
}

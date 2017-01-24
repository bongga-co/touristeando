package co.bongga.toury.interfaces;

import java.util.List;
import co.bongga.toury.models.Event;
import co.bongga.toury.models.Place;

/**
 * Created by bongga on 1/21/17.
 */

public interface DataCallback {
    void didReceiveEvent(List<Event> data);
    void didReceivePlace(List<Place> data);
}

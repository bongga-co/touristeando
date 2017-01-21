package co.bongga.toury.interfaces;

import java.util.List;
import co.bongga.toury.models.Event;

/**
 * Created by bongga on 1/21/17.
 */

public interface DataCallback {
    void didReceiveData(List<Event> data);
}

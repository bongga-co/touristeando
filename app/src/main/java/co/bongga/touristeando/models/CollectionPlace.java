package co.bongga.touristeando.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by bongga on 2/20/17.
 */

public class CollectionPlace {
    @SerializedName("data")
    private List<Place> listPlace;
    @SerializedName("error")
    private Map<String, Integer> error;

    public CollectionPlace() {
    }

    public CollectionPlace(List<Place> listPlace, Map<String, Integer> error) {
        this.listPlace = listPlace;
        this.error = error;
    }

    public List<Place> getListPlace() {
        return listPlace;
    }

    public void setListPlace(List<Place> listPlace) {
        this.listPlace = listPlace;
    }

    public Map<String, Integer> getError() {
        return error;
    }

    public void setError(Map<String, Integer> error) {
        this.error = error;
    }
}

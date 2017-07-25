package co.bongga.touristeando.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by Bongga on 7/24/17.
 */

public class CollectionHelp {
    @SerializedName("data")
    private List<Help> listHelp;
    @SerializedName("error")
    private Map<String, Integer> error;

    public CollectionHelp() {
    }

    public CollectionHelp(List<Help> listHelp, Map<String, Integer> error) {
        this.listHelp = listHelp;
        this.error = error;
    }

    public List<Help> getListHelp() {
        return listHelp;
    }

    public void setListHelp(List<Place> listPlace) {
        this.listHelp = listHelp;
    }

    public Map<String, Integer> getError() {
        return error;
    }

    public void setError(Map<String, Integer> error) {
        this.error = error;
    }
}

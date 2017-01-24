package co.bongga.toury.utils;

import java.util.List;

import co.bongga.toury.interfaces.APIEndpoints;
import co.bongga.toury.interfaces.DataCallback;
import co.bongga.toury.models.Event;
import co.bongga.toury.models.Place;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bongga on 1/21/17.
 */

public class DataManager {
    private static APIEndpoints apiService = APIClient.getClient().create(APIEndpoints.class);

    public static List<Event> willGetAllAttractions(String city, double lat, double lng, String category, final DataCallback callback){
        Call<List<Event>> call = apiService.willGetAllAttractions(city, lat, lng, category);
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>>call, Response<List<Event>> response) {
                callback.didReceiveEvent(response.body());
            }

            @Override
            public void onFailure(Call<List<Event>>call, Throwable t) {
                callback.didReceiveEvent(null);
            }
        });
        return null;
    }

    public static List<Place> willGetAllPlaces(String city, double lat, double lng, String category, final DataCallback callback){
        Call<List<Place>> call = apiService.willGetAllPlaces(city, lat, lng, category);
        call.enqueue(new Callback<List<Place>>() {
            @Override
            public void onResponse(Call<List<Place>>call, Response<List<Place>> response) {
                callback.didReceivePlace(response.body());
            }

            @Override
            public void onFailure(Call<List<Place>>call, Throwable t) {
                callback.didReceivePlace(null);
            }
        });
        return null;
    }
}

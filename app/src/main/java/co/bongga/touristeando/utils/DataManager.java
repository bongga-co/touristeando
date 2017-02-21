package co.bongga.touristeando.utils;

import java.util.List;
import co.bongga.touristeando.interfaces.APIEndpoints;
import co.bongga.touristeando.interfaces.DataCallback;
import co.bongga.touristeando.models.CollectionPlace;
import co.bongga.touristeando.models.Event;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bongga on 1/21/17.
 */

public class DataManager {
    private static APIEndpoints apiService = APIClient.getClient().create(APIEndpoints.class);

    public static List<Event> willGetAllAttractions(String city, double lat, double lng, String category, int distance, final DataCallback callback){
        Call<List<Event>> call = apiService.willGetAllAttractions(city, lat, lng, category, distance);
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

    public static CollectionPlace willGetAllPlaces(String city, double lat, double lng, String category, int distance, String sort, final DataCallback callback){
        Call<CollectionPlace> call = apiService.willGetAllPlaces(city, lat, lng, category, distance, sort);
        call.enqueue(new Callback<CollectionPlace>() {
            @Override
            public void onResponse(Call<CollectionPlace>call, Response<CollectionPlace> response) {
                CollectionPlace places = response.body();
                if(places.getListPlace() != null){
                    callback.didReceivePlace(places.getListPlace());
                }
            }

            @Override
            public void onFailure(Call<CollectionPlace>call, Throwable t) {
                callback.didReceivePlace(null);
            }
        });
        return null;
    }
}

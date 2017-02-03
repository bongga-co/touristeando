package co.bongga.touristeando.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import co.bongga.touristeando.interfaces.APIEndpoints;
import co.bongga.touristeando.interfaces.DataCallback;
import co.bongga.touristeando.models.Event;
import co.bongga.touristeando.models.Place;
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

    public static JsonElement willGetAllPlaces(String city, double lat, double lng, String category, int distance, final DataCallback callback){
        Call<JsonElement> call = apiService.willGetAllPlaces(city, lat, lng, category, distance);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement>call, Response<JsonElement> response) {
                JsonElement rObj = response.body();
                if(rObj.getAsJsonObject().get("data") != null){
                    JsonArray array = rObj.getAsJsonObject().get("data").getAsJsonArray();
                    List<Place> places = new Gson().fromJson(array.toString(), new TypeToken<List<Place>>(){}.getType());

                    callback.didReceivePlace(places);
                }
                else{
                    callback.didReceivePlace(null);
                }
            }

            @Override
            public void onFailure(Call<JsonElement>call, Throwable t) {
                callback.didReceivePlace(null);
            }
        });
        return null;
    }
}

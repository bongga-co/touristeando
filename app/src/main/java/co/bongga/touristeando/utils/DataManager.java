package co.bongga.touristeando.utils;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import co.bongga.touristeando.interfaces.APIEndpoints;
import co.bongga.touristeando.interfaces.DataCallback;
import co.bongga.touristeando.models.CollectionPlace;
import co.bongga.touristeando.models.PublicWiFi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bongga on 1/21/17.
 */

public class DataManager {
    private static APIEndpoints apiService = APIClient.getClient().create(APIEndpoints.class);
    private static APIEndpoints apiService2 = SODAClient.getClient().create(APIEndpoints.class);

    public static JsonElement willGetPublicWifiPoints(String params, final DataCallback callback){
        Call<JsonElement> call = apiService2.willGetPublicWifiPoints(params);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement>call, Response<JsonElement> response) {
                String body = response.body().toString();
                List<PublicWiFi> objects = UtilityManager.doubleBuilder().fromJson(body, new TypeToken<List<PublicWiFi>>(){}.getType());

                callback.didReceivePoints(objects);
            }

            @Override
            public void onFailure(Call<JsonElement>call, Throwable t) {
                callback.didReceivePoints(null);
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

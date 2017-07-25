package co.bongga.touristeando.utils;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import co.bongga.touristeando.interfaces.APIEndpoints;
import co.bongga.touristeando.interfaces.DataCallback;
import co.bongga.touristeando.models.CollectionHelp;
import co.bongga.touristeando.models.CollectionPlace;
import co.bongga.touristeando.models.GalleryItem;
import co.bongga.touristeando.models.Help;
import co.bongga.touristeando.models.HelpFeedback;
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

                List<Object> data = UtilityManager.objectFilter(objects, Object.class);
                callback.didReceiveData(data);
            }

            @Override
            public void onFailure(Call<JsonElement>call, Throwable t) {
                callback.didReceiveData(null);
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
                    List<Object> data = UtilityManager.objectFilter(places.getListPlace(), Object.class);
                    callback.didReceiveData(data);
                }
            }

            @Override
            public void onFailure(Call<CollectionPlace>call, Throwable t) {
                callback.didReceiveData(null);
            }
        });
        return null;
    }

    public static CollectionPlace willFetchPlaces(String city, double lat, double lng, String action, int distance, String foodType, Double budget, final DataCallback callback){
        Call<CollectionPlace> call = apiService.fetchPlaces(city, lat, lng, action, distance, foodType, budget);
        call.enqueue(new Callback<CollectionPlace>() {
            @Override
            public void onResponse(Call<CollectionPlace>call, Response<CollectionPlace> response) {
                CollectionPlace places = response.body();
                if(places.getListPlace() != null){
                    List<Object> data = UtilityManager.objectFilter(places.getListPlace(), Object.class);
                    callback.didReceiveData(data);
                }
            }

            @Override
            public void onFailure(Call<CollectionPlace>call, Throwable t) {
                callback.didReceiveData(null);
            }
        });
        return null;
    }

    public static CollectionHelp willShowHelp(final DataCallback callback){
        Call<CollectionHelp> call = apiService.willShowHelp();
        call.enqueue(new Callback<CollectionHelp>() {
            @Override
            public void onResponse(Call<CollectionHelp>call, Response<CollectionHelp> response) {
                CollectionHelp helpList = response.body();
                if(helpList.getListHelp() != null){
                    List<Object> data = UtilityManager.objectFilter(helpList.getListHelp(), Object.class);
                    callback.didReceiveData(data);
                }
            }

            @Override
            public void onFailure(Call<CollectionHelp>call, Throwable t) {
                callback.didReceiveData(null);
            }
        });
        return null;
    }

    public static List<GalleryItem> willGetPlaceGallery(String id, final DataCallback callback){
        Call<List<GalleryItem>> call = apiService.willGetPlaceGallery(id);
        call.enqueue(new Callback<List<GalleryItem>>() {
            @Override
            public void onResponse(Call<List<GalleryItem>>call, Response<List<GalleryItem>> response) {
                List<GalleryItem> gallery = response.body();
                List<Object> data = UtilityManager.objectFilter(gallery, Object.class);
                callback.didReceiveData(data);
            }

            @Override
            public void onFailure(Call<List<GalleryItem>>call, Throwable t) {
                callback.didReceiveData(null);
            }
        });
        return null;
    }

    public static List<GalleryItem> willGetPlaceGalleryWithLimit(String id, int start, int size, final DataCallback callback){
        Call<List<GalleryItem>> call = apiService.willGetPlaceGalleryWithLimit(id, start, size);
        call.enqueue(new Callback<List<GalleryItem>>() {
            @Override
            public void onResponse(Call<List<GalleryItem>>call, Response<List<GalleryItem>> response) {
                List<GalleryItem> gallery = response.body();
                List<Object> data = UtilityManager.objectFilter(gallery, Object.class);
                callback.didReceiveData(data);
            }

            @Override
            public void onFailure(Call<List<GalleryItem>>call, Throwable t) {
                callback.didReceiveData(null);
            }
        });
        return null;
    }

    public static List<HelpFeedback> saveFeedback(HelpFeedback feedback, final DataCallback callback){
        Call<List<HelpFeedback>> call = apiService.saveFeedback(feedback);
        call.enqueue(new Callback<List<HelpFeedback>>() {
            @Override
            public void onResponse(Call<List<HelpFeedback>>call, Response<List<HelpFeedback>> response) {
                List<HelpFeedback> feedback = response.body();
                List<Object> data = null;

                if(feedback != null){
                    data = UtilityManager.objectFilter(feedback, Object.class);
                }

                callback.didReceiveData(data);
            }

            @Override
            public void onFailure(Call<List<HelpFeedback>>call, Throwable t) {
                callback.didReceiveData(null);
            }
        });
        return null;
    }
}

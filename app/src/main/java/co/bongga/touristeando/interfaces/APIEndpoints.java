package co.bongga.touristeando.interfaces;

import com.google.gson.JsonElement;

import co.bongga.touristeando.models.CollectionPlace;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by bongga on 1/21/17.
 */

public interface APIEndpoints {
    @GET("places")
    Call<CollectionPlace> willGetAllPlaces(
        @Header("city") String city,
        @Header("latitude") double latitude,
        @Header("longitude") double longitude,
        @Header("category") String category,
        @Header("radius") int distance,
        @Header("sorting") String sorting
    );

    @GET("4ai7-uijz.json")
    Call<JsonElement> willGetPublicWifiPoints(
        @Query("$where") String params
    );
}

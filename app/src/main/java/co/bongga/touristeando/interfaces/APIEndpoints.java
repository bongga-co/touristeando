package co.bongga.touristeando.interfaces;

import com.google.gson.JsonElement;

import java.util.List;

import co.bongga.touristeando.models.CollectionPlace;
import co.bongga.touristeando.models.Gallery;
import co.bongga.touristeando.models.Help;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by bongga on 1/21/17.
 */

public interface APIEndpoints {
    @GET("help")
    Call<List<Help>> willShowHelp();

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

    @GET("places/{id}/gallery")
    Call<List<Gallery>> willGetPlaceGallery(
        @Path("id") String id
    );

    @GET("places/{id}/gallery/start/{start}/size/{size}")
    Call<List<Gallery>> willGetPlaceGalleryWithLimit(
            @Path("id") String id,
            @Path("start") int start,
            @Path("size") int size
    );
}

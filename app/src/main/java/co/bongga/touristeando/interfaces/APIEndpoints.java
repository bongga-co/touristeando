package co.bongga.touristeando.interfaces;

import com.google.gson.JsonElement;

import java.util.List;

import co.bongga.touristeando.models.CollectionHelp;
import co.bongga.touristeando.models.CollectionPlace;
import co.bongga.touristeando.models.GalleryItem;
import co.bongga.touristeando.models.Help;
import co.bongga.touristeando.models.HelpFeedback;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by bongga on 1/21/17.
 */

public interface APIEndpoints {
    @GET("help")
    Call<CollectionHelp> willShowHelp();

    @GET("places2")
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

    @GET("places2/{id}/gallery")
    Call<List<GalleryItem>> willGetPlaceGallery(
        @Path("id") String id
    );

    @GET("places2/{id}/gallery/start/{start}/size/{size}")
    Call<List<GalleryItem>> willGetPlaceGalleryWithLimit(
            @Path("id") String id,
            @Path("start") int start,
            @Path("size") int size
    );

    @POST("feedback")
    Call<List<HelpFeedback>> saveFeedback(@Body HelpFeedback feedback);

    @GET("places")
    Call<CollectionPlace> fetchPlaces(
            @Header("city") String city,
            @Header("latitude") double latitude,
            @Header("longitude") double longitude,
            @Header("action") String action,
            @Header("radius") int distance,
            @Header("foodType") String foodType,
            @Header("budget") Double budget
    );
}

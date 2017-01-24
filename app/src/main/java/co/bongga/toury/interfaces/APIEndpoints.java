package co.bongga.toury.interfaces;

import java.util.List;

import co.bongga.toury.models.Event;
import co.bongga.toury.models.Place;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by bongga on 1/21/17.
 */

public interface APIEndpoints {
    @GET("places")
    Call<List<Place>> willGetAllPlaces(
        @Header("city") String city,
        @Header("lat") double latitude,
        @Header("lng") double longitude,
        @Header("category") String category
    );

    @GET("attractions")
    Call<List<Event>> willGetAllAttractions(
        @Header("city") String city,
        @Header("lat") double latitude,
        @Header("lng") double longitude,
        @Header("category") String category
    );
}

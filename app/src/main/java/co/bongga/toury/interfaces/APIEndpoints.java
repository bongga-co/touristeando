package co.bongga.toury.interfaces;

import com.google.gson.JsonElement;
import java.util.List;
import co.bongga.toury.models.Event;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by bongga on 1/21/17.
 */

public interface APIEndpoints {
    @GET("places")
    Call<JsonElement> willGetAllPlaces(
        @Header("city") String city,
        @Header("latitude") double latitude,
        @Header("longitude") double longitude,
        @Header("category") String category,
        @Header("distance") int distance
    );

    @GET("attractions")
    Call<List<Event>> willGetAllAttractions(
        @Header("city") String city,
        @Header("lat") double latitude,
        @Header("lng") double longitude,
        @Header("category") String category,
        @Header("distance") int distance
    );
}

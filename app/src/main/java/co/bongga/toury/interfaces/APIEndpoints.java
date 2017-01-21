package co.bongga.toury.interfaces;

import java.util.List;

import co.bongga.toury.models.Event;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by bongga on 1/21/17.
 */

public interface APIEndpoints {
    /*@GET("/places")
    Call<Place> willGetAllPlaces();*/

    @GET("attractions")
    Call<List<Event>> willGetAllAttractions();
}

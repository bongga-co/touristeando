package co.bongga.toury.utils;

import java.util.List;

import co.bongga.toury.interfaces.APIEndpoints;
import co.bongga.toury.interfaces.DataCallback;
import co.bongga.toury.models.Event;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by bongga on 1/21/17.
 */

public class DataManager {
    private static APIEndpoints apiService = APIClient.getClient().create(APIEndpoints.class);
    public static List<Event> willGetAllAttractions(final DataCallback callback){
        Call<List<Event>> call = apiService.willGetAllAttractions();
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>>call, Response<List<Event>> response) {
                callback.didReceiveData(response.body());
            }

            @Override
            public void onFailure(Call<List<Event>>call, Throwable t) {
                callback.didReceiveData(null);
            }
        });
        return null;
    }
}

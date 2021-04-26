package androiddeveloper.amrrabbie.buseettask.network;

import androiddeveloper.amrrabbie.buseettask.model.PlacesResponse;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestrantsApiService {

    @GET("place/nearbysearch/json")
    public Single<PlacesResponse> getNearestRestrants(
            @Query("location") String location,
            @Query("radius") String radius ,
            @Query("sensor") String sensor,
            @Query("types") String types,
            @Query("key") String key

    );
}



package androiddeveloper.amrrabbie.buseettask.repostry;

import javax.inject.Inject;

import androiddeveloper.amrrabbie.buseettask.model.PlacesResponse;
import androiddeveloper.amrrabbie.buseettask.network.RestrantsApiService;
import io.reactivex.rxjava3.core.Single;

public class RestrantsRepostry {
    private RestrantsApiService restrantsApiService;

    @Inject
    public RestrantsRepostry(RestrantsApiService restrantsApiService) {
        this.restrantsApiService = restrantsApiService;
    }

    public Single<PlacesResponse> getnearestrestrants(String location,String radius,String sensor ,String types,String key){
        return restrantsApiService.getNearestRestrants(location,radius,sensor,types,key);
    }
}

package androiddeveloper.amrrabbie.buseettask.repostry;

import androidx.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import androiddeveloper.amrrabbie.buseettask.db.Restrant;
import androiddeveloper.amrrabbie.buseettask.db.RestrantDao;
import androiddeveloper.amrrabbie.buseettask.model.PlacesResponse;
import androiddeveloper.amrrabbie.buseettask.model.directions.DirectionsResponse;
import androiddeveloper.amrrabbie.buseettask.network.RestrantsApiService;
import io.reactivex.rxjava3.core.Single;

public class RestrantsRepostry {
    private RestrantsApiService restrantsApiService;
    private RestrantDao restrantDao;

    @Inject
    public RestrantsRepostry(RestrantsApiService restrantsApiService,RestrantDao restrantDao) {
        this.restrantsApiService = restrantsApiService;
        this.restrantDao=restrantDao;
    }

    public Single<PlacesResponse> getnearestrestrants(String location,String radius,String sensor ,String types,String key){
        return restrantsApiService.getNearestRestrants(location,radius,sensor,types,key);
    }

    public void insertRestrant(Restrant restrant){
        restrantDao.insertRestrant(restrant);
    }

    public void deleteRestrant(int id){
        restrantDao.deleteRestrant(id);
    }

    public LiveData<List<Restrant>> getAllRestrants(){
        return restrantDao.getAllRestrants();
    }

    public Single<DirectionsResponse> getTimeAndDistance(String origin,String destination,String key){
        return restrantsApiService.getTimeAndDistance(origin,destination,key);
    }
}

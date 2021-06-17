package androiddeveloper.amrrabbie.buseettask.viewmodel;

import android.util.Log;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import androiddeveloper.amrrabbie.buseettask.db.Restrant;
import androiddeveloper.amrrabbie.buseettask.model.PlacesResponse;
import androiddeveloper.amrrabbie.buseettask.model.ResultsItem;
import androiddeveloper.amrrabbie.buseettask.model.directions.DirectionsResponse;
import androiddeveloper.amrrabbie.buseettask.repostry.RestrantsRepostry;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RestrantsViewModel extends ViewModel {

    private RestrantsRepostry repostry;

    MutableLiveData<List<ResultsItem>> restrantslist=new MutableLiveData<>();

    LiveData<List<Restrant>> restrantslistoffline=null;

    MutableLiveData<DirectionsResponse> directionsresponse=new MutableLiveData<>();

    @ViewModelInject
    public RestrantsViewModel(RestrantsRepostry repostry) {
        this.repostry = repostry;
    }

    public MutableLiveData<List<ResultsItem>> getRestrantslist() {
        return restrantslist;
    }

    public LiveData<List<Restrant>> getRestrantslistoffline() {
        return restrantslistoffline;
    }

    public MutableLiveData<DirectionsResponse> getDirectionsresponse() {
        return directionsresponse;
    }

    public void getNearestRestrants(String location, String radius, String sensor , String types, String key){
        repostry.getnearestrestrants(location,radius,sensor,types,key)
                .subscribeOn(Schedulers.io())
                .map(new Function<PlacesResponse, List<ResultsItem>>() {
                    @Override
                    public List<ResultsItem> apply(PlacesResponse placesResponse) throws Throwable {
                        List<ResultsItem> list=placesResponse.getResults();
                        return list;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->restrantslist.setValue(result),error-> Log.e("error",error.getMessage()));
    }


    public void insertRestrant(Restrant restrant){
        repostry.insertRestrant(restrant);
    }

    public void deleteRestrant(int id){
        repostry.deleteRestrant(id);
    }

    public void getAllRestrants(){
        restrantslistoffline=repostry.getAllRestrants();
    }

    public void getTimeAndDistance(String origin,String destination,String key){
        repostry.getTimeAndDistance(origin,destination,key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->directionsresponse.setValue(result),error->Log.e("error",error.getMessage()));
    }



}

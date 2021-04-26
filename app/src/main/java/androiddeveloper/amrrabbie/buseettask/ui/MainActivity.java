package androiddeveloper.amrrabbie.buseettask.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androiddeveloper.amrrabbie.buseettask.adapters.RestrantsAdapter;
import androiddeveloper.amrrabbie.buseettask.R;
import androiddeveloper.amrrabbie.buseettask.databinding.ActivityMainBinding;
import androiddeveloper.amrrabbie.buseettask.model.ResultsItem;
import androiddeveloper.amrrabbie.buseettask.util.GpsTracker;
import androiddeveloper.amrrabbie.buseettask.util.Network;
import androiddeveloper.amrrabbie.buseettask.viewmodel.RestrantsViewModel;
import dagger.hilt.android.AndroidEntryPoint;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;


@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback , OnRestrantslistClick {

    ActivityMainBinding binding;
    RestrantsViewModel viewModel;
    RestrantsAdapter adapter;

    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private double rlatitude;
    private double rlongitude;
    private GpsTracker gpsTracker;
    private Marker marker1;
    private Marker marker2;
    String restaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLocation();

        viewModel = new ViewModelProvider(this).get(RestrantsViewModel.class);

        adapter = new RestrantsAdapter(this,this);

        bindNearestRestrants();

    }

    private void bindNearestRestrants() {
        String location =  latitude + "," + longitude;
        String radius = "5000";
        String sensor = "true";
        String types = "restaurant";
        String key = "AIzaSyAyKWcogS2vgE52G5ZBj9IXtgqQ7n3cP5A";

        viewModel.getNearestRestrants(location, radius, sensor, types, key);

        viewModel.getRestrantslist().observe(MainActivity.this, new Observer<List<ResultsItem>>() {
            @Override
            public void onChanged(List<ResultsItem> resultsItems) {
                Log.i("results", resultsItems.toString());
                binding.restrantsrecycler.setAdapter(adapter);
                adapter.setList(resultsItems);
                binding.pbar.setVisibility(View.GONE);
                binding.restrantsrecycler.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        // Add a marker in my loc and move the camera
        LatLng myloc = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(myloc).title("My location now"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(myloc));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(myloc).zoom(16).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.getUiSettings().setZoomControlsEnabled(true);

       // mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

    }

    public void getLocation() {

        latitude = 30.0135812;
        longitude = 31.2819673;

        if (!Network.isNetworkAvailable(MainActivity.this)) {
            latitude = 30.0135812;
            longitude = 31.2819673;
            return;
        } else {

            gpsTracker = new GpsTracker(MainActivity.this);
            if (gpsTracker.canGetLocation()) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();

            } else {
                gpsTracker.showSettingsAlert();
            }
        }
    }

    @Override
    public void onListClick(ResultsItem item) {
        if(item != null){
            binding.cview.setVisibility(View.VISIBLE);
            binding.name.setText(item.getName());

            List<String> types=item.getTypes();

            String resttypes="";

            for(int i=0;i<types.size();i++){
                if(i < types.size() -1){
                    resttypes=resttypes + types.get(i) + " - ";
                }else{
                    resttypes=resttypes + types.get(i);
                }
            }

            binding.type.setText(resttypes);

            Glide.with(MainActivity.this)
                    .load(item.getIcon())
                    .into(binding.img);

            rlatitude=item.getGeometry().getLocation().getLat();
            rlongitude=item.getGeometry().getLocation().getLng();
            restaddress=item.getVicinity();

            drawMpath();
        }
    }


    private void drawMpath() {
        try{

            mMap.clear();

            LatLng restloc = new LatLng(rlatitude, rlongitude);
            //mMap.addMarker(new MarkerOptions().position(barcelona).title("First Place"));


            int height1 = 100;
            int width1 = 75;
            BitmapDrawable bitmapdraw1 = (BitmapDrawable) getResources().getDrawable(R.drawable.redmarker);
            Bitmap b1 = bitmapdraw1.getBitmap();
            Bitmap smallMarker1 = Bitmap.createScaledBitmap(b1, width1, height1, false);


            int height = 100;
            int width = 75;
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.blackmarker);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);


            marker1=mMap.addMarker(new MarkerOptions().position(restloc).title(restaddress).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

            LatLng myloc = new LatLng(latitude, longitude);
            //mMap.addMarker(new MarkerOptions().position(madrid).title("Second Place"));
            marker2=mMap.addMarker(new MarkerOptions().position(myloc).title("My location now").icon(BitmapDescriptorFactory.fromBitmap(smallMarker1)));






            mMap.getUiSettings().setZoomControlsEnabled(true);

            LatLng curloc = new LatLng( latitude ,  longitude );

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curloc, 13));

        }catch (Exception e){

        }
    }

    @Override
    public void onBackPressed() {
        /*if (!shouldAllowBack()) {
            doSomething();
        } else {
            super.onBackPressed();
        }*/

        binding.cview.setVisibility(View.GONE);
        mMap.clear();
        // Add a marker in my loc and move the camera
        LatLng myloc = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(myloc).title("My location now"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(myloc));

        CameraPosition cameraPosition = new CameraPosition.Builder().target(myloc).zoom(16).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.getUiSettings().setZoomControlsEnabled(true);
    }
}
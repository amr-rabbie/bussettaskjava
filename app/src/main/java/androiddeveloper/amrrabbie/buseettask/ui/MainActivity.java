package androiddeveloper.amrrabbie.buseettask.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androiddeveloper.amrrabbie.buseettask.adapters.RestrantsAdapter;
import androiddeveloper.amrrabbie.buseettask.R;
import androiddeveloper.amrrabbie.buseettask.databinding.ActivityMainBinding;
import androiddeveloper.amrrabbie.buseettask.db.Restrant;
import androiddeveloper.amrrabbie.buseettask.di.RetrofitModule;
import androiddeveloper.amrrabbie.buseettask.model.ResultsItem;
import androiddeveloper.amrrabbie.buseettask.model.directions.DirectionsResponse;
import androiddeveloper.amrrabbie.buseettask.model.directions.Distance;
import androiddeveloper.amrrabbie.buseettask.model.directions.Duration;
import androiddeveloper.amrrabbie.buseettask.model.directions.LegsItem;
import androiddeveloper.amrrabbie.buseettask.model.directions.RoutesItem;
import androiddeveloper.amrrabbie.buseettask.util.GpsTracker;
import androiddeveloper.amrrabbie.buseettask.util.Network;
import androiddeveloper.amrrabbie.buseettask.viewmodel.RestrantsViewModel;
import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.Route;

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
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;

import com.google.maps.model.EncodedPolyline;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback , OnRestrantslistClick , View.OnClickListener , LocationListener {

    private static final String TAG = "";
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
    ResultsItem restrantdata;

    private LocationManager locationManager;
    private static final int PERMISSION_REQUEST_CODE = 200;

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

        binding.cview.setOnClickListener(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/

        if (!checkPermission()) {
            requestPermission();
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5000, 1, this);
        }


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
                setupSwipe();
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
            restrantdata=item;
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

    @Override
    public void onListOfflineClick(Restrant restrant) {

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



            //Define list to get all latlng for the route
            List<LatLng> path = new ArrayList();


            //Execute Directions API request
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey(RetrofitModule.Api_Key)
                    .build();

            String origin = latitude + "," + longitude;
            String destination = rlatitude + "," + rlongitude;

            //DirectionsApiRequest req = DirectionsApi.getDirections(context, "41.385064,2.173403", "40.416775,-3.70379");
            DirectionsApiRequest req = DirectionsApi.getDirections(context, origin, destination);

            try {
                DirectionsResult res = req.await();

                //Loop through legs and steps to get encoded polylines of each step
                if (res.routes != null && res.routes.length > 0) {
                    DirectionsRoute route = res.routes[0];

                    if (route.legs != null) {
                        for (int i = 0; i < route.legs.length; i++) {
                            DirectionsLeg leg = route.legs[i];
                            if (leg.steps != null) {
                                for (int j = 0; j < leg.steps.length; j++) {
                                    DirectionsStep step = leg.steps[j];
                                    if (step.steps != null && step.steps.length > 0) {
                                        for (int k = 0; k < step.steps.length; k++) {
                                            DirectionsStep step1 = step.steps[k];
                                            EncodedPolyline points1 = step1.polyline;
                                            if (points1 != null) {
                                                //Decode polyline and add points to list of route coordinates
                                                List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                                for (com.google.maps.model.LatLng coord1 : coords1) {
                                                    path.add(new LatLng(coord1.lat, coord1.lng));
                                                }
                                            }
                                        }
                                    } else {
                                        EncodedPolyline points = step.polyline;
                                        if (points != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords = points.decodePath();
                                            for (com.google.maps.model.LatLng coord : coords) {
                                                path.add(new LatLng(coord.lat, coord.lng));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, ex.getLocalizedMessage());
            }

            //Draw the polyline
            if (path.size() > 0) {
                PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLACK).width(10);
                mMap.addPolyline(opts);
            }



            mMap.getUiSettings().setZoomControlsEnabled(true);

            LatLng curloc = new LatLng( latitude ,  longitude );

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curloc, 13));


            String key = RetrofitModule.Api_Key;

            viewModel.getTimeAndDistance(origin, destination, key);

            viewModel.getDirectionsresponse().observe(MainActivity.this, new Observer<DirectionsResponse>() {
                @Override
                public void onChanged(DirectionsResponse googleMapsResponse) {
                    RoutesItem route = googleMapsResponse.getRoutes().get(0);
                    LegsItem leg = route.getLegs().get(0);
                    Distance distance = leg.getDistance();
                    Integer value = distance.getValue();
                    Double Mydistance = Double.valueOf(value / 1000);

                    Duration duration = leg.getDuration();
                    Integer value1 = duration.getValue();
                    Double mymints = Double.valueOf(value1 / 60);

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Distance and time");
                    builder.setMessage("Distance: " + Mydistance + " Km\nDuration: " + mymints + " Mins");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });



                    builder.create().show();


                }
            });

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

    public void setupSwipe(){
        ItemTouchHelper.SimpleCallback callback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int itemposition=viewHolder.getAdapterPosition();

                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Confirm message")
                        .setIcon(R.drawable.confirm)
                        .setMessage("Are You Sure you want to add this restrant to favourite list ?!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addRestranttoFavList(itemposition);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                adapter.notifyDataSetChanged();
                            }
                        })
                        ;
                AlertDialog dialog=builder.create();
                dialog.show();







            }
        };
        ItemTouchHelper itemTouchHelper=new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(binding.restrantsrecycler);
    }

    private void addRestranttoFavList(int itemposition) {
        ResultsItem item=adapter.getItemAt(itemposition);

        List<String> types=item.getTypes();

        String resttypes="";

        for(int i=0;i<types.size();i++){
            if(i < types.size() -1){
                resttypes=resttypes + types.get(i) + " - ";
            }else{
                resttypes=resttypes + types.get(i);
            }
        }

        Restrant restrant=new Restrant(item.getName(),resttypes,item.getIcon(),item.getGeometry().getLocation().getLat()+"",item.getGeometry().getLocation().getLng()+"",item.getVicinity(),item.getRating()+"");

        viewModel.insertRestrant(restrant);
        adapter.notifyDataSetChanged();
        Toast.makeText(MainActivity.this, "Restrant added to favourite list", Toast.LENGTH_SHORT).show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fav:
                Intent favIntent = new Intent(MainActivity.this, FavActivity.class);
                startActivity(favIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent intent=new Intent(MainActivity.this,MapActivity.class);
        intent.putExtra("data", (Serializable) restrantdata);
        startActivity(intent);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            //LatLng mylastloc = new LatLng(mylastlat, mylastlong);
            //marker1.setPosition(driverloc);
            //if (mMap != null) {



            Log.i("MyLocation","My last lication is : " + latitude + " , " + longitude);

            Toast.makeText(MainActivity.this, "My last lication is : " + latitude + " , " + longitude, Toast.LENGTH_SHORT).show();

           // drawMpath();



          /*  if (distance(clientpickuplate, clientpickuplong, latte, longg) <= 0.07) { // if distance < 0.1

                start.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);
                //   launch the activity
            }else {
                start.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
            }*/



        }catch (Exception e){

        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        /*int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);*/

        return result == PackageManager.PERMISSION_GRANTED /*&&
                result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED &&
                result3 == PackageManager.PERMISSION_GRANTED*/;
    }

    private void requestPermission() {

        //ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    /*boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readStorageAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[3] == PackageManager.PERMISSION_GRANTED;*/

                    //if (locationAccepted && cameraAccepted && readStorageAccepted && writeStorageAccepted)
                    if (locationAccepted )
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("التطبيق يحتاج بعض الصلاحيات",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    //requestPermissions(new String[]{ACCESS_FINE_LOCATION, CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                                                    requestPermissions(new String[]{ACCESS_FINE_LOCATION},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                }

                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("تم", okListener)
                .setNegativeButton("الغاء", null)
                .create()
                .show();
    }

}
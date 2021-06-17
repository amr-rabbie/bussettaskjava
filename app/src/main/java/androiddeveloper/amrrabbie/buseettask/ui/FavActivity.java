package androiddeveloper.amrrabbie.buseettask.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

import androiddeveloper.amrrabbie.buseettask.R;
import androiddeveloper.amrrabbie.buseettask.adapters.RestrantsOfflineAdapter;
import androiddeveloper.amrrabbie.buseettask.databinding.ActivityFavBinding;
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

@AndroidEntryPoint
public class FavActivity extends AppCompatActivity implements OnMapReadyCallback,OnRestrantslistClick {

    private static final String TAG = "";
    ActivityFavBinding binding;
    RestrantsViewModel viewModel;
    RestrantsOfflineAdapter adapter;

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
        //setContentView(R.layout.activity_fav);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_fav);

        viewModel=new ViewModelProvider(this).get(RestrantsViewModel.class);

        adapter=new RestrantsOfflineAdapter(this,this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getLocation();

        getAllFavRestrants();
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

        if (!Network.isNetworkAvailable(FavActivity.this)) {
            latitude = 30.0135812;
            longitude = 31.2819673;
            return;
        } else {

            gpsTracker = new GpsTracker(FavActivity.this);
            if (gpsTracker.canGetLocation()) {
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();

            } else {
                gpsTracker.showSettingsAlert();
            }
        }
    }


    private void getAllFavRestrants() {
        viewModel.getAllRestrants();

        viewModel.getRestrantslistoffline().observe(FavActivity.this, new Observer<List<Restrant>>() {
            @Override
            public void onChanged(List<Restrant> restrants) {
                binding.restrantsrecycler.setAdapter(adapter);
                setupSwipe();
                adapter.setList(restrants);
                binding.pbar.setVisibility(View.GONE);
                binding.restrantsrecycler.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupSwipe() {
        ItemTouchHelper.SimpleCallback callback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int itempos=viewHolder.getAdapterPosition();

                AlertDialog.Builder builder=new AlertDialog.Builder(FavActivity.this)
                        .setTitle("Confirm message")
                        .setIcon(R.drawable.confirm)
                        .setMessage("Are You Sure you want to delete this restrant to favourite list ?!")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteRestranttoFavList(itempos);
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

    private void deleteRestranttoFavList(int itempos) {
        Restrant restrant=adapter.getItemAt(itempos);
        viewModel.deleteRestrant(restrant.getId());
        adapter.notifyDataSetChanged();
        Toast.makeText(FavActivity.this, "Restrant deleted from favourite list", Toast.LENGTH_SHORT).show();


    }


    @Override
    public void onListClick(ResultsItem item) {

    }

    @Override
    public void onListOfflineClick(Restrant item) {
        if(item != null){
            binding.cview.setVisibility(View.VISIBLE);
            binding.name.setText(item.getName());



            binding.type.setText(item.getType());

            Glide.with(FavActivity.this)
                    .load(item.getImg())
                    .into(binding.img);

            rlatitude= Double.parseDouble(item.getLate());
            rlongitude= Double.parseDouble(item.getLong());
            restaddress=item.getAddress();

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

            viewModel.getDirectionsresponse().observe(FavActivity.this, new Observer<DirectionsResponse>() {
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

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(FavActivity.this);
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
}
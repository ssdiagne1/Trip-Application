/*
 * Main Activity
 * Samba Diagne
 */
package com.uncc.tripApplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonIOException;
import com.uncc.group12_hw09.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private final OkHttpClient client = new OkHttpClient();
    ArrayList<Coordinates> coordinatesArrayList = new ArrayList<>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);





    }

    public void onMapReady(GoogleMap googleMap) {
        Request request = new Request.Builder().url("https://www.theappsdr.com/map/route").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                new AlertDialog.Builder(getApplicationContext())
                        .setTitle("Unable to complete Http request")
                        .setMessage(e.getMessage())
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()){
                    try{

                        String body = response.body().string();
                        //Log.d("demo", "coordinates: "+body);

                        JSONObject root = null;
                        coordinatesArrayList.clear();

                        root = new JSONObject(body);
                        JSONArray pathJSONArray = root.getJSONArray("path");


                        for (int i = 0; i < pathJSONArray.length(); i++) {
                            JSONObject jsonObject = pathJSONArray.getJSONObject(i);
                            double latitute =  jsonObject.getDouble("latitude");
                            double longitude = jsonObject.getDouble("longitude");
                            coordinatesArrayList.add(new Coordinates(latitute,longitude));


                        }

                    }catch (JsonIOException | JSONException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMap = googleMap;
                            mMap.getUiSettings().setZoomControlsEnabled(true);
                            mMap.getUiSettings().isMyLocationButtonEnabled();
                            for (int i = 0; i < coordinatesArrayList.size()-1; i++) {


                                LatLng firstLocation = new LatLng(coordinatesArrayList.get(i).getLatitude(), coordinatesArrayList.get(i).getLongitude());
                                LatLng secondLocation = new LatLng(coordinatesArrayList.get(i+1).getLatitude(), coordinatesArrayList.get(i+1).getLongitude());


                                mMap.addPolyline((new PolylineOptions()).add(secondLocation, firstLocation)
                                                .add(secondLocation, firstLocation)
                                                .width(5)
                                                .color(Color.RED))
                                                .isGeodesic();
                                //add a marker for the start location
                                if (i ==0) {
                                    mMap.addMarker(new MarkerOptions()
                                            .position(firstLocation))
                                            .setTitle("Start location");
                                }
                                //add a marker for the end location
                                if (i+1 == coordinatesArrayList.size()-1) {
                                    mMap.addMarker(new MarkerOptions()
                                            .position(secondLocation))
                                            .setTitle("End location");
                                }
                                // Move the camera instantly to Sydney with a zoom of 15.
                                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 15));
                                // Zoom in, animating the camera.
                                mMap.animateCamera(CameraUpdateFactory.zoomIn());

                                // Zoom out to zoom level 10, animating with a duration of 2 seconds.
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

                                // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(secondLocation)      // Sets the center of the map to Mountain View
                                        .zoom(10)// Sets the zoom
                                         .bearing(40)            // Sets the orientation of the camera to east
                                        .tilt(80)// Sets the tilt of the camera to 30 degrees
                                        .build();                   // Creates a CameraPosition from the builder
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                            }
                        }
                    });
                }else {
                    ResponseBody responseBody = response.body();
                    String body = responseBody.string();
                    new AlertDialog.Builder(getApplicationContext())
                            .setTitle("Unable to parse the json file")
                            .setMessage(body)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }
            }
        });

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


}

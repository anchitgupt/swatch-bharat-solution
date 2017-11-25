package ateam.com.clean.Map;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ateam.com.clean.Data.LatLngFetcher;
import ateam.com.clean.R;

public class DustbinLocator extends AppCompatActivity implements OnMapReadyCallback, ValueEventListener {

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));
    public static final LatLngBounds BOUNDS_INDIA1 = new LatLngBounds(new LatLng(7.798000, 68.14712), new LatLng(37.090000, 97.34466));
    private GoogleMap mMap;
    private static String TAG = "DustbinLocator";
    private DatabaseReference mDatabase;
    private ArrayList<LatLngFetcher> latLngFetcherArrayList, latLngFetcherArrayList2 = new ArrayList<>();
    private LatLngFetcher latLngFetcher;
    LatLng latLng;
    LocationManager locationManager;
    FirebaseUser user;
    String city=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dustbin_locator);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        latLngFetcherArrayList = new ArrayList<>();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        latLngFetcher = new LatLngFetcher();
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        String[] user_id = user.getEmail().split("@");

        FirebaseDatabase.getInstance().getReference("users").child(user_id[0]).child("city").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                city = (String) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.e(TAG, "onCreate: "+city );
        FirebaseDatabase.getInstance().getReference("dustbin").child("dustbin").addValueEventListener(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            showGPSDisabledAlertToUser();
        mMap.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));*/
        //mMap.addMarker(customMarkerPositions());
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        latLngFetcherArrayList = new ArrayList<>();
        for (DataSnapshot mydata: dataSnapshot.getChildren()
                ) {
            latLngFetcher = mydata.getValue(LatLngFetcher.class);
            //Log.e(TAG, "onDataChange: "+latLngFetcher.getLatitude()+" "+latLngFetcher.getLongitude() );
            //latLng = new LatLng(Double.parseDouble(latLngFetcher.getLatitude()),Double.parseDouble(latLngFetcher.getLongitude()));
            latLngFetcherArrayList.add(latLngFetcher);
        }
        for (int i=0;i<latLngFetcherArrayList.size();i++){
            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(
                    latLngFetcherArrayList.get(i).getLatitude()
            )
            ,Double.parseDouble(
                    latLngFetcherArrayList.get(i).getLongitude()
            )
                    )));
        }
        //mMap.setLatLngBoundsForCameraTarget(BOUNDS_INDIA);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);

                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

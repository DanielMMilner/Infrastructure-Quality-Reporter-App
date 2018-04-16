package s3542977.com.tqr;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Test", "No ACCESS_FINE_LOCATION Permission");
            return;
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.i("Test", "No ACCESS_COARSE_LOCATION Permission");
            return;
        }

        if(locationManager == null)
            Log.i("Test", "Location manager is null");

        assert locationManager != null;

        mMap.setMyLocationEnabled(true);

        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        // Add a marker in Sydney and move the camera
        System.out.println(lastKnownLocation.getLatitude()+" "+ lastKnownLocation.getLongitude());
        Log.i("Test", "Location " + lastKnownLocation.getLatitude()+ " "+ lastKnownLocation.getLongitude());

        LatLng currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        addMarkers();
    }

    private void addMarkers(){
        SQLiteDatabase mydatabase = openOrCreateDatabase("database", MODE_PRIVATE, null);
        Cursor resultSet = mydatabase.rawQuery("Select * from InfrastructureQuality", null);
//        if (!resultSet.moveToFirst())
//            return;
        resultSet.moveToFirst();

        LatLng latLng;
        do {
            latLng = new LatLng(resultSet.getDouble(1), resultSet.getDouble(0));
            Log.i("log", String.valueOf(latLng.longitude));
            Log.i("lat", String.valueOf(latLng.latitude));

            mMap.addMarker(new MarkerOptions().position(latLng).title("Dummy Data"));

        } while (resultSet.moveToNext());
    }
}

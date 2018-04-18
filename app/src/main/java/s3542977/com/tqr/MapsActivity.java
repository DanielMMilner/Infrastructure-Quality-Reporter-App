package s3542977.com.tqr;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        databaseHandler = new DatabaseHandler(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Test", "No ACCESS_FINE_LOCATION Permission");
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Test", "No ACCESS_COARSE_LOCATION Permission");
            return;
        }

        if (locationManager == null)
            Log.i("Test", "Location manager is null");

        assert locationManager != null;

        mMap.setMyLocationEnabled(true);

        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
        LatLng currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

        addMarkers();
    }

    private void addMarkers() {
        if (databaseHandler.isDatabaseEmpty())
            return;
        LatLng latLng;
        while (!databaseHandler.isLastRow()) {
            latLng = new LatLng(databaseHandler.getLongitudeAsDouble(),
                    databaseHandler.getLatitudeAsDouble());

            mMap.addMarker(new MarkerOptions().position(latLng).title("Dummy Data"));
            databaseHandler.nextRow();
        }
    }
}
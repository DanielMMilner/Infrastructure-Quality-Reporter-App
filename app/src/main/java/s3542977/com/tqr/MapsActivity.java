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
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ArrayList<WeightedLatLng> weightedLatLngList;
    private ArrayList<LatLng> latLngList;

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
        latLngList = new ArrayList<>();
        weightedLatLngList = new ArrayList<>();
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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

        getLatLngCoordinates();

//        addMarkers();

        addHeatMap();
    }

    private void getLatLngCoordinates() {
        if (databaseHandler.isDatabaseEmpty())
            return;
        do {
            LatLng latLng = new LatLng(databaseHandler.getLatitudeAsDouble(),
                    databaseHandler.getLongitudeAsDouble());
            double weighting = 100 - databaseHandler.getQualityAsDouble();
            WeightedLatLng weightedLatLng = new WeightedLatLng(latLng, weighting);

            latLngList.add(latLng);
            weightedLatLngList.add(weightedLatLng);
        } while (databaseHandler.hasNext());
    }

    private void addMarkers() {
        for (LatLng latLng : latLngList) {
            mMap.addMarker(new MarkerOptions().position(latLng).title("Dummy Data"));
        }
    }

    private void addHeatMap() {
        if (weightedLatLngList.isEmpty())
            return;
        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder().weightedData(weightedLatLngList).build();
        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }
}
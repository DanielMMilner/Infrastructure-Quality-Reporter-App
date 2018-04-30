package s3542977.com.tqr;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private ArrayList<WeightedLatLng> weightedLatLngList;
    private ArrayList<LatLng> latLngList;
    private TileOverlay mOverlay;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        //https://developer.android.com/training/appbar/setting-up
        getMenuInflater().inflate(R.menu.maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filters:
                // User chose the "Settings" item, show the app settings UI...
                return true;
            case R.id.action_toggle_heatMap:
                if(item.isChecked()){
                    removeHeatMap();
                    item.setChecked(false);
                }else{
                    addHeatMap();
                    item.setChecked(true);
                }
                return true;
            case R.id.action_toggle_markers:
                if(item.isChecked()){
                    removeMarkers();
                    item.setChecked(false);
                }else{
                    addMarkers();
                    item.setChecked(true);
                }
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
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

    private void removeMarkers() {
        mMap.clear();
    }

    private void removeHeatMap() {
        mOverlay.remove();
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
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }
}
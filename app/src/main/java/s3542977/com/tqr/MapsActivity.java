package s3542977.com.tqr;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import java.util.ArrayList;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {
    private GoogleMap mMap;
    private ArrayList<WeightedLatLng> weightedLatLngList;
    private TileOverlay mOverlay;
    private ArrayList<MapMarkers> markers;
    private ArrayList<MapMarkers> visibleMarkers;
    private ClusterManager<MapMarkers> mClusterManager;
    private AlertDialog filterDialog;
    private String[] filterItems;

    DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        markers = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        databaseHandler = new DatabaseHandler(this);
        weightedLatLngList = new ArrayList<>();

        setUpFilter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //https://developer.android.com/training/appbar/setting-up
        getMenuInflater().inflate(R.menu.maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
//        https://developer.android.com/guide/topics/ui/dialogs
        switch (item.getItemId()) {
            case R.id.action_filters:
                filterDialog.show();
                return true;
            case R.id.action_toggle_heatMap:
                if (item.isChecked()) {
                    mOverlay.setVisible(false);
                    item.setChecked(false);
                } else {
                    mOverlay.setVisible(true);
                    item.setChecked(true);
                }
                return true;
            case R.id.action_toggle_markers:
                if (item.isChecked()) {
                    mClusterManager.clearItems();
                    item.setChecked(false);
                } else {
                    mClusterManager.addItems(visibleMarkers);
                    item.setChecked(true);
                }
                return true;
            default:
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
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        mMap.setOnInfoWindowClickListener(this);

        getLatLngCoordinates();

        setUpMarkers();
        setUpHeatMap();
    }

    private void setUpFilter(){
        databaseHandler.search(DatabaseHandler.TYPES, null);
        ArrayList<Map<String, String>> types = databaseHandler.getResult(DatabaseHandler.TYPES);
        filterItems = new String[types.size()];
        boolean[] items = new boolean[types.size()];
        int i = 0;
        for (Map<String, String> row : types) {
            for (Map.Entry<String, String> entry : row.entrySet()) {
                filterItems[i] = entry.getValue();
                items[i] = true;
                i++;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Filter")
                .setMultiChoiceItems(filterItems, items, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {}
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        updateMarkersAndHeatMap();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        filterDialog = builder.create();
    }

    private void setUpHeatMap() {
        if (weightedLatLngList.isEmpty())
            return;
        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder().weightedData(weightedLatLngList).build();
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        mOverlay.setVisible(false);
    }

    private void setUpMarkers() {
        mClusterManager = new ClusterManager<>(this, mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
    }

    private void updateMarkersAndHeatMap(){
        SparseBooleanArray checkedItemPositions = filterDialog.getListView().getCheckedItemPositions();
        ArrayList<String> allowedTypes = new ArrayList<>();
        for(int i = 0; i < checkedItemPositions.size(); i++){
            if(checkedItemPositions.get(i)){
                allowedTypes.add(filterItems[i]);
            }
        }

        mClusterManager.clearItems();
        visibleMarkers = new ArrayList<>(markers);
        for (MapMarkers marker: markers) {
            if(!allowedTypes.contains(marker.getSnippet())){
                visibleMarkers.remove(marker);
            }
        }
        mClusterManager.addItems(visibleMarkers);
    }

    private void getLatLngCoordinates() {
        databaseHandler.search(DatabaseHandler.INFRASTRUCTURE, null);
        ArrayList<Map<String, String>> result = databaseHandler.getResult(DatabaseHandler.INFRASTRUCTURE);

        if (result.isEmpty())
            return;

        for (Map<String, String> row : result) {
            double lat = Double.parseDouble(row.get("Latitude"));
            double lng = Double.parseDouble(row.get("Longitude"));

            LatLng latLng = new LatLng(lat, lng);

            markers.add(new MapMarkers(latLng, "Quality: " + row.get("Quality"), row.get("Type")));

            double weighting = 100 - Double.parseDouble(row.get("Quality"));
            WeightedLatLng weightedLatLng = new WeightedLatLng(latLng, weighting);
            weightedLatLngList.add(weightedLatLng);
        }
        visibleMarkers = new ArrayList<>(markers);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
//        Log.d("ID", String.valueOf(marker.getPosition()));
        databaseHandler.findInfrastructureReports(marker.getPosition());
        Toast.makeText(this, "Info window clicked", Toast.LENGTH_SHORT).show();
        showResult();
    }

    private void showResult() {
        Intent intent = new Intent(this, DatabaseResultActivity.class);
        ArrayList<String> resultList = new ArrayList<>();
        StringBuilder resultString = new StringBuilder();
        ArrayList<Map<String, String>> result = databaseHandler.getResult(DatabaseHandler.REPORTS);

        for (Map<String, String> row : result) {
            int i = 0;
            for (Map.Entry<String, String> entry : row.entrySet()) {
                resultString.append(entry.getKey()).append(": ").append(entry.getValue());
                if (i++ != row.size() - 1) {
                    resultString.append("\n");
                }
            }
            resultList.add(resultString.toString());
            resultString.setLength(0);
        }

        intent.putExtra("resultList", resultList);
        startActivity(intent);
    }
}
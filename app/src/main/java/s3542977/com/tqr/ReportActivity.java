package s3542977.com.tqr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class ReportActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    int quality = 0;
    double longitude = 0;
    double latitude = 0;
    TextView qualityText;
    EditText descriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        showCurrentLocation();
        qualityText = findViewById(R.id.qualityValue);
        descriptionText = findViewById(R.id.descriptionText);

        final SeekBar qualityBar = findViewById(R.id.qualityBar);
        qualityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                quality = progress;
                setQualityText();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setQualityText() {
        String text = String.valueOf(quality);
        qualityText.setText(text);
    }

    private void showCurrentLocation() {
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

        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        latitude = lastKnownLocation.getLatitude();
        longitude = lastKnownLocation.getLongitude();

        TextView textView = findViewById(R.id.locationText);
        String text = "Current Location:\nLatitude: " + latitude +
                "\nLongitude: " + longitude;
        textView.setText(text);
    }

    public void takePhoto(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void submitReport(View view){
        String description = descriptionText.getText().toString();

        String query = "INSERT INTO InfrastructureQuality VALUES("+latitude+","+longitude+","+ quality+", '"+ description+"');";

        SQLiteDatabase mydatabase = openOrCreateDatabase("database",MODE_PRIVATE,null);
//        mydatabase.execSQL("DROP TABLE InfrastructureQuality;");
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS InfrastructureQuality(Latitude NUMERIC(10,5), Longitude NUMERIC(10,5), Quality NUMERIC, Description VARCHAR);");
        mydatabase.execSQL(query);

        Cursor resultSet = mydatabase.rawQuery("Select * from InfrastructureQuality",null);
        resultSet.moveToFirst();
        Log.i("databaseThing", query);

        do{
            Log.i("databaseThing latitude", resultSet.getString(0));
            Log.i("databaseThing longitude", resultSet.getString(1));
            Log.i("databaseThing quality", resultSet.getString(2));
            Log.i("databaseThing desc", resultSet.getString(3));
        }while (resultSet.moveToNext());
    }
}

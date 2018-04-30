package s3542977.com.tqr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;

public class ReportActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    int quality = 0;
    double longitude = 0;
    double latitude = 0;
    TextView qualityText;
    EditText descriptionText;
    DatabaseHandler databaseHandler;
    String imageFilePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        showCurrentLocation();
        qualityText = findViewById(R.id.qualityValue);
        descriptionText = findViewById(R.id.descriptionText);
        databaseHandler = new DatabaseHandler(this);

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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Test", "No ACCESS_COARSE_LOCATION Permission");
            return;
        }

        if (locationManager == null)
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

    public void takePhoto(View view) {
        //http://www.codexpedia.com/android/action_image_capture-intent-for-taking-image-in-android/
        long time= System.currentTimeMillis();
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir.getAbsolutePath() + "/" + time + ".jpg");
        imageFilePath = image.getAbsolutePath();
        Log.d("takePicture", "picture will be saved at: " + imageFilePath);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void submitReport(View view) {
        String description = descriptionText.getText().toString();
        databaseHandler.addEntry(latitude, longitude, quality, description, imageFilePath);
    }
}

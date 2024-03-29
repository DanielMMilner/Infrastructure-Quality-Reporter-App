package s3542977.com.tqr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_EMPLOYEE_ID = 2;
    static final int REQUEST_INFRASTRUCTURE_ID = 3;
    static final int REQUEST_SPEED_TEST = 4;
    static final int REQUEST_INTERFERENCE_LEVEL = 5;

    int quality = 0;
    TextView qualityText;
    EditText descriptionText;
    EditText employeeIdText;
    EditText infrastructureIdText;
    EditText interferenceLevel;
    EditText speedTestText;
    ImageView photo;
    DatabaseHandler databaseHandler;
    String imageFilePath = "";
    SeekBar qualityBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        qualityText = findViewById(R.id.qualityValue);
        descriptionText = findViewById(R.id.descriptionText);
        employeeIdText = findViewById(R.id.employeeId);
        infrastructureIdText = findViewById(R.id.infrastructureID);
        interferenceLevel = findViewById(R.id.interferenceLevel);
        photo = findViewById(R.id.photoTaken);
        speedTestText = findViewById(R.id.speedTestText);

        databaseHandler = new DatabaseHandler(this);

        qualityBar = findViewById(R.id.qualityBar);
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
        String text = "Quality Rating: " + String.valueOf(quality);
        qualityText.setText(text);
    }

    public void buttonPressed(View view) {
        Intent intent;
        switch (view.getId()) {
            case (R.id.empolyeeAdd):
                intent = new Intent(this, AddToDatabaseActivity.class);
                intent.putExtra("Table", DatabaseHandler.EMPLOYEES);
                intent.putExtra("returnData", true);
                startActivityForResult(intent, REQUEST_EMPLOYEE_ID);
                break;
            case (R.id.empolyeeSearch):
                intent = new Intent(this, SearchDatabaseActivity.class);
                intent.putExtra("Table", DatabaseHandler.EMPLOYEES);
                intent.putExtra("returnData", true);
                startActivityForResult(intent, REQUEST_EMPLOYEE_ID);
                break;
            case (R.id.infrastructureAdd):
                intent = new Intent(this, AddToDatabaseActivity.class);
                intent.putExtra("Table", DatabaseHandler.INFRASTRUCTURE);
                intent.putExtra("returnData", true);
                startActivityForResult(intent, REQUEST_INFRASTRUCTURE_ID);
                break;
            case (R.id.infrastructureSearch):
                intent = new Intent(this, SearchDatabaseActivity.class);
                intent.putExtra("Table", DatabaseHandler.INFRASTRUCTURE);
                intent.putExtra("returnData", true);
                startActivityForResult(intent, REQUEST_INFRASTRUCTURE_ID);
                break;
            case (R.id.getSpeedButton):
                intent = new Intent(this, SpeedTestActivity.class);
                intent.putExtra("returnData", true);
                startActivityForResult(intent, REQUEST_SPEED_TEST);
                break;
            case (R.id.getInterferenceButton):
                intent = new Intent(this, BluetoothActivity.class);
                intent.putExtra("returnData", true);
                startActivityForResult(intent, REQUEST_INTERFERENCE_LEVEL);
                break;
        }
    }

    public void takePhoto(View view) {
        long time = System.currentTimeMillis();
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir.getAbsolutePath() + "/" + time + ".jpg");
        imageFilePath = image.getAbsolutePath();
        Log.d("takePicture", "picture will be saved at: " + imageFilePath);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri apkURI = FileProvider.getUriForFile(this, this.getApplicationContext()
                .getPackageName() + ".provider", image);


        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, apkURI);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (REQUEST_INFRASTRUCTURE_ID):
                if (resultCode == Activity.RESULT_OK) {
                    Integer returnValueId = data.getIntExtra("idResult", -1);
                    Log.d("returnValueId Report", String.valueOf(returnValueId));
                    infrastructureIdText.setText(String.valueOf(returnValueId));
                }
                break;
            case (REQUEST_EMPLOYEE_ID):
                if (resultCode == Activity.RESULT_OK) {
                    Integer returnValueId = data.getIntExtra("idResult", -1);
                    Log.d("returnValueId Report", String.valueOf(returnValueId));
                    employeeIdText.setText(String.valueOf(returnValueId));
                }
                break;
            case (REQUEST_IMAGE_CAPTURE):
                if (new File(imageFilePath).exists()) {
                    Bitmap imageBitmap = BitmapFactory.decodeFile(imageFilePath);
                    imageBitmap = Bitmap.createBitmap(imageBitmap);
                    photo.setImageBitmap(imageBitmap);
                    photo.setRotation(90);
                } else {
                    photo.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_camera, this.getTheme()));
                }
                break;
            case (REQUEST_SPEED_TEST):
                if (resultCode == Activity.RESULT_OK) {
                    double returnValueSpeed = data.getDoubleExtra("speedResult", 0);
                    Log.d("returnValueSpeed Report", String.valueOf(returnValueSpeed));
                    speedTestText.setText(String.format(Locale.UK, "%.2f", returnValueSpeed));
                }
                break;
            default:
                Log.d("Result", "Fail");
        }
    }

    public void submitReport(View view) {
        String employeeId = employeeIdText.getText().toString();
        if (employeeId.isEmpty()) {
            errorMessage("Employee ID");
            return;
        }
        String infrastructureId = infrastructureIdText.getText().toString();
        if (infrastructureId.isEmpty()) {
            errorMessage("Infrastructure ID");
            return;
        }

        String description = descriptionText.getText().toString();
        if (description.isEmpty()) {
            errorMessage("Description");
            return;
        }

        String interferenceLevelString = interferenceLevel.getText().toString();
        String speedString = speedTestText.getText().toString();

        Map<String, String> options = new HashMap<>();

        options.put("idEmployee", employeeId);
        options.put("idInfrastructure", infrastructureId);
        options.put("Quality", String.valueOf(quality));
        options.put("InterferenceLevel", interferenceLevelString);
        options.put("Speed", speedString);
        options.put("Description", description);
        options.put("ImageFilePath", imageFilePath);

        databaseHandler.addEntry(DatabaseHandler.REPORTS, options);

        qualityBar.setProgress(0);
        descriptionText.setText("");
        employeeIdText.setText("");
        infrastructureIdText.setText("");
        interferenceLevel.setText("");
        speedTestText.setText("");
        photo.setImageBitmap(null);
        imageFilePath = "";

        Toast.makeText(this, "Successfully added to database", Toast.LENGTH_LONG).show();
    }

    private void errorMessage(String desc) {
        Toast.makeText(this, "Please add a " + desc, Toast.LENGTH_LONG).show();
    }
}
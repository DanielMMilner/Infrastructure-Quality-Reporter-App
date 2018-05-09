package s3542977.com.tqr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    int quality = 0;
    TextView qualityText;
    EditText descriptionText;
    EditText employeeIdText;
    EditText infrastructureIdText;
    EditText interferenceLevel;

    ImageView photo;

    DatabaseHandler databaseHandler;
    String imageFilePath = "";

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
        String text = "Quality: " + String.valueOf(quality);
        qualityText.setText(text);
    }

    public void buttonPressed(View view) {
        Intent intent;
        switch (view.getId()) {
            case (R.id.empolyeeAdd):
                intent = new Intent(this, AddToDatabaseActivity.class);
                intent.putExtra("Table", DatabaseHandler.EMPLOYEES);
                startActivity(intent);
                break;
            case (R.id.empolyeeSearch):
                intent = new Intent(this, SearchDatabaseActivity.class);
                intent.putExtra("Table", DatabaseHandler.EMPLOYEES);
                startActivity(intent);
                break;
            case (R.id.infrastructureAdd):
                intent = new Intent(this, AddToDatabaseActivity.class);
                intent.putExtra("Table", DatabaseHandler.INFRASTRUCTURE);
                startActivity(intent);
                break;
            case (R.id.infrastructureSearch):
                intent = new Intent(this, SearchDatabaseActivity.class);
                intent.putExtra("Table", DatabaseHandler.INFRASTRUCTURE);
                startActivity(intent);
                break;
            case (R.id.getInterferenceButton):
                break;
        }
    }

    public void takePhoto(View view) {
        //http://www.codexpedia.com/android/action_image_capture-intent-for-taking-image-in-android/
        long time = System.currentTimeMillis();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (new File(imageFilePath).exists()) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(imageFilePath);
            imageBitmap = Bitmap.createBitmap(imageBitmap);
            photo.setImageBitmap(imageBitmap);
        } else {
            photo.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_camera, this.getTheme()));
        }
    }

    public void submitReport(View view) {
        String description = descriptionText.getText().toString();
        String employeeId = employeeIdText.getText().toString();
        String infrastructureId = infrastructureIdText.getText().toString();
        String interferenceLevelString = interferenceLevel.getText().toString();

        Map<String, String> options = new HashMap<>();

        options.put("idEmployee", employeeId);
        options.put("idInfrastructure", infrastructureId);
        options.put("Quality", String.valueOf(quality));
        options.put("InterferenceLevel", interferenceLevelString);
        options.put("Description", description);
        options.put("ImageFilePath", imageFilePath);

        databaseHandler.addEntry(DatabaseHandler.REPORTS, options);
    }
}
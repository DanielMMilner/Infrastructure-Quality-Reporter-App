package s3542977.com.tqr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddToDatabaseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText firstOption;
    private EditText secondOption;
    private EditText thirdOption;
    private Spinner typeSpinner;
    private TextView addResultText;
    private int tableSpinnerPosition;
    private DatabaseHandler databaseHandler;
    double longitude = 0;
    double latitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_database);
        firstOption = findViewById(R.id.firstOption2);
        secondOption = findViewById(R.id.secondOption2);
        thirdOption = findViewById(R.id.thirdOption2);

        addResultText = findViewById(R.id.addResultText);

        Spinner tableSpinner = findViewById(R.id.addToDatabaseSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.search_database_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tableSpinner.setAdapter(adapter);
        tableSpinner.setOnItemSelectedListener(this);

        Intent mIntent = getIntent();
        tableSpinnerPosition = mIntent.getIntExtra("Table", 0);
        tableSpinner.setSelection(tableSpinnerPosition);

        databaseHandler = new DatabaseHandler(this);

        typeSpinner = findViewById(R.id.typeSpinner);
        typeSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
        if (spinner.getId() == R.id.addToDatabaseSpinner) {
            tableSpinnerPosition = position;
            clearOptions();
            setOptions();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void clearOptions() {
        firstOption.setText("");
        secondOption.setText("");
        thirdOption.setText("");
        addResultText.setText("");
    }

    private void setOptions() {
        firstOption.setVisibility(View.VISIBLE);
        secondOption.setVisibility(View.VISIBLE);
        thirdOption.setVisibility(View.INVISIBLE);
        typeSpinner.setVisibility(View.INVISIBLE);

        if (tableSpinnerPosition == DatabaseHandler.EMPLOYEES) {
            firstOption.setHint("Name");
            firstOption.setInputType(InputType.TYPE_CLASS_TEXT);
            secondOption.setHint("Phone");
            secondOption.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (tableSpinnerPosition == DatabaseHandler.INFRASTRUCTURE) {
            firstOption.setHint("Latitude");
            firstOption.setInputType(InputType.TYPE_CLASS_NUMBER);
            secondOption.setHint("Longitude");
            secondOption.setInputType(InputType.TYPE_CLASS_NUMBER);
            thirdOption.setVisibility(View.VISIBLE);
            typeSpinner.setVisibility(View.VISIBLE);

            getCurrentLocation();

            firstOption.setText(String.valueOf((latitude)));
            secondOption.setText(String.valueOf((longitude)));

            ArrayList<String> options = databaseHandler.getTypesList();
            if (options != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, options);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                typeSpinner.setAdapter(adapter);
            } else {
                Log.d("Types List", "There is no types in the database");
            }
        } else if (tableSpinnerPosition == DatabaseHandler.TYPES) {
            firstOption.setHint("Type");
            firstOption.setInputType(InputType.TYPE_CLASS_TEXT);
            secondOption.setVisibility(View.INVISIBLE);
        } else if (tableSpinnerPosition == DatabaseHandler.REPORTS) {
            Intent intent = new Intent(this, ReportActivity.class);
            startActivity(intent);
        }
    }

    private void getCurrentLocation() {
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

        if(lastKnownLocation != null){
            latitude = lastKnownLocation.getLatitude();
            longitude = lastKnownLocation.getLongitude();
        }
    }

    public void addToDatabase(View view) {
        String firstOptionText;
        String secondOptionText;
        String thirdOptionText;

        Map<String, String> options = new HashMap<>();

        if (tableSpinnerPosition == DatabaseHandler.EMPLOYEES) {
            firstOptionText = String.valueOf(firstOption.getText());
            secondOptionText = String.valueOf(secondOption.getText());

            if (firstOptionText.isEmpty() || secondOptionText.isEmpty()) {
                addResultText.setText(R.string.addToDatabaseError);
                return;
            }

            options.put("Name", firstOptionText);
            options.put("Phone", secondOptionText);
        } else if (tableSpinnerPosition == DatabaseHandler.INFRASTRUCTURE) {
            firstOptionText = String.valueOf(firstOption.getText());
            secondOptionText = String.valueOf(secondOption.getText());
            thirdOptionText = String.valueOf(thirdOption.getText());

            String type = typeSpinner.getSelectedItem().toString();

            if (firstOptionText.isEmpty() || secondOptionText.isEmpty() || thirdOptionText.isEmpty()) {
                addResultText.setText(R.string.addToDatabaseError);
                return;
            }

            options.put("Latitude", firstOptionText);
            options.put("Longitude", secondOptionText);
            options.put("Quality", thirdOptionText);
            options.put("idType", type);
        } else if (tableSpinnerPosition == DatabaseHandler.TYPES) {
            firstOptionText = String.valueOf(firstOption.getText());

            if (firstOptionText.isEmpty()) {
                addResultText.setText(R.string.addToDatabaseError);
                return;
            }

            options.put("idType", firstOptionText);
        }

        clearOptions();
        databaseHandler.addEntry(tableSpinnerPosition, options);

        ArrayList<Map<String, String>> result = databaseHandler.getLastRow(tableSpinnerPosition);

        if (result.isEmpty()){
            if(tableSpinnerPosition == DatabaseHandler.TYPES) {
                String text = "Successfully added to database\n Type: " + options.get("idType");
                addResultText.setText(text);
            }else{
                addResultText.setText(R.string.addToDatabaseFailed);
            }
        }else{
            StringBuilder resultString = new StringBuilder();
            resultString.append("Successfully added to Database").append('\n');

            for (Map<String, String> row : result) {
                for (Map.Entry<String, String> entry : row.entrySet()) {
                    resultString.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
                resultString.append("\n");
            }

            addResultText.setText(resultString);
        }

        if(getIntent().getBooleanExtra("returnData", false)){
            Intent resultIntent = new Intent();
            ArrayList<Integer> resultID = databaseHandler.getResultIdsList(tableSpinnerPosition);

            resultIntent.putExtra("idResult", resultID.get(0));
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }
}

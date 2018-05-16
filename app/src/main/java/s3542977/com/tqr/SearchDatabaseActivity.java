package s3542977.com.tqr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchDatabaseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    static final int REQUEST_ID = 1;

    private EditText firstOption;
    private EditText secondOption;
    private EditText thirdOption;
    private TextView resultsText;
    private TextView firstOr;
    private TextView secondOr;
    private Button nearbyButton;
    private Spinner typeSpinner;

    int spinnerPosition;
    DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_database);
        firstOption = findViewById(R.id.firstOption);
        secondOption = findViewById(R.id.secondOption);
        thirdOption = findViewById(R.id.thirdOption);

        nearbyButton = findViewById(R.id.nearbyButton);

        resultsText = findViewById(R.id.resultsText);
        resultsText.setMovementMethod(new ScrollingMovementMethod());

        firstOr = findViewById(R.id.firstOr);
        secondOr = findViewById(R.id.secondOr);

        typeSpinner = findViewById(R.id.typeSpinner2);

        Spinner tableSpinner = findViewById(R.id.searchDatabaseSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.search_database_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tableSpinner.setAdapter(adapter);
        tableSpinner.setOnItemSelectedListener(this);
        Intent mIntent = getIntent();
        spinnerPosition = mIntent.getIntExtra("Table", 0);
        if (mIntent.getBooleanExtra("returnData", false)) {
            tableSpinner.setEnabled(false);
        }
        tableSpinner.setSelection(spinnerPosition);
        databaseHandler = new DatabaseHandler(this);

        setOptions();
    }

    private void setOptions() {
        firstOption.setVisibility(View.VISIBLE);
        secondOption.setVisibility(View.VISIBLE);
        thirdOption.setVisibility(View.VISIBLE);
        firstOr.setVisibility(View.VISIBLE);
        secondOr.setVisibility(View.VISIBLE);
        typeSpinner.setVisibility(View.INVISIBLE);

        if (spinnerPosition == DatabaseHandler.EMPLOYEES) {
            firstOption.setHint("Employee ID");
            firstOption.setInputType(InputType.TYPE_CLASS_NUMBER);
            secondOption.setHint("Name");
            secondOption.setInputType(InputType.TYPE_CLASS_TEXT);
            thirdOption.setHint("Phone");
            thirdOption.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (spinnerPosition == DatabaseHandler.INFRASTRUCTURE) {
            firstOption.setHint("Infrastructure ID");
            firstOption.setInputType(InputType.TYPE_CLASS_NUMBER);
            secondOption.setVisibility(View.INVISIBLE);
            thirdOption.setVisibility(View.INVISIBLE);
            typeSpinner.setVisibility(View.VISIBLE);
            secondOr.setVisibility(View.VISIBLE);
            ArrayList<String> options = databaseHandler.getTypesList();
            if (!options.isEmpty()) {
                options.add("");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, options);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                typeSpinner.setAdapter(adapter);
            } else {
                Log.d("Types List", "There is no types in the database");
            }
            typeSpinner.setSelection(options.size() - 1);
        } else if (spinnerPosition == DatabaseHandler.REPORTS) {
            firstOption.setHint("Report ID");
            secondOption.setHint("Employee ID");
            thirdOption.setHint("Infrastructure ID");
            firstOption.setInputType(InputType.TYPE_CLASS_NUMBER);
            secondOption.setInputType(InputType.TYPE_CLASS_NUMBER);
            thirdOption.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (spinnerPosition == DatabaseHandler.TYPES) {
            firstOption.setVisibility(View.INVISIBLE);
            secondOption.setVisibility(View.INVISIBLE);
            thirdOption.setVisibility(View.INVISIBLE);
            firstOr.setVisibility(View.INVISIBLE);
            secondOr.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerPosition = position;
        clearOptions();
        setOptions();

        if (spinnerPosition == DatabaseHandler.TYPES) {
            databaseHandler.search(spinnerPosition, null);
            showResult();
        }

        if (spinnerPosition == DatabaseHandler.INFRASTRUCTURE) {
            nearbyButton.setVisibility(View.VISIBLE);
        } else {
            nearbyButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void clearOptions() {
        firstOption.setText("");
        secondOption.setText("");
        thirdOption.setText("");
        resultsText.setText("");
    }

    public void searchNearby(View view) {
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
        double longitude = 0;
        double latitude = 0;


        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        if (lastKnownLocation != null) {
            latitude = lastKnownLocation.getLatitude();
            longitude = lastKnownLocation.getLongitude();
        }
        double maxLongitude = longitude + 0.0002;
        double maxLatitude = latitude + 0.0002;
        double minLongitude = longitude - 0.0002;
        double minLatitude = latitude - 0.0002;

        databaseHandler.searchLatLngInRange(maxLatitude, maxLongitude, minLatitude, minLongitude);

        showResult();
    }

    public void searchDB(View view) {
        String firstOptionText;
        String secondOptionText;
        String thirdOptionText;

        Map<String, String> options = new HashMap<>();

        if (spinnerPosition == DatabaseHandler.EMPLOYEES) {
            firstOptionText = String.valueOf(firstOption.getText());
            secondOptionText = String.valueOf(secondOption.getText());
            thirdOptionText = String.valueOf(thirdOption.getText());

            if (!firstOptionText.isEmpty())
                options.put("idEmployee", firstOptionText);
            if (!secondOptionText.isEmpty())
                options.put("Name", secondOptionText);
            if (!thirdOptionText.isEmpty())
                options.put("Phone", thirdOptionText);
        } else if (spinnerPosition == DatabaseHandler.INFRASTRUCTURE) {
            firstOptionText = String.valueOf(firstOption.getText());
            String type = typeSpinner.getSelectedItem().toString();
            if (!firstOptionText.isEmpty())
                options.put("idInfrastructure", firstOptionText);
            if (!type.isEmpty())
                options.put("idType", type);
        } else if (spinnerPosition == DatabaseHandler.REPORTS) {
            firstOptionText = String.valueOf(firstOption.getText());
            secondOptionText = String.valueOf(secondOption.getText());
            thirdOptionText = String.valueOf(thirdOption.getText());

            if (!firstOptionText.isEmpty())
                options.put("idReports", firstOptionText);
            if (!secondOptionText.isEmpty())
                options.put("idEmployee", secondOptionText);
            if (!thirdOptionText.isEmpty())
                options.put("idInfrastructure", thirdOptionText);
        }

        if (!options.isEmpty()) {
            databaseHandler.search(spinnerPosition, options);
            showResult();
        }
    }

    private void showResult() {
        Intent intent = new Intent(this, DatabaseResultActivity.class);
        ArrayList<String> resultList = new ArrayList<>();
        StringBuilder resultString = new StringBuilder();
        ArrayList<Map<String, String>> result = databaseHandler.getResult(spinnerPosition);
        ArrayList<Integer> resultIdList = databaseHandler.getResultIdsList(spinnerPosition);

        if (result.isEmpty()) {
            resultString.append("No results found.");
            resultsText.setText(resultString);
            return;
        }

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
        intent.putExtra("resultIdList", resultIdList);
        startActivityForResult(intent, REQUEST_ID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent resultIntent = new Intent();

        switch (requestCode) {
            case (REQUEST_ID): {
                if (resultCode == Activity.RESULT_OK) {
                    String returnValue = data.getStringExtra("chosenResult");
                    Integer returnValueId = data.getIntExtra("idResult", -1);

                    Log.d("returnValue", returnValue);
                    Log.d("returnValueId", String.valueOf(returnValueId));

                    resultIntent.putExtra("idResult", returnValueId);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
                break;
            }
            default:
                Log.d("Result", "Fail");
        }
    }
}
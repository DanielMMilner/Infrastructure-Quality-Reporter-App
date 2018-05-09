package s3542977.com.tqr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

    private static final int EMPLOYEES = 0;
    private static final int INFRASTRUCTURE = 1;
    private static final int REPORTS = 2;
    private static final int TYPES = 3;

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
        tableSpinnerPosition = 0;

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

        if (tableSpinnerPosition == EMPLOYEES) {
            firstOption.setHint("Name");
            secondOption.setHint("Phone");
        } else if (tableSpinnerPosition == INFRASTRUCTURE) {
            firstOption.setHint("Latitude");
            secondOption.setHint("Longitude");
            thirdOption.setVisibility(View.VISIBLE);
            typeSpinner.setVisibility(View.VISIBLE);

            ArrayList<String> options = databaseHandler.getTypesList();
            if (options != null) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, options);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                typeSpinner.setAdapter(adapter);
            } else {
                Log.d("Types List", "There is no types in the database");
            }
        } else if (tableSpinnerPosition == TYPES) {
            firstOption.setHint("Type");
            secondOption.setVisibility(View.INVISIBLE);
        } else if (tableSpinnerPosition == REPORTS) {
            Intent intent = new Intent(this, ReportActivity.class);
            startActivity(intent);
        }
    }

    public void addToDatabase(View view) {
        String firstOptionText;
        String secondOptionText;
        String thirdOptionText;

        Map<String, String> options = new HashMap<>();

        if (tableSpinnerPosition == EMPLOYEES) {
            firstOptionText = String.valueOf(firstOption.getText());
            secondOptionText = String.valueOf(secondOption.getText());

            if (firstOptionText.isEmpty() || secondOptionText.isEmpty()) {
                addResultText.setText(R.string.addToDatabaseError);
                return;
            }

            options.put("Name", firstOptionText);
            options.put("Phone", secondOptionText);
        } else if (tableSpinnerPosition == INFRASTRUCTURE) {
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
        } else if (tableSpinnerPosition == TYPES) {
            firstOptionText = String.valueOf(firstOption.getText());

            if (firstOptionText.isEmpty()) {
                addResultText.setText(R.string.addToDatabaseError);
                return;
            }

            options.put("idType", firstOptionText);
        }

        clearOptions();

        addResultText.setText(R.string.addToDatabse);

        databaseHandler.addEntry(tableSpinnerPosition, options);
    }
}

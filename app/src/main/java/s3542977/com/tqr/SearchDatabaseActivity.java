package s3542977.com.tqr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.HashMap;
import java.util.Map;

public class SearchDatabaseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private EditText firstOption;
    private EditText secondOption;
    private EditText thirdOption;
    private Spinner spinner;
    int spinnerPosition;
    private static final int EMPLOYEES = 0;
    private static final int INFRASTRUCTURE = 1;
    private static final int REPORTS = 2;
    private static final int TYPES = 3;
    DatabaseHandler databaseHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_database);
        firstOption = findViewById(R.id.firstOption);
        secondOption = findViewById(R.id.secondOption);
        thirdOption = findViewById(R.id.thirdOption);

        spinner = findViewById(R.id.searchDatabaseSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.search_database_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinnerPosition = 0;

        databaseHandler = new DatabaseHandler(this);

        setOptions();
    }

    private void setOptions() {
        firstOption.setVisibility(View.VISIBLE);
        secondOption.setVisibility(View.VISIBLE);
        thirdOption.setVisibility(View.VISIBLE);

        if(spinnerPosition == EMPLOYEES){
            firstOption.setHint("Employee ID");
            secondOption.setHint("Name");
            thirdOption.setHint("Phone");
        }else if(spinnerPosition == INFRASTRUCTURE){
            firstOption.setHint("Infrastructure ID");
            secondOption.setHint("Type");
            thirdOption.setVisibility(View.INVISIBLE);
        }else if(spinnerPosition == REPORTS){
            firstOption.setHint("Report ID");
            secondOption.setHint("Employee ID");
            thirdOption.setHint("Infrastructure ID");
        }else if(spinnerPosition == TYPES){
            firstOption.setVisibility(View.INVISIBLE);
            secondOption.setVisibility(View.INVISIBLE);
            thirdOption.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerPosition = position;
        clearOptions();
        setOptions();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void clearOptions() {
        firstOption.setText("");
        secondOption.setText("");
        thirdOption.setText("");
    }

    public void searchDB(View view){
        String firstOptionText;
        String secondOptionText;
        String thirdOptionText;

        Map<String, String> options = new HashMap<>();


        if(spinnerPosition == EMPLOYEES){
            firstOptionText = String.valueOf(firstOption.getText());
            secondOptionText = String.valueOf(secondOption.getText());
            thirdOptionText = String.valueOf(thirdOption.getText());

            options.put("idEmployee", firstOptionText);
            options.put("Name", secondOptionText);
            options.put("Phone", thirdOptionText);
        }else if(spinnerPosition == INFRASTRUCTURE){
            firstOptionText = String.valueOf(firstOption.getText());
            secondOptionText = String.valueOf(secondOption.getText());

            options.put("idInfrastructure", firstOptionText);
            options.put("idType", secondOptionText);
        }else if(spinnerPosition == REPORTS){
            firstOptionText = String.valueOf(firstOption.getText());
            secondOptionText = String.valueOf(secondOption.getText());
            thirdOptionText = String.valueOf(thirdOption.getText());

            options.put("idReports", firstOptionText);
            options.put("idEmployee", secondOptionText);
            options.put("idInfrastructure", thirdOptionText);
        }

        databaseHandler.search(spinnerPosition, options);
    }
}
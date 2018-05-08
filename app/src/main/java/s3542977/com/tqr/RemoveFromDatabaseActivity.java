package s3542977.com.tqr;

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

public class RemoveFromDatabaseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText idOption;
    private TextView removeResult;
    private DatabaseHandler databaseHandler;
    private int spinnerPosition = 0;
    private Spinner typeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_from_database);

        databaseHandler = new DatabaseHandler(this);

        idOption = findViewById(R.id.idOption);

        removeResult = findViewById(R.id.removeResult);

        Spinner tableSpinner = findViewById(R.id.removeFromDatabaseSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.search_database_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tableSpinner.setAdapter(adapter);
        tableSpinner.setOnItemSelectedListener(this);

        typeSpinner = findViewById(R.id.typeSpinner3);
        typeSpinner.setOnItemSelectedListener(this);

        ArrayList<String> options = databaseHandler.getTypesList();
        if (options != null) {
            ArrayAdapter<String> adapterTypes = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, options);
            adapterTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            typeSpinner.setAdapter(adapterTypes);
        } else {
            Log.d("Types List", "There is no types in the database");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
        if (spinner.getId() == R.id.removeFromDatabaseSpinner) {
            spinnerPosition = position;
            if (position == DatabaseHandler.TYPES) {
                idOption.setVisibility(View.INVISIBLE);
                typeSpinner.setVisibility(View.VISIBLE);
            } else {
                idOption.setVisibility(View.VISIBLE);
                typeSpinner.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void buttonPressed(View view) {
        String id;

        if (spinnerPosition == DatabaseHandler.TYPES) {
            id = typeSpinner.getSelectedItem().toString();
        } else {
            id = String.valueOf(idOption.getText());
        }

        if (id.isEmpty()) {
            removeResult.setText(R.string.enterAnId);
            return;
        }

        if (databaseHandler.removeEntry(spinnerPosition, id)) {
            removeResult.setText(R.string.removeSuccess);
        } else {
            removeResult.setText(R.string.removeFailed);
        }
    }
}

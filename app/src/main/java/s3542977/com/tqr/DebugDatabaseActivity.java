package s3542977.com.tqr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DebugDatabaseActivity extends AppCompatActivity {
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_database);
        databaseHandler = new DatabaseHandler(this);
    }

    public void buttonPressed(View view) {
        switch (view.getId()) {
            case R.id.addRandomReports10:
                databaseHandler.generateRandomEntries(DatabaseHandler.REPORTS, 10);
                break;
            case R.id.addRandomReports100:
                databaseHandler.generateRandomEntries(DatabaseHandler.REPORTS, 100);
                break;
            case R.id.addRandomInfrastructure10:
                databaseHandler.generateRandomEntries(DatabaseHandler.INFRASTRUCTURE, 10);
                break;
            case R.id.addRandomeEmployees10:
                databaseHandler.generateRandomEntries(DatabaseHandler.EMPLOYEES, 10);
                break;
            case R.id.clearEmployees:
                databaseHandler.clearTable(DatabaseHandler.EMPLOYEES);
                break;
            case R.id.clearReports:
                databaseHandler.clearTable(DatabaseHandler.REPORTS);
                break;
            case R.id.clearTypes:
                databaseHandler.clearTable(DatabaseHandler.TYPES);
                break;
            case R.id.clearInfrastructure:
                databaseHandler.clearTable(DatabaseHandler.INFRASTRUCTURE);
                break;
        }
    }
}

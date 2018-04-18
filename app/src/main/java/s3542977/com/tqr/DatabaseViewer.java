package s3542977.com.tqr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DatabaseViewer extends AppCompatActivity {
    String text;
    TextView textView;
    DatabaseHandler databaseHandler;
    Button nextRowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_viewer);
        textView = findViewById(R.id.dbText);
        nextRowButton = findViewById(R.id.nextRowButton);
        databaseHandler = new DatabaseHandler(this);

        if (databaseHandler.isDatabaseEmpty()){
            setStateToEmptyDatabase();
        }else{
            setText();
        }
    }

    public void clearDB(View view) {
        databaseHandler.clearDatabase();
        setStateToEmptyDatabase();
    }

    private void setStateToEmptyDatabase(){
        Log.i("Database", "IS EMPTY");
        nextRowButton.setEnabled(false);

        text = "Database is empty";
        textView.setText(text);
    }

    private void setText() {
        Log.i("Database", "NOT EMPTY");
        text = "latitude: " + databaseHandler.getLatitudeAsString() +
                "\nlongitude " + databaseHandler.getLongitudeAsString() +
                "\nquality " + databaseHandler.getQuality() +
                "\ndescription " + databaseHandler.getDescription();
        nextRowButton.setEnabled(true);

        textView.setText(text);
    }

    public void nextRow(View view) {
        databaseHandler.nextRow();
        setText();
    }

    public void addRandomData(View view){
        for(int i = 0; i < 10; i++){
            databaseHandler.generateRandomEntry();
        }
        databaseHandler.isDatabaseEmpty();
        setText();
    }
}

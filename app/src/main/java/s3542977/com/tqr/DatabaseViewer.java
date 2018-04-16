package s3542977.com.tqr;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class DatabaseViewer extends AppCompatActivity {
    Cursor resultSet;
    String text;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_viewer);

        textView = findViewById(R.id.dbText);

        SQLiteDatabase mydatabase = openOrCreateDatabase("database", MODE_PRIVATE, null);
        resultSet = mydatabase.rawQuery("Select * from InfrastructureQuality", null);
        if (!resultSet.moveToFirst()) {
            emptyDB();
        } else {
            setText();
        }
    }

    public void clearDB(View view) {
        SQLiteDatabase mydatabase = openOrCreateDatabase("database", MODE_PRIVATE, null);
        mydatabase.execSQL("delete from InfrastructureQuality;");
        emptyDB();
    }

    private void setText() {
        text = "latitude: " + resultSet.getString(0) +
                "\nlongitude " + resultSet.getString(1) +
                "\nquality " + resultSet.getString(2) +
                "\ndescription " + resultSet.getString(3);

        textView.setText(text);
    }

    private void emptyDB() {
        text = "Database is empty";
        textView.setText(text);
        Button nextRowButton = findViewById(R.id.nextRowButton);
        nextRowButton.setEnabled(false);
    }

    public void nextRow(View view) {
        if (!resultSet.isLast()) {
            resultSet.moveToNext();
        } else {
            resultSet.moveToFirst();
        }
        setText();
    }

    public void addRandomData(View view){
        Random generator = new Random();
        double lat;
        double log;
        int quality;
        SQLiteDatabase mydatabase = openOrCreateDatabase("database", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS InfrastructureQuality(Latitude NUMERIC(10,5), Longitude NUMERIC(10,5), Quality NUMERIC, Description VARCHAR);");
        String query;

        for(int i = 0; i < 10; i++){
            log = generator.nextDouble() * .47 - 38;
            lat = generator.nextDouble() * .93 + 144.5;
            quality = generator.nextInt(101);
            query = "INSERT INTO InfrastructureQuality VALUES(" + lat + "," + log + "," + quality + ", '" + "Dummy data" + "');";
            mydatabase.execSQL(query);
        }
    }
}

package s3542977.com.tqr;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class DatabaseHandler {
    private Context context;
    private SQLiteDatabase database;
    private Cursor resultSet;

    DatabaseHandler(Context context) {
        this.context = context;
        database = context.openOrCreateDatabase("database", MODE_PRIVATE, null);
        Log.i("Database", "CREATING DB");
        database.execSQL("CREATE TABLE IF NOT EXISTS InfrastructureQuality(Latitude NUMERIC(10,5),"+
                " Longitude NUMERIC(10,5), Quality NUMERIC, Description VARCHAR);");
        Log.i("Database", "GETTING DB");
        resultSet = database.rawQuery("Select * from InfrastructureQuality", null);
        Log.i("Database", "DB RETRIEVED");
    }

    public boolean isDatabaseEmpty() {
        Log.i("Database", "MOVE TO FIRST");
        return !resultSet.moveToFirst();
    }

    public void generateRandomEntry() {
        Random generator = new Random();
        double log = generator.nextDouble() * .47 - 38;
        double lat = generator.nextDouble() * .93 + 144.5;
        int quality = generator.nextInt(101);
        String query = "INSERT INTO InfrastructureQuality VALUES(" + lat + "," + log + "," +
                quality + ", 'Dummy data');";
        database.execSQL(query);
        resultSet = database.rawQuery("Select * from InfrastructureQuality", null);
    }

    public void clearDatabase() {
        database.execSQL("delete from InfrastructureQuality;");
        resultSet = database.rawQuery("Select * from InfrastructureQuality", null);
    }

    public void nextRow(){
        if (!resultSet.isLast()) {
            resultSet.moveToNext();
        } else {
            resultSet.moveToFirst();
        }
    }

    public String getLatitude() {
        return resultSet.getString(0);
    }

    public String getLongitude() {
        return resultSet.getString(1);
    }

    public String getQuality() {
        return resultSet.getString(2);
    }

    public String getDescription() {
        return resultSet.getString(3);
    }
}

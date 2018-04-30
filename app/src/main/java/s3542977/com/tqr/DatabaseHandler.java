package s3542977.com.tqr;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class DatabaseHandler {
    private static final int LATITUDE_COLUMN = 0;
    private static final int LONGITUDE_COLUMN = 1;
    private static final int QUALITY_COLUMN = 2;
    private static final int DESCRIPTION_COLUMN = 3;
    private static final int IMAGE_FILE_PATH_COLUMN = 4;


    private SQLiteDatabase database;
    private Cursor resultSet;

    DatabaseHandler(Context context) {
        database = context.openOrCreateDatabase("database", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS InfrastructureQuality(Latitude NUMERIC(10,5)," +
                " Longitude NUMERIC(10,5), Quality NUMERIC, Description VARCHAR, ImageFilePath VARCHAR);");
        resultSet = database.rawQuery("Select * from InfrastructureQuality", null);
    }

    public boolean isDatabaseEmpty() {
        return !resultSet.moveToFirst();
    }

    public void generateRandomEntry() {
        Random generator = new Random();
        double lat = generator.nextDouble() * .47 - 38;
        double log = generator.nextDouble() * .93 + 144.5;
        int quality = generator.nextInt(101);
        String description = "Dummy Data";
        String imageFilePath = "";

        addEntry(lat,log, quality, description, imageFilePath);
    }

    public void addEntry(double lat, double log, int quality, String description, String imageFilePath){
        String query = "INSERT INTO InfrastructureQuality VALUES(" + lat + "," + log + "," +
                quality + ",'" + description + "','" + imageFilePath + "');";

        database.execSQL(query);
        resultSet = database.rawQuery("Select * from InfrastructureQuality", null);
    }

    public void clearDatabase() {
        database.execSQL("delete from InfrastructureQuality;");
        resultSet = database.rawQuery("Select * from InfrastructureQuality", null);
    }

    public boolean hasNext() {
        if (resultSet.isLast()) {
            resultSet.moveToFirst();
            return false;
        } else {
            resultSet.moveToNext();
            return true;
        }
    }

    public String getLatitudeAsString() {
        return resultSet.getString(LATITUDE_COLUMN);
    }

    public double getLatitudeAsDouble() {
        return resultSet.getDouble(LATITUDE_COLUMN);
    }

    public String getLongitudeAsString() {
        return resultSet.getString(LONGITUDE_COLUMN);
    }

    public double getLongitudeAsDouble() {
        return resultSet.getDouble(LONGITUDE_COLUMN);
    }

    public String getQualityAsString() {
        return resultSet.getString(QUALITY_COLUMN);
    }

    public double getQualityAsDouble() {
        return resultSet.getDouble(QUALITY_COLUMN);
    }

    public String getDescription() {
        return resultSet.getString(DESCRIPTION_COLUMN);
    }

    public String getImageFIlePath() {
        return resultSet.getString(IMAGE_FILE_PATH_COLUMN);
    }
}

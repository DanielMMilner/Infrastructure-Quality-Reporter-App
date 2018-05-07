package s3542977.com.tqr;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class DatabaseHandler {
    private static final int LATITUDE_COLUMN = 0;
    private static final int LONGITUDE_COLUMN = 1;
    private static final int QUALITY_COLUMN = 2;
    private static final int DESCRIPTION_COLUMN = 3;
    private static final int IMAGE_FILE_PATH_COLUMN = 4;

    private static final int EMPLOYEES = 0;
    private static final int INFRASTRUCTURE = 1;
    private static final int REPORTS = 2;
    private static final int TYPES = 3;

    private SQLiteDatabase database;
    private Cursor resultSet;

    DatabaseHandler(Context context) {
        database = context.openOrCreateDatabase("database", MODE_PRIVATE, null);

//        database.execSQL("DROP TABLE Reports;");
//        database.execSQL("DROP TABLE Employees;");
//        database.execSQL("DROP TABLE Infrastructure;");
//        database.execSQL("DROP TABLE Type;");

        String setUpQuery =
                        "CREATE TABLE IF NOT EXISTS Type(" +
                        "  idType VARCHAR(45) PRIMARY KEY);";
        database.execSQL(setUpQuery);

        setUpQuery =
                        "CREATE TABLE IF NOT EXISTS Infrastructure(" +
                        "  `idInfrastructure` INTEGER PRIMARY KEY," +
                        "  `idType` VARCHAR(45)," +
                        "  `Latitude` DOUBLE," +
                        "  `Longitude` DOUBLE," +
                        "  CONSTRAINT `idType`" +
                        "    FOREIGN KEY (`idType`)" +
                        "    REFERENCES Type (`idType`)" +
                        "    ON DELETE NO ACTION" +
                        "    ON UPDATE NO ACTION);";
        database.execSQL(setUpQuery);
        setUpQuery =
                        "CREATE TABLE IF NOT EXISTS Employees(" +
                        "  `idEmployee` INTEGER PRIMARY KEY," +
                        "  `Name` VARCHAR(45)," +
                        "  `Phone` VARCHAR(15));";
        database.execSQL(setUpQuery);
        setUpQuery =
                        "CREATE TABLE IF NOT EXISTS Reports(" +
                        "  `idReports` INTEGER PRIMARY KEY," +
                        "  `idEmployee` INTEGER," +
                        "  `idInfrastructure` INTEGER," +
                        "  `Quality` INTEGER," +
                        "  `InterferenceLevel` VARCHAR(45)," +
                        "  `ImageFilePath` VARCHAR(100)," +
                        "  CONSTRAINT `idEmployee`" +
                        "    FOREIGN KEY (`idEmployee`)" +
                        "    REFERENCES `Employees` (`idEmployee`)" +
                        "    ON DELETE NO ACTION" +
                        "    ON UPDATE NO ACTION," +
                        "  CONSTRAINT `idInfrastructure`" +
                        "    FOREIGN KEY (`idInfrastructure`)" +
                        "    REFERENCES `Infrastructure` (`idInfrastructure`)" +
                        "    ON DELETE NO ACTION" +
                        "    ON UPDATE NO ACTION);";
        database.execSQL(setUpQuery);

//        database.execSQL("CREATE TABLE IF NOT EXISTS Infrastructure(Latitude NUMERIC(10,5)," +
//                " Longitude NUMERIC(10,5), Quality NUMERIC, Description VARCHAR, ImageFilePath VARCHAR);");
        resultSet = database.rawQuery("Select * from InfrastructureQualitys", null);
    }

    public boolean isDatabaseEmpty() {
        return !resultSet.moveToFirst();
    }

//    public void generateRandomEntry() {
//        Random generator = new Random();
//        double lat = generator.nextDouble() * .47 - 38;
//        double log = generator.nextDouble() * .93 + 144.5;
//        int quality = generator.nextInt(101);
//        String description = "Dummy Data";
//        String imageFilePath = "";
//
//        addEntry(lat, log, quality, description, imageFilePath);
//    }

    public void search(int tableID, Map<String, String> options){
        String tableName = getTableName(tableID);
        
        StringBuilder optionString = new StringBuilder();
        optionString.append("Select * FROM ").append(tableName).append(" Where ");
        int i = 0;
        for (Map.Entry<String, String> entry : options.entrySet())
        {
            optionString.append(entry.getKey());

            if(isInteger(entry.getKey())){
                optionString.append(" = ").append(entry.getValue());
            }else{
                optionString.append(" like '").append(entry.getValue()).append("'");
            }

            if(i++ != options.size() - 1){
                optionString.append(" OR ");
            }
        }
        optionString.append(";");

        Log.d("Query", String.valueOf(optionString));

        resultSet = database.rawQuery(String.valueOf(optionString), null);
    }

    private boolean isInteger(String key) {
        return key.equals("idEmployee") || key.equals("idReports") || key.equals("idInfrastructure")
                || key.equals("Latitude") || key.equals("Longitude");
    }

    private String getTableName(int tableID){
        if(tableID == EMPLOYEES){
            return "Employees";
        }else if(tableID == INFRASTRUCTURE){
            return  "Infrastructure";
        }else if(tableID == REPORTS){
            return "Reports";
        }else if(tableID == TYPES){
            return "Type";
        }else{
            return "";
        }
    }

    public void addEntry(int tableID, Map<String, String> options) {
        String tableName = getTableName(tableID);

        StringBuilder query = new StringBuilder();
        StringBuilder optionString = new StringBuilder();
        StringBuilder columns = new StringBuilder();
        query.append("INSERT INTO ").append(tableName);
        optionString.append(" VALUES(");
        columns.append("(");

        int i = 0;
        for (Map.Entry<String, String> entry : options.entrySet())
        {
            columns.append(entry.getKey());

            if(isInteger(entry.getKey())){
                optionString.append(entry.getValue());
            }else{
                optionString.append(" '").append(entry.getValue()).append("' ");
            }

            if(i++ != options.size() - 1){
                optionString.append(", ");
                columns.append(", ");
            }
        }
        columns.append(")");
        optionString.append(");");

        query.append(columns).append(optionString);

        Log.d("Insert Query", String.valueOf(query));

        database.execSQL(String.valueOf(query));
    }

    public void clearDatabase() {
        database.execSQL("delete from InfrastructureQualitys;");
        resultSet = database.rawQuery("Select * from InfrastructureQualitys", null);
    }

    public ArrayList<String> getTypesList(){
        ArrayList<String> options = new ArrayList<>();
        resultSet = database.rawQuery("Select * from Type", null);
        if (isDatabaseEmpty())
            return null;

        do{
            options.add(resultSet.getString(0));
        }while(hasNext());
        return options;
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

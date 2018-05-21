package s3542977.com.tqr;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class DatabaseHandler implements Serializable {
    public static final int EMPLOYEES = 0;
    public static final int INFRASTRUCTURE = 1;
    public static final int REPORTS = 2;
    public static final int TYPES = 3;

    private SQLiteDatabase database;
    private Cursor resultSet;

    DatabaseHandler(Context context) {
        database = context.openOrCreateDatabase("database", MODE_PRIVATE, null);
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
                        "  `Quality` INTEGER," +
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
                        "  `Speed` DOUBLE," +
                        "  `Description` VARCHAR(1000)," +
                        "  `ImageFilePath` VARCHAR(100)," +
                        "  `DateTime` DATETIME," +
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
    }

    private boolean isResultEmpty() {
        return !resultSet.moveToFirst();
    }

    public void search(int tableID, Map<String, String> options) {
        String tableName = getTableName(tableID);

        StringBuilder optionString = new StringBuilder();

        if (options == null || options.isEmpty()) {
            optionString.append("Select * FROM ").append(tableName).append(";");
            resultSet = database.rawQuery(String.valueOf(optionString), null);
            return;
        }

        optionString.append("Select * FROM ").append(tableName);

        if (tableID == REPORTS) {
            optionString.append(" NATURAL JOIN Employees ");
        }

        optionString.append(" Where ");

        int i = 0;
        for (Map.Entry<String, String> entry : options.entrySet()) {
            optionString.append(entry.getKey());

            if (isInteger(entry.getKey())) {
                optionString.append(" = ").append(entry.getValue());
            } else {
                optionString.append(" like '%").append(entry.getValue()).append("%'");
            }

            if (i++ != options.size() - 1) {
                optionString.append(" OR ");
            }
        }

//        if (tableID == REPORTS) {
//            optionString.append(" AND idEmployee = ").append(options.get("idEmployee"));
//        }

        optionString.append(";");

        Log.d("Query", String.valueOf(optionString));

        resultSet = database.rawQuery(String.valueOf(optionString), null);
    }

    public void searchLatLngInRange(double maxLatitude, double maxLongitude, double minLatitude, double minLongitude) {
        StringBuilder query = new StringBuilder();

        query.append("SELECT * FROM Infrastructure WHERE (Latitude BETWEEN ")
                .append(minLatitude).append(" AND ").append(maxLatitude).append(") AND (Longitude BETWEEN ")
                .append(minLongitude).append(" AND ").append(maxLongitude).append(");");

        Log.d("Nearby Query", String.valueOf(query));

        resultSet = database.rawQuery(String.valueOf(query), null);
    }

    private boolean isInteger(String key) {
        return key.equals("idEmployee") || key.equals("idReports") || key.equals("idInfrastructure")
                || key.equals("Latitude") || key.equals("Longitude");
    }

    private String getTableName(int tableID) {
        if (tableID == EMPLOYEES) {
            return "Employees";
        } else if (tableID == INFRASTRUCTURE) {
            return "Infrastructure";
        } else if (tableID == REPORTS) {
            return "Reports";
        } else if (tableID == TYPES) {
            return "Type";
        } else {
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
        for (Map.Entry<String, String> entry : options.entrySet()) {
            columns.append(entry.getKey());

            if (isInteger(entry.getKey())) {
                optionString.append(entry.getValue());
            } else {
                optionString.append(" '").append(entry.getValue()).append("' ");
            }

            if (i++ != options.size() - 1) {
                optionString.append(", ");
                columns.append(", ");
            }
        }
        if (tableID == REPORTS) {
            columns.append(", DateTime");
            optionString.append(", DateTime('now')");
        }

        columns.append(")");
        optionString.append(");");

        query.append(columns).append(optionString);

        Log.d("Insert Query", String.valueOf(query));

        database.execSQL(String.valueOf(query));

        if (tableID == REPORTS) {
            updateInfrastructureQuality(options);
        }
    }

    private void updateInfrastructureQuality(Map<String, String> options) {
        String newQuality = options.get("Quality");
        String id = options.get("idInfrastructure");

        String query = "UPDATE Infrastructure SET Quality = " + newQuality + " WHERE idInfrastructure = " + id + ";";
        Log.d("Update query", query);

        database.execSQL(query);
    }

    public ArrayList<Map<String, String>> getResult(int tableID) {
        ArrayList<Map<String, String>> result = new ArrayList<>();

        if (isResultEmpty())
            return result;

        do {
            Map<String, String> resultMap = new HashMap<>();
            if (tableID == EMPLOYEES) {
                resultMap.put("Employee ID", String.valueOf(resultSet.getInt(0)));
                resultMap.put("Name", String.valueOf(resultSet.getString(1)));
                resultMap.put("Phone", String.valueOf(resultSet.getString(2)));
            } else if (tableID == INFRASTRUCTURE) {
                resultMap.put("Infrastructure ID", String.valueOf(resultSet.getInt(0)));
                resultMap.put("Type", String.valueOf(resultSet.getString(1)));
                resultMap.put("Latitude", String.valueOf(resultSet.getDouble(2)));
                resultMap.put("Longitude", String.valueOf(resultSet.getDouble(3)));
                resultMap.put("Quality", String.valueOf(resultSet.getInt(4)));
            } else if (tableID == REPORTS) {
                resultMap.put("Report ID", String.valueOf(resultSet.getInt(0)));
                resultMap.put("Employee ID", String.valueOf(resultSet.getInt(1)));
                resultMap.put("Infrastructure ID", String.valueOf(resultSet.getInt(2)));
                resultMap.put("Quality", String.valueOf(resultSet.getInt(3)));
                resultMap.put("InterferenceLevel", String.valueOf(resultSet.getString(4)));
                resultMap.put("Speed", String.valueOf(resultSet.getInt(5)));
                resultMap.put("Description", String.valueOf(resultSet.getString(6)));
                resultMap.put("DateTime", String.valueOf(resultSet.getString(8)));
                resultMap.put("Employee Name", String.valueOf(resultSet.getString(9)));
                resultMap.put("Phone", String.valueOf(resultSet.getString(10)));
            } else if (tableID == TYPES) {
                resultMap.put("Type", String.valueOf(resultSet.getString(0)));
            }
            result.add(resultMap);
        } while (hasNext());

        return result;
    }

    public ArrayList<Integer> getResultIdsList(int tableID) {
        ArrayList<Integer> result = new ArrayList<>();

        if (isResultEmpty())
            return result;

        do {
            if (tableID == EMPLOYEES || tableID == INFRASTRUCTURE || tableID == REPORTS) {
                result.add(resultSet.getInt(0));
            }
        } while (hasNext());
        return result;
    }

    public ArrayList<String> getResultImageFilePaths(int tableID) {
        ArrayList<String> result = new ArrayList<>();

        if (isResultEmpty())
            return result;

        do {
            if (tableID == REPORTS) {
                result.add(resultSet.getString(7));
            }
        } while (hasNext());
        return result;
    }

    public void clearTable(int tableID) {
        String table = getTableName(tableID);

        database.execSQL("delete from " + table + ";");
    }

    public Boolean removeEntry(int tableID, String id) {
        String table = getTableName(tableID);
        String idName;
        if (tableID == EMPLOYEES) {
            idName = "idEmployee";
        } else if (tableID == INFRASTRUCTURE) {
            idName = "idInfrastructure";
        } else if (tableID == REPORTS) {
            idName = "idReports";
        } else if (tableID == TYPES) {
            idName = "idType";
            return database.delete(table, idName + " LIKE '" + id + "'", null) > 0;
        } else {
            return false;
        }
        return database.delete(table, idName + "=" + id, null) > 0;
    }

    public void generateRandomEntries(int tableID, int amount) {
        if (tableID == REPORTS) {
            Random generator = new Random();
            for (int i = 0; i < amount; i++) {
                int quality = generator.nextInt(101);
                int interferenceLevel = generator.nextInt(101);
                double speed = 100 * generator.nextDouble();
                int numEmployees = (int) DatabaseUtils.queryNumEntries(database, "Employees");
                int numInfrastructure = (int) DatabaseUtils.queryNumEntries(database, "Infrastructure");
                int idEmployee = generator.nextInt(numEmployees + 1) + 1;
                int idInfrastructure = generator.nextInt(numInfrastructure + 1) + 1;

                Map<String, String> options = new HashMap<>();
                options.put("idEmployee", String.valueOf(idEmployee));
                options.put("idInfrastructure", String.valueOf(idInfrastructure));
                options.put("Quality", String.valueOf(quality));
                options.put("InterferenceLevel", String.valueOf(interferenceLevel));
                options.put("Speed", String.valueOf(speed));
                options.put("Description", "Dummy Data");
                options.put("ImageFilePath", "");

                addEntry(tableID, options);
            }
        } else if (tableID == INFRASTRUCTURE) {
            Random generator = new Random();
            for (int i = 0; i < amount; i++) {
                double lat = generator.nextDouble() * .47 - 38;
                double log = generator.nextDouble() * .93 + 144.5;
                int quality = generator.nextInt(101);

                resultSet = database.rawQuery("SELECT * FROM Type ORDER BY RANDOM() LIMIT 1;", null);
                resultSet.moveToFirst();
                if (resultSet.getCount() == 0) {
                    return;
                }
                String type = resultSet.getString(0);

                Map<String, String> options = new HashMap<>();
                options.put("idType", type);
                options.put("Latitude", String.valueOf(lat));
                options.put("Longitude", String.valueOf(log));
                options.put("Quality", String.valueOf(quality));

                addEntry(tableID, options);
            }

        } else if (tableID == EMPLOYEES) {
            Random generator = new Random();
            ArrayList<String> names = new ArrayList<>();
            names.add("Troy Olyvia");
            names.add("Shannon Miller");
            names.add("Maxene Sydnie");
            names.add("Michael Johnson");
            names.add("James Brown");
            names.add("Shannon Taylor");
            names.add("Brooke Jones");
            names.add("Taylor Miller");
            names.add("Emma Blaese");
            names.add("Savannah Johnson");
            names.add("Darren Davis");
            names.add("Dale Williams");
            names.add("Dakota Jones");
            names.add("Brooke Wilson");

            for (int i = 0; i < amount; i++) {
                int nameIndex = generator.nextInt(names.size());

                int phoneNumber = (int) (10000000 + generator.nextFloat() * 90000000);

                Map<String, String> options = new HashMap<>();
                options.put("Name", names.get(nameIndex));
                options.put("Phone", String.valueOf(phoneNumber));

                addEntry(tableID, options);
            }
        }
    }

    public ArrayList<Map<String, String>> getLastRow(int tableID) {
        String tableName = getTableName(tableID);

        StringBuilder query = new StringBuilder();
        String idName;

        switch (tableID) {
            case EMPLOYEES:
                idName = "idEmployee";
                break;
            case INFRASTRUCTURE:
                idName = "idInfrastructure";
                break;
            case REPORTS:
                idName = "idReports";
                break;
            default:
                return new ArrayList<>();
        }

        query.append("SELECT * FROM '").append(tableName).append("' WHERE ")
                .append(idName).append(" = (SELECT MAX(").append(idName).append(") FROM '")
                .append(tableName).append("');");

        Log.d("Last Row Query", String.valueOf(query));

        resultSet = database.rawQuery(String.valueOf(query), null);

        return getResult(tableID);
    }

    public ArrayList<String> getTypesList() {
        ArrayList<String> options = new ArrayList<>();
        resultSet = database.rawQuery("Select * from Type", null);
        if (isResultEmpty())
            return options;

        do {
            options.add(resultSet.getString(0));
        } while (hasNext());
        return options;
    }

    private boolean hasNext() {
        if (resultSet.isLast()) {
            resultSet.moveToFirst();
            return false;
        } else {
            resultSet.moveToNext();
            return true;
        }
    }

    public void findInfrastructureReports(LatLng position) {
        StringBuilder optionString = new StringBuilder();

        String tableName = getTableName(INFRASTRUCTURE);
        optionString.append("Select idInfrastructure FROM ").append(tableName).append(" Where Latitude = ")
                .append(position.latitude).append(" AND Longitude = ").append(position.longitude).append(";");

        resultSet = database.rawQuery(String.valueOf(optionString), null);

        resultSet.moveToFirst();
        Map<String, String> options = new HashMap<>();
        options.put("idInfrastructure", String.valueOf(resultSet.getInt(0)));

        search(REPORTS, options);
    }
}

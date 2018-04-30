package s3542977.com.tqr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class DatabaseViewer extends AppCompatActivity {
    String text;
    Bitmap imageBitmap;
    String imagePath;
    TextView textView;
    ImageView imageView;
    DatabaseHandler databaseHandler;
    Button nextRowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_viewer);
        textView = findViewById(R.id.dbText);
        nextRowButton = findViewById(R.id.nextRowButton);
        imageView = findViewById(R.id.dbImage);
        databaseHandler = new DatabaseHandler(this);

        if (databaseHandler.isDatabaseEmpty()){
            setStateToEmptyDatabase();
        }else{
            setText();
            setImage();
        }
    }

    public void clearDB(View view) {
        databaseHandler.clearDatabase();
        setStateToEmptyDatabase();
    }

    private void setStateToEmptyDatabase(){
        nextRowButton.setEnabled(false);

        text = "Database is empty";
        textView.setText(text);
    }

    private void setText() {
        text = "latitude: " + databaseHandler.getLatitudeAsString() +
                "\nlongitude " + databaseHandler.getLongitudeAsString() +
                "\nquality " + databaseHandler.getQualityAsString() +
                "\ndescription " + databaseHandler.getDescription();
        nextRowButton.setEnabled(true);

        textView.setText(text);
    }

    public void nextRow(View view) {
        databaseHandler.hasNext();
        setText();
        setImage();
    }

    private void setImage() {
        imagePath = databaseHandler.getImageFIlePath();

        if(new File(imagePath).exists()) {
            imageBitmap = BitmapFactory.decodeFile(imagePath);
            imageBitmap = Bitmap.createBitmap(imageBitmap);
            imageView.setImageBitmap(imageBitmap);
        }else{
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_camera, this.getTheme()));
        }
    }

    public void addRandomData(View view){
        for(int i = 0; i < 10; i++){
            databaseHandler.generateRandomEntry();
        }
        databaseHandler.isDatabaseEmpty();
        setText();
    }
}

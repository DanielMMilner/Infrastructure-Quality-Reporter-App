package s3542977.com.tqr;

import android.content.Intent;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_viewer);
    }

    public void buttonPressed(View view){
        Intent intent;
        switch (view.getId()){
            case R.id.addButton:
                intent = new Intent(this, AddToDatabaseActivity.class);
                startActivity(intent);
                break;
            case R.id.removeButton:
                intent = new Intent(this, RemoveFromDatabaseActivity.class);
                startActivity(intent);
                break;
            case R.id.searchButton:
                intent = new Intent(this, SearchDatabaseActivity.class);
                startActivity(intent);
                break;
            case R.id.debugButton:
//                intent = new Intent(this, DebugDatabaseActivity.class);
                intent = new Intent(this, DatabaseResultActivity.class);

                startActivity(intent);
                break;
        }
    }
}

package s3542977.com.tqr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class DatabaseReportsResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_reports_results);

        ArrayList<String> resultList = getIntent().getStringArrayListExtra("resultList");
        ArrayList<String> resultImageFilePaths = getIntent().getStringArrayListExtra("resultImageFilePaths");

        if (resultList == null || resultList.isEmpty()) {
            Log.d("Result", "resultList is empty");
            return;
        }

        if (resultImageFilePaths == null || resultImageFilePaths.isEmpty()) {
            Log.d("Result", "resultImageFilePaths is empty");
            return;
        }

        LinearLayout linearLayout = findViewById(R.id.layout);

        int index = 0;
        for (String result : resultList) {
            index++;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView textView = new TextView(this);

            textView.setText(result);
            textView.setTextSize(18);
            ImageView imageView = new ImageView(this);

            String filePath = resultImageFilePaths.get(index - 1);
            if (!filePath.isEmpty()) {
                if (new File(filePath).exists()) {
                    Bitmap imageBitmap = BitmapFactory.decodeFile(filePath);
                    imageBitmap = Bitmap.createBitmap(imageBitmap);
                    imageView.setImageBitmap(imageBitmap);
                    imageView.setRotation(90);
                    imageView.setPadding(0, 250, 0, 150);
                    imageView.setAdjustViewBounds(true);
                }
            }
            linearLayout.addView(textView, params);
            linearLayout.addView(imageView, params);

            View view = new View(this);
            view.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    30));
            view.setBackgroundColor(Color.BLACK);
            linearLayout.addView(view);
        }
    }
}



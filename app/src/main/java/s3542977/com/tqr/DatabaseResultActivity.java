package s3542977.com.tqr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Map;

public class DatabaseResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_result);

        ArrayList<String> resultList = getIntent().getStringArrayListExtra("resultList");
        int tableId = getIntent().getIntExtra("tableId", -1);

        if (resultList == null || resultList.isEmpty()) {
            Log.d("Result", "Its empty");
            return;
        }

        LinearLayout linearLayout = findViewById(R.id.layout);

        int i = 0;

        for (String result :resultList) {
            i++;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            Button button = new Button(this);
            button.setId(i);
            final int id_ = button.getId();

            button.setText(result);
            button.setTextSize(15);

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Log.d("Button", String.valueOf(id_));
                }
            });

            linearLayout.addView(button, params);
        }
    }
}

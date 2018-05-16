package s3542977.com.tqr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class DatabaseResultActivity extends AppCompatActivity {
    private Intent resultIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_result);
        resultIntent = new Intent();

        ArrayList<String> resultList = getIntent().getStringArrayListExtra("resultList");

        ArrayList<Integer> resultIdList = getIntent().getIntegerArrayListExtra("resultIdList");


        if (resultList == null || resultList.isEmpty()) {
            Log.d("Result", "Its empty");
            return;
        }

        LinearLayout linearLayout = findViewById(R.id.layout);

        int index = 0;

        for (String result : resultList) {
            index++;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            Button button = new Button(this);
            if (resultIdList.isEmpty()) {
                button.setId(index);
            } else {
                button.setId(resultIdList.get(index - 1));
            }
            final int id_ = button.getId();

            button.setText(result);
            button.setTextSize(15);

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Button button1 = findViewById(view.getId());
                    Log.d("Button", String.valueOf(id_));
                    resultIntent.putExtra("chosenResult", button1.getText());
                    resultIntent.putExtra("idResult", id_);

                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            });

            linearLayout.addView(button, params);
        }
    }
}

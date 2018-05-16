package s3542977.com.tqr;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

public class SpeedTestActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView textView;
    private ProgressBar speedTestProgress;
    private Spinner spinner;
    private double speed;
    int spinnerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_test);
        textView = findViewById(R.id.speedTestStatus);
        speedTestProgress = findViewById(R.id.speedTestProgress);
        spinner = findViewById(R.id.testSizeSpinner);
        spinnerPosition = 0;

        //https://developer.android.com/guide/topics/ui/controls/spinner

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.speed_test_sizes_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }


    public void runTest(View view) {
        String file_url;

        file_url = (String) spinner.getItemAtPosition(spinnerPosition);

        switch (spinnerPosition) {
            case 0:
                file_url = "http://ftp.iinet.net.au/pub/speed/SpeedTest_1MB";
                break;
            case 1:
                file_url = "http://ftp.iinet.net.au/pub/speed/SpeedTest_16MB";
                break;
            case 2:
                file_url = "http://ftp.iinet.net.au/pub/speed/SpeedTest_32MB";
                break;
            case 3:
                file_url = "http://ftp.iinet.net.au/pub/speed/SpeedTest_64MB";
                break;
            case 4:
                file_url = "http://ftp.iinet.net.au/pub/speed/SpeedTest_128MB";
                break;
            case 5:
                file_url = "http://ftp.iinet.net.au/pub/speed/SpeedTest_256MB";
                break;
            case 6:
                file_url = "http://ftp.iinet.net.au/pub/speed/SpeedTest_512MB";
                break;
            case 7:
                file_url = "http://ftp.iinet.net.au/pub/speed/SpeedTest_1024MB";
                break;
            case 8:
                file_url = "http://ftp.iinet.net.au/pub/speed/SpeedTest_2048MB";
                break;
            case 9:
                file_url = "http://ftp.iinet.net.au/pub/speed/SpeedTest_4096MB";
                break;
        }

        new SpeedTestDownload().execute(file_url);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerPosition = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class SpeedTestDownload extends AsyncTask<String, String, String> {
        private long startTime;
        private long endTime;
        private long total = 0;
        private String temp = "";
        int progress = 0;
        private double seconds;
        private double bits;

        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                byte data[] = new byte[131072];
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lengthOfFile = connection.getContentLength();
                BufferedInputStream input = new BufferedInputStream(connection.getInputStream());

                long updateTimerStart = System.currentTimeMillis();
                long updateTimerEnd;

                startTime = System.currentTimeMillis();
                while ((count = input.read(data)) != -1) {
                    total += count;
                    progress = (int) ((total * 100) / lengthOfFile);
                    updateTimerEnd = System.currentTimeMillis();
                    if (updateTimerEnd - updateTimerStart > 50) {
                        //update the progress every 50ms
                        updateTimerStart = updateTimerEnd;
                        publishProgress();
                    }
                }
                publishProgress();
                input.close();
                returnResultIfRequired();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        private void returnResultIfRequired() {
            if(getIntent().getBooleanExtra("returnData", false)){
                Intent resultIntent = new Intent();

                resultIntent.putExtra("speedResult", speed);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... prog) {
            speedTestProgress.setProgress(progress);
            setText();
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            setText();
        }

        private void setText() {
            endTime = System.currentTimeMillis();
            seconds = (endTime - startTime) / 1000.0;
            bits = total * 8;
            speed = bits / seconds / 1000000;

            temp = "Downloaded " + total + " bytes \nTime: " +
                    String.valueOf(endTime - startTime) + "ms\nSpeed: " + String.format(Locale.UK, "%.2f", speed) + "Mbps";
            textView.setText(temp);
        }
    }
}

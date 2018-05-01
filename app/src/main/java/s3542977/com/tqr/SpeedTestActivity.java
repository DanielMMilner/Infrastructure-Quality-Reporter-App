package s3542977.com.tqr;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

public class SpeedTestActivity extends AppCompatActivity {
    private TextView textView;
    private ProgressBar speedTestProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_test);
        textView = findViewById(R.id.speedTestStatus);
        speedTestProgress = findViewById(R.id.speedTestProgress);
    }

    public void runTest(View view) {
        String file_url = "";

        switch (view.getId()) {
            case (R.id.test50MB):
                file_url = "http://ftp.iinet.net.au/test50MB.dat";
                break;
            case (R.id.test100MB):
                file_url = "http://ftp.iinet.net.au/test100MB.dat";
                break;
            case (R.id.test500MB):
                file_url = "http://ftp.iinet.net.au/test500MB.dat";
                break;
        }
        new SpeedTestDownload().execute(file_url);
    }

    public class SpeedTestDownload extends AsyncTask<String, String, String> {
        private long startTime;
        private long endTime;
        private long total = 0;
        private String temp = "";
        int progress = 0;
        private double seconds;
        private double bits;
        private double speed;

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
            int progressUpdateCounter = 0;
            try {
                byte data[] = new byte[131072];
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lengthOfFile = connection.getContentLength();
                BufferedInputStream input = new BufferedInputStream(connection.getInputStream());

                startTime = System.currentTimeMillis();
                while ((count = input.read(data)) != -1) {
                    total += count;
                    progress = (int) ((total * 100) / lengthOfFile);
                    progressUpdateCounter++;
                    if(progressUpdateCounter >= 50){
                        publishProgress();
                        progressUpdateCounter = 0;
                    }
                }
                System.out.println(progressUpdateCounter);
                endTime = System.currentTimeMillis();

                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
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
            seconds = (endTime - startTime)/1000.0;
            bits = total*8;
            speed = bits / seconds /1000000;

            temp = "Downloaded " + total + " bytes in " +
                    String.valueOf(endTime - startTime) + "ms\n Speed: " + String.format(Locale.UK,"%.2f", speed) + "Mbps";
            textView.setText(temp);
        }
    }
}

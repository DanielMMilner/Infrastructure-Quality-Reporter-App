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
        private byte data[] = new byte[50000000];
        private long total = 0;
        private String temp = "";

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
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();

                int lengthOfFile = connection.getContentLength();

                startTime = System.currentTimeMillis();
                InputStream input = new BufferedInputStream(url.openStream(), 50000000);

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                }
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
        protected void onProgressUpdate(String... progress) {
            speedTestProgress.setProgress(Integer.parseInt(progress[0]));
            endTime = System.currentTimeMillis();
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
            temp = "Downloaded " + total + " bytes in " + String.valueOf(endTime - startTime) + "ms";
            textView.setText(temp);
        }
    }
}

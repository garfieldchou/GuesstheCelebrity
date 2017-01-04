package com.garfield_chou.guessthecelebrity;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

	public class DownloadWebTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground (String... urls) {

			String result = "";
			URL url;
			HttpURLConnection connection = null;

			try {
				url = new URL(urls[0]);
				connection = (HttpURLConnection) url.openConnection();
				InputStream inputStream = connection.getInputStream();
				InputStreamReader reader = new InputStreamReader(inputStream);

				int data = reader.read();

				while (-1 != data) {
					char current = (char)data;
					result += current;
					data = reader.read();
				}
				return result;
			}
			catch (Exception e) {
				e.printStackTrace();
				return "Failed to get web content";
			}
		}
	}

    public void chooseName (View view) {

        Log.i("button tapped", view.getTag().toString());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String result = null;
        DownloadWebTask task = new DownloadWebTask();

        try {
        	result = task.execute("http://www.posh24.com/celebrities").get();
        }
        catch (InterruptedException e) {
        	e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }

        Log.i("Contents of the web", result);
    }
}

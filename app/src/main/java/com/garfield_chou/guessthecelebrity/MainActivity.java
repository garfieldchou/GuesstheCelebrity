package com.garfield_chou.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> photoSources = new ArrayList<String>();
    ArrayList<String> photoNames = new ArrayList<String>();
    ImageView imageView;

    public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    	@Override
    	protected Bitmap doInBackground (String... urls) {

    		URL url = null;
    		Bitmap bitmap = null;

    		try {
    			url = new URL(urls[0]);
    			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    			InputStream inputStream = connection.getInputStream();
    			bitmap = BitmapFactory.decodeStream(inputStream);
    		}
            catch (Exception e) {
            	e.printStackTrace();
            }
            return bitmap;
    	}
    }

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

    	DownloadImageTask task = new DownloadImageTask();
    	Bitmap bitmap = null;

    	try {
    		bitmap = task.execute(photoSources.get(94)).get();
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}
    	imageView.setImageBitmap(bitmap);
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

        Pattern p = Pattern.compile("src=\"(http://cdn.posh24.com/images/:profile.*?)\"");
        Matcher m = p.matcher(result);

        while (m.find()) {
            photoSources.add(m.group(1));
        }
        /*
        for (String photoSource : photoSources) {
        	Log.i("photoSrc", photoSource);
        }
        */

        p = Pattern.compile("alt=\"(.*?)\"");
        m = p.matcher(result);

        while (m.find()) {
            photoNames.add(m.group(1));
        }
        /*
        for (String photoName : photoNames) {
        	Log.i("photoName", photoName);
        } 
        */
        imageView = (ImageView) findViewById(R.id.imageView);
    }
}

package com.garfield_chou.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InterfaceAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> photoSources = new ArrayList<String>();
    ArrayList<String> photoNames = new ArrayList<String>();
    ImageView imageView;
	int correctButtonIdx, correctAnswerIdx;

    public void generateQuestion () {

		Random rand = new Random();
		correctAnswerIdx = rand.nextInt(100);
		correctButtonIdx = rand.nextInt(4);

		DownloadImageTask task = new DownloadImageTask();
		Bitmap bitmap = null;

		try {
			bitmap = task.execute(photoSources.get(correctAnswerIdx)).get();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		imageView.setImageBitmap(bitmap);

		int correctButtonId = getResources().getIdentifier("nameButton" + correctButtonIdx, "id", getPackageName());
		Button correctButton = (Button)findViewById(correctButtonId);
		correctButton.setText(photoNames.get(correctAnswerIdx));

		for (int i = 0; i < 4; i++) {
			if (correctButtonIdx == i) continue;
			int wrongButtonId = getResources().getIdentifier("nameButton" + i, "id", getPackageName());
			Button wrongButton = (Button)findViewById(wrongButtonId);

			int wrongAnswerIdx;
			do {

				wrongAnswerIdx = rand.nextInt(100);

			} while (correctAnswerIdx == wrongAnswerIdx);
			wrongButton.setText(photoNames.get(wrongAnswerIdx));
		}

	}

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

		Log.i("button tapped", view.getTag().toString());
		if (view.getTag().toString().equals(Integer.toString(correctButtonIdx))) {
			Toast.makeText(MainActivity.this, "Correct!", Toast.LENGTH_LONG).show();
		}
		else {
			Toast.makeText(MainActivity.this, "Wrong! it was " + photoNames.get(correctAnswerIdx), Toast.LENGTH_LONG).show();
		}
		generateQuestion();

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
		generateQuestion();
    }
}

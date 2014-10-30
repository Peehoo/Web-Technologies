package com.example.myfirstapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

public class StockChartAsyncTask extends AsyncTask<String, Void, Drawable>{

	@Override
	protected Drawable doInBackground(String... params) {
		URL url;
		try {
			url = new URL(params[0]);
			URLConnection conn = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			Drawable image =ImageOperations(MainActivity.mainActivity,params[0]);
			return image;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Object fetch(String address) throws MalformedURLException,
    IOException {
        URL url = new URL(address);
        Object content = url.getContent();
        return content;
    }  

    private Drawable ImageOperations(Context ctx, String url) {
        try {
            InputStream is = (InputStream) this.fetch(url);
            Drawable d = Drawable.createFromStream(is, "src");
            return d;
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
	

}

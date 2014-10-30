package com.example.myfirstapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class StockInfoAsyncTask extends
		AsyncTask<String, Void, JSONObject> {

	@Override
	protected JSONObject doInBackground(String... params) {
		JSONObject result = null;
		try {

			URL url = new URL(
					"http://cs-server.usc.edu:13449/examples/servlet/MyServlet?symbol="
							+ params[0]);
			URLConnection conn = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			String line;
			StringBuffer sb = new StringBuffer();
			// take Yahoo's legible JSON and turn it into one big string.
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			String response = sb.toString();
			result = new JSONObject(response).getJSONObject("result");
			//TextView temp = (TextView) findViewById(R.id.companyName);
			//R.id.companyName = result.get("Name");
			
			Log.e("YourApp StockInfoAsyncTask : doInBackground", result.toString());
		
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		return result;
	}

}

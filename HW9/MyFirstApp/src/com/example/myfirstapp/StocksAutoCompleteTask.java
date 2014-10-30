package com.example.myfirstapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

class StocksAutoCompleteTask extends AsyncTask<String, Void, ArrayList<String>>
{
	ArrayAdapter<String> adapter;
	AutoCompleteTextView textView;
	MainActivity activity;
	
	public StocksAutoCompleteTask(ArrayAdapter<String> adapter, AutoCompleteTextView textView, MainActivity activity){
		this.adapter = adapter;
		this.textView = textView;
		this.activity = activity;
	}
	
	@Override
	// three dots is java for an array of strings
	protected ArrayList<String> doInBackground(String... args)
	{

		Log.d("gottaGo", "doInBackground");

		ArrayList<String> suggestionArray = new ArrayList<String>();

		try
		{
			URL yahooAPI = new URL(
					// URLEncoder.encode(url,"UTF-8");
					"http://autoc.finance.yahoo.com/autoc?query="+ URLEncoder.encode(args[0].toString(), "UTF-8") + "&callback=YAHOO.Finance.SymbolSuggest.ssCallback");
			URLConnection tc = yahooAPI.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					tc.getInputStream()));

			String line;
			StringBuffer sb = new StringBuffer();
			//take Yahoo's legible JSON and turn it into one big string.
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			String response = sb.toString();
			response = response.substring(response.indexOf('(')+1,response.length()-1);
			//turn that string into a JSON object
			JSONObject suggestions = new JSONObject(response);	
			//now get the JSON array that's inside that object 
			JSONObject resultSet = suggestions.getJSONObject("ResultSet");//.getJSONObject("Result");
			JSONArray ja = new JSONArray(resultSet.getString("Result"));

			for (int i = 0; i < ja.length(); i++) {
				JSONObject jo = (JSONObject) ja.get(i);
				//add each entry to our array
				suggestionArray.add(jo.getString("symbol") + ", " + jo.getString("name") + "(" + jo.getString("exch") + ")");
			}
		} catch (IOException e)
		{
			Log.e("YourApp", "StocksAutoCompleteTask : doInBackground", e);

		} catch (Exception e)
		{
			Log.e("YourApp", "StocksAutoCompleteTask : doInBackground", e);
		}
		return suggestionArray;
	}

	//then our post

	@Override
	protected void onPostExecute(ArrayList<String> result)
	{

		Log.d("YourApp", "onPostExecute : " + result.size());
		//update the adapter
		adapter = new ArrayAdapter<String>(activity, R.layout.list_item);
		adapter.setNotifyOnChange(true);
		//attach the adapter to textview
		textView.setAdapter(adapter);

		for (String string : result)
		{

			Log.d("YourApp", "onPostExecute : result = " + string);
			adapter.add(string);
			adapter.notifyDataSetChanged();

		}

		Log.d("YourApp", "onPostExecute : autoCompleteAdapter" + adapter.getCount());

	}
	
	

}

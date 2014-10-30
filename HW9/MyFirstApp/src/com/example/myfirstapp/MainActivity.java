package com.example.myfirstapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session.StatusCallback;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.FacebookDialog.ShareDialogBuilder;
import com.facebook.widget.WebDialog.OnCompleteListener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

private class SessionStatusCallback implements Session.StatusCallback {
    	
    	int i = 0;
    	
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        	Log.v("i = ", ""+i);
        	i++;
        	if(session.isOpened())
        	{
        		Log.v("tester", "opened");
        		//publishFeedDialog();
        	}
        	else
        	{
        		Log.v("tester", "not opened");
        	}
        }        
    }
	
	JSONObject resultInfo;
	JSONObject resultQuote;
	JSONObject resultNews;
	String errorInResult;
	public static MainActivity mainActivity;
	private UiLifecycleHelper uiHelper;
	final Context context = this;

	public MainActivity getThisObject(){
		return this;
	}
	public MainActivity getActivity(){
		return this;
	}
	
	
	// facebook code
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    @Override
    public void onStart() 
    {
        super.onStart();
        Session s  = Session.getActiveSession();
        s.addCallback(statusCallback);
    }

    @Override
    public void onStop() 
    {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 try {
	            PackageInfo info = getPackageManager().getPackageInfo(
	                    "com.example.myfirstapp", 
	                    PackageManager.GET_SIGNATURES);
	            for (Signature signature : info.signatures) {
	                MessageDigest md = MessageDigest.getInstance("SHA");
	                md.update(signature.toByteArray());
	                Log.v("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
	                }
	        } catch (NameNotFoundException e) {
	        	e.printStackTrace();

	        } catch (NoSuchAlgorithmException e) {

	        }
		 
		 
	        
		 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		
		
		uiHelper = new UiLifecycleHelper(this, new StatusCallback() {
			
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				//exception.printStackTrace();
				
			}
		});
	    uiHelper.onCreate(savedInstanceState);

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_item);
		final AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.edit_message);
		adapter.setNotifyOnChange(true);
		textView.setAdapter(adapter);
		textView.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
					adapter.clear();
					StocksAutoCompleteTask task = new StocksAutoCompleteTask(adapter, textView, getThisObject());
					//now pass the argument in the textview to the task
					task.execute(textView.getText().toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			public void afterTextChanged(Editable s) {

			}
		});
		
		// Setting the text of the autoCompleteBox to just symbol
		textView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long arg3) {
				String stock = (String) adapter.getItemAtPosition(position);
				displayStockData(textView, stock);
			}

			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void openFacebookTab(View view){
		ShareDialogBuilder builder = new FacebookDialog.ShareDialogBuilder(this);
		boolean b = builder.canPresent();
		
		if(!b){
			// facebook code
	        Session session = Session.getActiveSession();
	        
	        if (session == null) 
	        {
	        	session = new Session(this);
	            Session.setActiveSession(session);
	        }
	        
	        if (!session.isOpened() && !session.isClosed()) 
	        {
	        	Log.v("testing", "1");
	            session.openForRead(new Session.OpenRequest(getActivity()).setCallback(statusCallback));
	        }
	        String state = session.getState().toString();
	        Log.v("session status", state);
			publishFeedDialog();
		}
		else{
			FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
	        .setLink("https://developers.facebook.com/android")
	        .build();
			uiHelper.trackPendingDialogCall(shareDialog.present());
		}
	}
	

	
	/** Called when the user clicks the Search button */
	public void searchSymbol(View view) {
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.list_item);
		final AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.edit_message);
		String stock = textView.getText().toString();
		displayStockData(textView, stock);
	}

	public void openNewsTab(View view) {
		Intent intent = new Intent(this, DisplayNewsActivity.class);
		try {
			JSONArray newsItems = resultNews.getJSONArray("Item");
			intent.putExtra("Length", newsItems.length());
			for (int i=0; i<newsItems.length(); i++) {
				intent.putExtra("itemTitle" + i, newsItems.getJSONObject(i).getString("Title"));
				intent.putExtra("itemLink" + i, newsItems.getJSONObject(i).getString("Link"));
			}	
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		 startActivity(intent);
	}
	
	public void displayStockData(final AutoCompleteTextView textView, String stock) {

		String[] stockArr = stock.split(",");
		String symbol = stockArr[0];
		textView.setText(symbol);
		StockInfoAsyncTask task = new StockInfoAsyncTask();
		AsyncTask<String, Void, JSONObject> result = task.execute(symbol);
		try {
			resultInfo = result.get();
			try{
				errorInResult = resultInfo.getString("error");
			}catch(JSONException e){
				errorInResult = "No Error";
			}
			if(errorInResult.equals("Missing Symbol")){
				String message = "Please enter a stock symbol";
				String title = "Missing Symbol";
				showErrorMessage(message, title);
			}
			else if(errorInResult.equals("Symbol not found!")){
				String message = "Stock symbol not found";
				String title = "Illegal Symbol";
				showErrorMessage(message, title);
			}
			else{
				resultQuote = resultInfo.getJSONObject("Quote");
				resultNews = resultInfo.getJSONObject("News");
				
				TextView companyName = (TextView) findViewById(R.id.companyName);
				companyName.setText(resultInfo.getString("Name") + "(" + resultInfo.getString("Symbol") + ")");
				
				TextView lastTradePriceOnly = (TextView) findViewById(R.id.lastTradePriceOnly);
				lastTradePriceOnly.setText(resultQuote.getString("LastTradePriceOnly"));
				
				TextView change = (TextView) findViewById(R.id.change);
				ImageView image = (ImageView) findViewById(R.id.image);
			
				if(resultQuote.getString("ChangeType").equals("-")){
					image.setImageResource(R.drawable.red_down_arrow);
					change.setTextColor(0xffff0000);
					change.setText(resultQuote.getString("Change") + " (" + resultQuote.getString("ChangeInPercent") + ")");
				}
				else if(resultQuote.getString("ChangeType").equals("+")){
					image.setImageResource(R.drawable.g);
					change.setTextColor(0xff00ff00);
					change.setText(resultQuote.getString("Change") + " (" + resultQuote.getString("ChangeInPercent") + ")");
				}
				
				TextView prevClose = (TextView) findViewById(R.id.prevClose);
				prevClose.setText("Prev Close");
				
				TextView prevCloseValue = (TextView) findViewById(R.id.prevCloseValue);
				prevCloseValue.setText(resultQuote.getString("PreviousClose"));
				
				TextView open = (TextView) findViewById(R.id.open);
				open.setText("Open");
				
				TextView openValue = (TextView) findViewById(R.id.openValue);
				openValue.setText(resultQuote.getString("Open"));
				
				TextView bid = (TextView) findViewById(R.id.bid);
				bid.setText("Bid");
				
				TextView bidValue = (TextView) findViewById(R.id.bidValue);
				bidValue.setText(resultQuote.getString("Bid"));
				
				TextView ask = (TextView) findViewById(R.id.ask);
				ask.setText("Ask");
				
				TextView askValue = (TextView) findViewById(R.id.askValue);
				askValue.setText(resultQuote.getString("Ask"));
				
				TextView firstYrTarget = (TextView) findViewById(R.id.firstYrTarget);
				firstYrTarget.setText("1st Yr Target");
				
				TextView firstYrTargetValue = (TextView) findViewById(R.id.firstYrTargetValue);
				firstYrTargetValue.setText(resultQuote.getString("OneYearTargetPrice"));
				
				TextView daysRange = (TextView) findViewById(R.id.daysRange);
				daysRange.setText("Day's Range");
				
				TextView daysRangeValue = (TextView) findViewById(R.id.daysRangeValue);
				daysRangeValue.setText(resultQuote.getString("DaysLow") + " - " + resultQuote.getString("DaysHigh"));
				
				TextView yearRange = (TextView) findViewById(R.id.yearRange);
				yearRange.setText("52 wk Range");
				
				TextView yearRangeValue = (TextView) findViewById(R.id.yearRangeValue);
				yearRangeValue.setText(resultQuote.getString("YearLow") + " - " + resultQuote.getString("YearHigh"));
				
				TextView volume = (TextView) findViewById(R.id.volume);
				volume.setText("Volume");
				
				TextView volumeValue = (TextView) findViewById(R.id.volumeValue);
				volumeValue.setText(resultQuote.getString("Volume"));
				
				TextView avgVolume = (TextView) findViewById(R.id.avgVolume);
				avgVolume.setText("Avg Vol(3m)");
				
				TextView avgVolumeValue = (TextView) findViewById(R.id.avgVolumeValue);
				avgVolumeValue.setText(resultQuote.getString("AverageDailyVolume"));
				
				TextView marketCap = (TextView) findViewById(R.id.marketCap);
				marketCap.setText("Market Cap");
				
				TextView marketCapValue = (TextView) findViewById(R.id.marketCapValue);
				marketCapValue.setText(resultQuote.getString("MarketCapitalization"));
				
				ImageView stockChart = (ImageView) findViewById(R.id.stockChart);
				String url = resultInfo.getString("StockChartImageURL");
				mainActivity = this;
				
				try{   
			        StockChartAsyncTask chartTask = new StockChartAsyncTask();
			        AsyncTask<String, Void, Drawable> stockChartImage = chartTask.execute(url);
			        stockChart.setImageDrawable(stockChartImage.get());
			    }
			    catch(Exception ex){
			        ex.printStackTrace();
			    }
				
				Button button_news = (Button) findViewById(R.id.button_news);
				button_news.setText("News Headlines");
				button_news.setVisibility(0);
				
				Button button_facebook = (Button) findViewById(R.id.button_facebook);
				button_facebook.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Session session= new Session(getApplicationContext());
						Session.setActiveSession(session);
						session.openForRead(new Session.OpenRequest(getActivity()));
						//().equals(Session))
						publishFeedDialog();
					}
				});
				button_facebook.setText("Facebook");
				button_facebook.setVisibility(0);
			}	
		} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
	public void showErrorMessage(String message, String title) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
 
			// set title
			alertDialogBuilder.setTitle(title);
 
			// set dialog message
			alertDialogBuilder
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, close
						// current activity
						dialog.cancel();
					}
				  });
			
				
 
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
 
				// show it
				alertDialog.show();
	}	
	
	
	@Override
	protected void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	private void publishFeedDialog() {
	    Bundle params = new Bundle();
	    try {
			params.putString("name", resultInfo.getString("Name"));
			params.putString("caption", "");
			 params.putString("description", "Last Trade Price: " + resultQuote.getString("LastTradePriceOnly") + ", Change: " + resultQuote.getString("Change") +
					 "(" + resultQuote.getString("ChangeInPercent") + ")");
			 params.putString("link", "http://finance.yahoo.com/q;_ylt=AndfgAteJ1llIdqpPcC7ItWiuYdG;_ylu=X3oDMTBxdGVyNzJxBHNlYwNVSCAzIERlc2t0b3AgU2VhcmNoIDEx;_ylg=X3oDMTByaDM4cG9kBGxhbmcDZW4tVVMEcHQDMgR0ZXN0AzUxMjAxMw--;_ylv=3?uhb=uhb2&fr=uh3_finance_vert_gs&type=2button&s="+resultInfo.getString("Symbol") +"%2C");
			    params.putString("picture", resultInfo.getString("StockChartImageURL") );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
	   
	    

	    WebDialog feedDialog = (
	        new WebDialog.FeedDialogBuilder(getActivity(),
	            Session.getActiveSession(),
	            params))
	        .setOnCompleteListener(new OnCompleteListener() {

	            @Override
	            public void onComplete(Bundle values, FacebookException error) {
	                if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(getActivity(),
	                            "Posted story, id: "+postId,
	                            Toast.LENGTH_SHORT).show();
	                    } else {
	                        // User clicked the Cancel button
	                        Toast.makeText(getActivity().getApplicationContext(), 
	                            "Publish cancelled", 
	                            Toast.LENGTH_SHORT).show();
	                    }
	                } else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(getActivity().getApplicationContext(), 
	                        "Publish cancelled", 
	                        Toast.LENGTH_SHORT).show();
	                } else {
	                    // Generic, ex: network error
	                    Toast.makeText(getActivity().getApplicationContext(), 
	                        "Error posting story", 
	                        Toast.LENGTH_SHORT).show();
	                }
	            }


	        })
	        .build();
	    feedDialog.show();
	}
	

}

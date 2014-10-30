package com.example.myfirstapp;

import java.util.ArrayList;
import java.util.HashMap;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Build;

public class DisplayNewsActivity extends ListActivity {

	 //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<Item> listItems=new ArrayList<Item>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<Item> adapter;
	
    Item item;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_display_news);

		// Get the message from the intent
	    Intent intent = getIntent();
	    int length = intent.getIntExtra("Length", 5);
	    
	    
	    adapter=new ArrayAdapter<Item>(this,
	            android.R.layout.simple_list_item_1,
	            listItems);
	        setListAdapter(adapter);
	    
	   // ListView newsList = new ListView(this);
	    for(int i=0; i<length; i++){
	    	String title = intent.getStringExtra("itemTitle" + i);
		    String link = intent.getStringExtra("itemLink"+ i);
	    	Item newsItem = new Item(title, link);
		    listItems.add(newsItem);
	    }
	    adapter.notifyDataSetChanged();
	    
	  /**  this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	            String url = adapter.getItem(position).getLink();
	            Intent i = new Intent(Intent.ACTION_VIEW);
	            i.setData(Uri.parse(url));
	            startActivity(i);
	        }
	    }); */
	    registerForContextMenu(this.getListView());
	}
	    
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo aInfo = (AdapterContextMenuInfo) menuInfo;
		// We know that each row in the adapter is a Map

		item =  adapter.getItem(aInfo.position);
		menu.setHeaderTitle("View News");
		menu.add(1, 1, 1, "View");	
		menu.add(1, 2, 2, "Cancel");
	}
  
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    int itemId = item.getItemId();
	    // Implements our logic
	   if(itemId==1){
		    String url = this.item.getLink();
	        Intent i = new Intent(Intent.ACTION_VIEW);
	        i.setData(Uri.parse(url));
	        startActivity(i);
		    return true;
	   }
	   else 
		   return false;
	}  
	    
		
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_news, menu);
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_display_news,
					container, false);
			return rootView;
		}
	}

}

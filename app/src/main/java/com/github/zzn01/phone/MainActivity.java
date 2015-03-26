package com.github.zzn01.phone;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;

public class MainActivity extends TabActivity {

    private TabHost tabHost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        tabHost = (TabHost) findViewById(android.R.id.tabhost);

        TabHost.TabSpec favorite = tabHost.newTabSpec("Favorite");
        favorite.setIndicator(getString(R.string.favorite));
        Intent songsIntent = new Intent(this, FavoriteActivity.class);
        favorite.setContent(songsIntent);

        TabHost.TabSpec callLog = tabHost.newTabSpec("CallLog");
        callLog.setIndicator(getString(R.string.call_log));
        Intent photosIntent = new Intent(this, CallLogActivity.class);
        callLog.setContent(photosIntent);

        TabHost.TabSpec contact = tabHost.newTabSpec("Contacts");
        contact.setIndicator(getString(R.string.contacts));
        Intent videosIntent = new Intent(this, ContactActivity.class);
        contact.setContent(videosIntent);

        tabHost.addTab(favorite);
        tabHost.addTab(callLog);
        tabHost.addTab(contact);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater imf = getMenuInflater();
        imf.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item1) {
            Intent intent = new Intent(MainActivity.this, InsertCallLog.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}

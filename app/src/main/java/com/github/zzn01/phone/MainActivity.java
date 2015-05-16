package com.github.zzn01.phone;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main);

        findViewById(R.id.tab_favorite).setOnClickListener(
            new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    switchFragment(FavoriteFragment.newInstance());
                }
            }
        );
        findViewById(R.id.tab_log).setOnClickListener(
            new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    switchFragment(LogFragment.newInstance());
                }
            }
        );

        findViewById(R.id.tab_contact).setOnClickListener(
            new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    switchFragment(ContactFragment.newInstance());
                }
            }
        );

        switchFragment(FavoriteFragment.newInstance());


    }

    public void switchFragment(Fragment fragment){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_container, fragment);
        ft.commit();
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
           // Intent intent = new Intent(MainActivity.this, InsertCallLog.class);
           // startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}

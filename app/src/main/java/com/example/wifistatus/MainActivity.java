package com.example.wifistatus;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Fragment fragment = getFragmentManager().findFragmentById(R.id.content_frame);
        if (fragment == null)
        {
            fragment = new MainFragment();
            getFragmentManager().beginTransaction().add(R.id.content_frame, fragment).commit();
        }
    }

}

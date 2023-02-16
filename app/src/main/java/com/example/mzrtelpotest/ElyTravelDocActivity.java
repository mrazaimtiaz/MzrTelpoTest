package com.example.mzrtelpotest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.MenuItem;

import com.elyctis.idtabsdk.io.Io;

public class ElyTravelDocActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private ElyTravelDocPagerAdapter mElyTravelDocPagerAdapter;
    private BroadcastReceiver mReceiver = null;
    private PendingIntent mPendingIntent = null;

    // TODO Continue implementation of a workaround to not power off module when screen orientation change
    //public static final String KEY_LAST_ORIENTATION = "last_orientation";
    //private int mLastOrientation;

    public boolean enableLog = false;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ely_travel_doc);
        setupActionBar();

        //if (savedInstanceState == null) {
        //    mLastOrientation = getResources().getConfiguration().orientation;
        //}

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mElyTravelDocPagerAdapter = new ElyTravelDocPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        mViewPager.setAdapter(mElyTravelDocPagerAdapter);

        // Instead of doing multiple connection attempt to the scanner
        // we wait for broadcast messages
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equalsIgnoreCase(UsbAttachedReceiver.ACTION_MODULE_ATTACHED) ||
                        action.equalsIgnoreCase(UsbAttachedReceiver.ACTION_MODULE_FORCE_REFRESH)) {
                    // Dismiss waiting message and check connection
                    ElyDialogFragment.dismiss( getFragmentManager());

                    // TODO Check if the CL reader and scanner are listed
                    /*if(mReaderFragment.refreshReaderList()==0) {
                        ElyDialogFragment.show( getFragmentManager(), R.string.error_peripheral,
                                R.string.error_scard_noreaders, android.R.attr.alertDialogIcon);
                    }*/
                }
            }
        };

        mPendingIntent = PendingIntent.getBroadcast( this, 0,
                new Intent(UsbAttachedReceiver.ACTION_MODULE_FORCE_REFRESH), 0 );
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Power ON Module
        Io.powerOnTopModule();

        // Waiting for module connection
        ElyDialogFragment.show( getFragmentManager(), R.string.title_info,
                R.string.text_waiting_peripheral, android.R.attr.actionModeFindDrawable);
    }


    protected void onResume() {
        super.onResume();

        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(UsbAttachedReceiver.ACTION_MODULE_ATTACHED);
        intentfilter.addAction(UsbAttachedReceiver.ACTION_MODULE_FORCE_REFRESH);
        registerReceiver(mReceiver, intentfilter);

        // Set alarm to force module status refreshing
        AlarmManager am = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
        am.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
                2000, mPendingIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        AlarmManager am = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
        am.cancel(mPendingIntent);
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Power OFF Modules
        Io.powerOffTopModule();
    }

    /*@Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLastOrientation = savedInstanceState.getInt(KEY_LAST_ORIENTATION);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_LAST_ORIENTATION, mLastOrientation);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ely_travel_doc, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        if(!enableLog) {
            menu.getItem(0).setEnabled(true);
            menu.getItem(1).setEnabled(false);
        } else {
            menu.getItem(0).setEnabled(false);
            menu.getItem(1).setEnabled(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }
        */
        if(id == R.id.enableLog) {
            enableLog = true;
            return true;
        }else if(id == R.id.disableLog) {
            enableLog = false;
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

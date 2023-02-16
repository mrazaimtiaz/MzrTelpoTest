package com.example.mzrtelpotest;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.elyctis.idtabsdk.usbpermission.UsbPermissionActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    static final DashboardIcon[] DASHBOARD_ICONS = {
            new DashboardIcon(R.drawable.ic_eye, "ELY MRZ", ElyMrzActivity.class),
            new DashboardIcon(R.drawable.ic_radiowaves, "ELY SCARD", ElySCardActivity.class),
            new DashboardIcon(R.drawable.ic_plane, "ELY TRAVEL DOC", ElyTravelDocActivity.class),
            new DashboardIcon(R.drawable.ic_gear, "SETTINGS", SettingsActivity.class),
            /*
            new DashboardIcon(R.drawable.ic_plane, "ELY CHECKIN", ElyCheckInActivity.class),
            new DashboardIcon(R.drawable.ic_plane, "LOGIN", ElyLoginActivity.class),
            new DashboardIcon(R.drawable.ic_eye, "Profile", ElyProfileActivity.class),
            new DashboardIcon(R.drawable.ic_eye, "REGISTER", ElyRegisterActivity.class),
             */
    };
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;
        float scaleFactor = metrics.density;
        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;

        if (BuildConfig.DEBUG) {
            setTitle(getResources().getText(R.string.app_name) + " DEBUG Mode v" + BuildConfig.VERSION_NAME);
        } else {
            setTitle(getResources().getText(R.string.app_name) + " v" + BuildConfig.VERSION_NAME);
        }

        sendBroadcast(new Intent("android.intent.action.OTG_MODE"));
        GridView gridview = (GridView) findViewById(R.id.dashboard_grid);
        gridview.setAdapter(new DashboardIconAdapter(this, DASHBOARD_ICONS));

        gridview.setOnItemClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO mail to support@elyctis.com
                Snackbar.make(view, "mail to support@elyctis.com", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        UsbPermissionActivity usbPermissionActivity = new UsbPermissionActivity(getApplicationContext()," com.elyctis.idtabsuite");
        if(!usbPermissionActivity.isAllUsbDevicesAccessPermissionGranted()) {
            usbPermissionActivity.getUsbDeviceAccessPermission();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

        // Check if DocumentService is running and if it could conflict with the activity
        if((DASHBOARD_ICONS[position].shortcut == ElyMrzActivity.class ||
                DASHBOARD_ICONS[position].shortcut == ElyTravelDocActivity.class) &&
                DocumentService.isRunning()) {
            // Display dialog alert
            ElyDialogFragment.show( getFragmentManager(), R.string.title_error,
                    R.string.error_peripheral_access_conflict, android.R.attr.alertDialogIcon);
        } else {
            Intent intent = new Intent(this, DASHBOARD_ICONS[position].shortcut);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}

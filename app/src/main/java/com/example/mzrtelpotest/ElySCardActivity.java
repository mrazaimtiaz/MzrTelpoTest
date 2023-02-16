package com.example.mzrtelpotest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.elyctis.idtabsdk.io.Io;
import com.elyctis.idtabsdk.scard.SCardReader;
import com.elyctis.idtabsdk.usbpermission.UsbPermissionActivity;

import java.io.File;

/**
 * Implements the activity that lists the smartcard readers and performs the execution of *.apdu
 * files from the system. Listing the smartcard readers is dealt by ElyScardReaderFragment and
 * executing the *.apdu files is by ElyScardScriptFragment.
 */
public class ElySCardActivity extends AppCompatActivity {

    String TAG = "ElySCardActivity";
    private ElySCardReaderFragment mReaderFragment;
    private ElySCardScriptFragment mScriptFragment;
    private WebView wvApduLog;
    public SCardReader mScardReader = null;
    private BroadcastReceiver mReceiver = null;
    private PendingIntent mPendingIntent = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ely_scard);

        setupActionBar();

        mReaderFragment = (ElySCardReaderFragment)
                (getFragmentManager().findFragmentById(R.id.fragment_reader_list));
        mScriptFragment = (ElySCardScriptFragment)
                (getFragmentManager().findFragmentById(R.id.fragment_script_control));
        wvApduLog = (WebView)findViewById(R.id.webview_ApduLog);
        wvApduLog.setBackgroundColor(Color.TRANSPARENT);

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
                    if(mReaderFragment.refreshReaderList()==0) {
                        ElyDialogFragment.show( getFragmentManager(), R.string.title_error,
                                R.string.error_scard_no_reader, android.R.attr.alertDialogIcon);
                    }
                }
            }
        };

        mPendingIntent = PendingIntent.getBroadcast( this, 0,
                new Intent(UsbAttachedReceiver.ACTION_MODULE_FORCE_REFRESH), 0 );
        UsbPermissionActivity usbPermissionActivity = new UsbPermissionActivity(getApplicationContext()," com.elyctis.idtabsuite");
        if(!usbPermissionActivity.isAllUsbDevicesAccessPermissionGranted()) {
            usbPermissionActivity.getUsbDeviceAccessPermission();
        }
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

        // Power ON Modules
        Io.powerOnTopModule();
        Io.powerOnBottomModule();

        // Waiting for module connection
        ElyDialogFragment.show( getFragmentManager(), R.string.title_info,
                R.string.text_waiting_peripheral, android.R.attr.actionModeFindDrawable);
    }

    @Override
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
        Io.powerOffBottomModule();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            // Check if readers are listed
            if(mReaderFragment.refreshReaderList()==0) {
                ElyDialogFragment.show( getFragmentManager(), R.string.title_error,
                        R.string.error_scard_no_reader, android.R.attr.alertDialogIcon);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setSmartCardReader (SCardReader sCardReader) {
        this.mScardReader = sCardReader;
    }

    public SCardReader getSmartCardReader () {
        return this.mScardReader;
    }

    public void webClear () {
        wvApduLog.loadData("", "text/html", "utf-8");
    }

    public void webLoad (String filePath) {
        if(!(Environment.getExternalStorageState().equalsIgnoreCase("MOUNTED"))) {
            ElyDialogFragment.show( getFragmentManager(), R.string.error_storage,
                    R.string.error_storage_not_mounted, android.R.attr.alertDialogIcon);
            return;
        }

        File file = new File(Environment.getExternalStorageDirectory().getPath() + filePath);
        if (!file.exists())  {

            return;
        }

        wvApduLog.loadUrl("file:///" + file.getPath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            wvApduLog.getSettings().setTextZoom(
                    (int)getResources().getDimension(R.dimen.web_view_zoom_size));
        }
    }
}

package com.example.mzrtelpotest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


import com.elyctis.idtabsdk.io.Io;
import com.elyctis.idtabsdk.mrz.MrzScanner;
import com.elyctis.idtabsdk.usbpermission.UsbPermissionActivity;

public class ElyMrzActivity extends AppCompatActivity {
    private TextView tvMrz;
    String TAG = "ElyMrzActivity";
    private MrzScanner mScanner = null;
    private BroadcastReceiver mReceiver = null;
    private PendingIntent mPendingIntent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ely_mrz);

        setupActionBar();

        sendBroadcast(new Intent("android.intent.action.OTG_MODE"));
        tvMrz = (TextView)(findViewById(R.id.tvMrz));
        mScanner = new MrzScanner(getApplicationContext(), null);

        // Instead of doing multiple connection attempt to the scanner
        // we wait for broadcast messages
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equalsIgnoreCase(UsbAttachedReceiver.ACTION_MODULE_ATTACHED) ||
                        action.equalsIgnoreCase(UsbAttachedReceiver.ACTION_MODULE_FORCE_REFRESH)) {
                    // Check connection and dismiss waiting message
                    boolean isOpen = mScanner.open();
                    ElyDialogFragment.dismiss(getFragmentManager());
                    mScanner.close();
                    // Check status
                    if (!isOpen) {
                        Log.i("ElyMrzActivity", "No MRZ scanner detected.");
                        ElyDialogFragment.show(getFragmentManager(), R.string.title_error,
                                R.string.error_mrzscanner_conn, android.R.attr.alertDialogIcon);
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
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: calling read mrz");
                // Do something after 5s = 5000ms
                readContinue();
            }
        }, 1000);



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

        // Power ON MRZ Scanner
        Io.powerOnTopModule();

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

        // Power OFF MRZ Scanner
        Io.powerOffTopModule();
    }

    public void getInfo(View view) {
        if(mScanner.open()) {
            try {
                String info = "Firmware version:\t\t" +
                        mScanner.getFirmwareVersion() +
                        "\nNNA version:\t\t\t\t\t\t\t" +
                        mScanner.getNnaVersion() +
                        "\nProduct Info:\t\t\t\t\t\t" +
                        mScanner.getProductInfo() +
                        "\nSerial Number:\t\t\t\t\t" +
                        mScanner.getSerialNumber();

                tvMrz.setText(info);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mScanner.close();
        } else {
            tvMrz.setText(R.string.error_mrzscanner_conn);
        }
    }

    public void readContinue(){
        if(mScanner.open()) {
            String mrz = "";
            try {
                mrz = mScanner.readMrz();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mScanner.close();

            if(mrz.trim().length() == 0) {
                tvMrz.setText(R.string.error_mrzscanner_nomrz);
            } else {
                tvMrz.setText(mrz.replace('\r', '\n'));
            }
        } else {
            tvMrz.setText(R.string.error_mrzscanner_conn);
        }
    }
    public void readMrz(View view) {
        if(mScanner.open()) {
            String mrz = "";
            try {
                mrz = mScanner.readMrz();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mScanner.close();

            if(mrz.trim().length() == 0) {
                tvMrz.setText(R.string.error_mrzscanner_nomrz);
            } else {
                tvMrz.setText(mrz.replace('\r', '\n'));
            }
        } else {
            tvMrz.setText(R.string.error_mrzscanner_conn);
        }
    }
}

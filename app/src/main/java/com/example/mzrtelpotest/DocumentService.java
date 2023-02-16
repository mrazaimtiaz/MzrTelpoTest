package com.example.mzrtelpotest;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.elyctis.idtabsdk.icao.IcaoMrzParser;
import com.elyctis.idtabsdk.io.Io;
import com.elyctis.idtabsdk.mrz.DataElement;
import com.elyctis.idtabsdk.mrz.MrzParser;
import com.elyctis.idtabsdk.mrz.MrzScanner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DocumentService extends Service {

    private static final String TAG = "DocumentService";

    private static final String ACTION_STARTDOCREADING = "com.example.mzrtelpotest.action.STARTDOCREADING";
    private static final String ACTION_STOPDOCREADING = "com.example.mzrtelpotest.action.STOPDOCREADING";

    private static final int NOTIFICATION_STATUS = 0;
    private static final int NOTIFICATION_NEWDOC = 1;

    // Parameters
    private static final String EXTRA_PARAM_URL = "com.example.mzrtelpotest.extra.URL";
    private static final String EXTRA_PARAM_OPERATOR = "com.example.mzrtelpotest.extra.OPERATOR";

    private MrzScanner mScanner = null;
    private BroadcastReceiver mReceiver = null;
    private PendingIntent mPendingIntent = null;

    // Flag that the BroadcastReceiver has been registered
    private static Boolean sRegistered = false;

    private String mUrlAction = "https://inspection-test.herokuapp.com";
    private String mUrlSend = "";
    private String mOperator = "";

    public DocumentService() {
    }

    @Override
    public void onCreate() {
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equalsIgnoreCase(UsbAttachedReceiver.ACTION_MODULE_ATTACHED)){
                    handleActionStartDocReading(mUrlSend, mOperator);
                } else if(action.equalsIgnoreCase(UsbAttachedReceiver.ACTION_MODULE_FORCE_REFRESH)){
                    // Check the status
                    if(mScanner==null) {
                        NewDocumentNotification.notify(getApplicationContext(), R.string.title_error,
                                R.string.error_mrzscanner_conn, "", mUrlAction, NOTIFICATION_STATUS);
                    }
                }
            }
        };

        mPendingIntent = PendingIntent.getBroadcast( this, 0,
                new Intent(UsbAttachedReceiver.ACTION_MODULE_FORCE_REFRESH), 0 );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_STARTDOCREADING.equals(action)) {
                mUrlSend = intent.getStringExtra(EXTRA_PARAM_URL);
                mOperator = intent.getStringExtra(EXTRA_PARAM_OPERATOR);

                handleActionInitDocReading();

                // TODO check status and handle/diplay error if occurs
                // Use a
            } else if (ACTION_STOPDOCREADING.equals(action)) {
                handleActionStopDocReading();
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        handleActionStopDocReading();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * Handle document reading action in the provided background thread.
     */
    private void handleActionInitDocReading() {
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(UsbAttachedReceiver.ACTION_MODULE_ATTACHED);
        intentfilter.addAction(UsbAttachedReceiver.ACTION_MODULE_FORCE_REFRESH);
        registerReceiver(mReceiver, intentfilter);
        sRegistered = true;

        AlarmManager am = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
        am.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() +
                2000, mPendingIntent);
        // Power ON MRZ Scanner
        Io.powerOnTopModule();

        NewDocumentNotification.cancel(this);
        Notification notification = NewDocumentNotification.get(this,
                R.string.title_document_service, R.string.text_document_notification_waiting,
                "", mUrlAction, NOTIFICATION_STATUS);
        startForeground(1, notification);
    }

    /**
     * Handle reset of the continuous read mode in the provided background thread.
     */
    private void handleActionResetDocReading() {
        // Re-enable continuous read mode
        try {
            mScanner.setContinuousReadMode(true);
        } catch (Exception e) {
            e.printStackTrace();
            NewDocumentNotification.notify(getApplicationContext(), R.string.title_error,
                    R.string.error_mrzscanner_conn, "", mUrlAction, NOTIFICATION_STATUS);
        }
    }

    /**
     * Handle document reading action in the provided background thread with the provided
     * parameters.
     */
    private void handleActionStartDocReading(String url, String operator) {
        mScanner = new MrzScanner(getApplicationContext(), new MrzScanner.Listener() {
            @Override
            public void onContinuousReadModeCallback(String mrz) {
                Log.d(TAG, mrz);
                MrzParser ePassportParser;
                ePassportParser = MrzParser.match(IcaoMrzParser.ICAO_MRZ_PARSERS, mrz);
                if (ePassportParser != null) {
                    HashMap<String, DataElement> documentData = ePassportParser.parse(mrz);

                    try {
                        String issuing_state = documentData.get(IcaoMrzParser.KEY_ISSUING_STATE).getRefineString();
                        String surname = documentData.get(IcaoMrzParser.KEY_SURNAME).getRefineString();
                        String name = documentData.get(IcaoMrzParser.KEY_GIVEN_NAMES).getRefineString();
                        String doc_number = documentData.get(IcaoMrzParser.KEY_DOC_NUM).getRefineString();
                        String sex = documentData.get(IcaoMrzParser.KEY_SEX).getRefineString();
                        String dob = documentData.get(IcaoMrzParser.KEY_DOB).getRefineString();
                        String doe = documentData.get(IcaoMrzParser.KEY_DOE).getRefineString();

                        NewDocumentNotification.notify(getApplicationContext(),
                                R.string.title_document_service, R.string.text_document_notification_mrz_valid,
                                mrz, mUrlAction, NOTIFICATION_NEWDOC);

                        // Send data to server
                        if (mUrlSend != "")
                            postMrzData(mrz, "",
                                    issuing_state, surname, name, doc_number, sex, dob, doe);
                    }catch(Exception exc) {
                        Log.e(TAG, "Failed to get all refine string from MRZ");
                        ePassportParser = null;
                    }
                }

                // Notify the incorrect MRZ
                if (ePassportParser == null) {
                    NewDocumentNotification.notify(getApplicationContext(),
                            R.string.title_document_service, R.string.text_document_notification_mrz_invalid,
                            mrz, mUrlAction, NOTIFICATION_NEWDOC);
                }

                handleActionResetDocReading();
            }

            @Override
            public void onPresenceDetectModeCallback(byte b) {

            }
        });

        // Initialize scanner and run service
        mScanner.open();
        handleActionResetDocReading();
    }

    /**
     * Handle action stop in the provided background thread.
     */
    private void handleActionStopDocReading() {
        try {
            if(mScanner!=null) {
                mScanner.setContinuousReadMode(false);
                mScanner.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(sRegistered) {
            AlarmManager am = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
            am.cancel(mPendingIntent);
            unregisterReceiver(mReceiver);

            sRegistered = false;
        }

        stopForeground(true);

        NewDocumentNotification.cancel(this);

        // Power OFF MRZ Scanner
        Io.powerOffTopModule();
    }

    /**
     * Starts this service to perform document reading with the given parameters.
     *
     * @see Service
     */
    public static void startDocumentReading(Context context, String url, String operator) {
        Intent intent = new Intent(context, DocumentService.class);
        intent.setAction(ACTION_STARTDOCREADING);
        intent.putExtra(EXTRA_PARAM_URL, url);
        intent.putExtra(EXTRA_PARAM_OPERATOR, operator);
        context.startService(intent);
    }

    /**
     * Stops document reading.
     *
     * @see Service
     */
    public static void stopDocumentReading(Context context) {
        Intent intent = new Intent(context, DocumentService.class);
        intent.setAction(ACTION_STOPDOCREADING);
        context.startService(intent);
    }

    public static boolean isRunning() {
        return sRegistered;
    }

    private void postMrzData(final String mrz, final String doc_type, final String issuing_state,
                             final String surname, final String name, final String doc_number,
                             final String sex, final String dob, final String doe) {
        Log.d(TAG, "Post MRZ");
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, mUrlSend,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d(TAG, response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //tvMrz.setText("Check in successful\nReservation id: " + jsonObject.optString("_id"));
                            Log.d(TAG, jsonObject.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // TODO handle and notify the error
                            Log.d(TAG, e.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO handle and notify the error
                        Log.d(TAG, error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                String [] lines = mrz.split("\r");
                params.put("line1", lines[0]);
                if (lines.length > 1)
                    params.put("line2", lines[1]);
                if (lines.length > 2)
                    params.put("line3", lines[2]);

                params.put("operator", mOperator);
                params.put("doc_type", doc_type);
                params.put("issuing_state", issuing_state);
                params.put("surname", surname);
                params.put("name", name);
                params.put("doc_number", doc_number);
                params.put("sex", sex);
                params.put("date_of_birth", dob);
                params.put("date_of_expiry", doe);
                return params;
            }
        };
        queue.add(postRequest);
    }
}

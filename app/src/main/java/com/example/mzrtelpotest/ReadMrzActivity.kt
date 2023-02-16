package com.example.mzrtelpotest

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.elyctis.idtabsdk.mrz.MrzScanner
import com.elyctis.idtabsdk.usbpermission.UsbPermissionActivity
import com.example.mzrtelpotest.utils.MrzParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReadMrzActivity : AppCompatActivity() {


    private var mScanner: MrzScanner? = null
    private var mReceiver: BroadcastReceiver? = null
    private var mPendingIntent: PendingIntent? = null


   lateinit  var mzr_text: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_mrz)

        sendBroadcast(Intent("android.intent.action.OTG_MODE"))

        mScanner = MrzScanner(applicationContext, null)

        mzr_text =  findViewById<TextView>(R.id.mzr_text)


        // Instead of doing multiple connection attempt to the scanner
        // we wait for broadcast messages
        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action
                if (action.equals(UsbAttachedReceiver.ACTION_MODULE_ATTACHED, ignoreCase = true) ||
                    action.equals(
                        UsbAttachedReceiver.ACTION_MODULE_FORCE_REFRESH,
                        ignoreCase = true
                    )
                ) {
                    // Check connection and dismiss waiting message
                    val isOpen = mScanner!!.open()
                    ElyDialogFragment.dismiss(fragmentManager)
                    mScanner!!.close()
                    // Check status
                    if (!isOpen) {
                        Log.i("ElyMrzActivity", "No MRZ scanner detected.")
                        ElyDialogFragment.show(
                            fragmentManager, R.string.title_error,
                            R.string.error_mrzscanner_conn, android.R.attr.alertDialogIcon
                        )
                    }
                }
            }
        }
        mPendingIntent = PendingIntent.getBroadcast(
            this, 0,
            Intent(UsbAttachedReceiver.ACTION_MODULE_FORCE_REFRESH), 0
        )
        val usbPermissionActivity = UsbPermissionActivity(
            applicationContext, " com.elyctis.idtabsuite"
        )
        if (!usbPermissionActivity.isAllUsbDevicesAccessPermissionGranted) {
            usbPermissionActivity.getUsbDeviceAccessPermission()
        }
        CoroutineScope(Dispatchers.Main).launch {
            while (true){
                delay(1000)
                readContinue()
            }
        }

    }

    fun readContinue() {
        if (mScanner!!.open()) {
            var mrz = ""
            try {
                mrz = mScanner!!.readMrz()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mScanner!!.close()
            try{
                if (mrz.trim { it <= ' ' }.length == 0) {
                    mzr_text.setText(R.string.error_mrzscanner_nomrz)
                } else {
                    mzr_text.setText(mrz.replace('\r', '\n'))
                    var record = MrzParser.parse(mrz.replace('\r', '\n'));

                    mzr_text.setText(mrz.replace('\r', '\n') + "\n" + record.givenNames + " " + record.surname)
                    System.out.println("Name: " + record.givenNames + " " + record.surname);
                }
            }catch (e: Exception){
                e.printStackTrace()

            }

        } else {
            mzr_text.setText(R.string.error_mrzscanner_conn)
        }
    }
}
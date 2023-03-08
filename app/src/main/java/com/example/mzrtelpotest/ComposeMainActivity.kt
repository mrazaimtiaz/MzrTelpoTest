package com.example.mzrtelpotest

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elyctis.idtabsdk.mrz.MrzScanner
import com.elyctis.idtabsdk.usbpermission.UsbPermissionActivity
import com.example.mzrtelpotest.common.Constants
import com.example.mzrtelpotest.domain.model.SelectDepartment
import com.example.mzrtelpotest.presentation.SelectServiceScreen
import com.example.mzrtelpotest.presentation.SettingScreen
import com.example.mzrtelpotest.ui.theme.MzrTelpoTestTheme
import com.gicproject.onepagequeuekioskapp.Screen
import com.gicproject.onepagequeuekioskapp.presentation.MyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ComposeMainActivity : ComponentActivity() {



    private var viewModel: MyViewModel? = null

    private var mScanner: MrzScanner? = null
    private var mReceiver: BroadcastReceiver? = null
    private var mPendingIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        sendBroadcast(Intent("android.intent.action.OTG_MODE"))

        mScanner = MrzScanner(applicationContext, null)



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

        super.onCreate(savedInstanceState)
        setContent {
            viewModel = hiltViewModel()
            viewModel?.initMzr(scanner = mScanner)
            MzrTelpoTestTheme(darkTheme = false)  {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.SelectServiceScreen.route
                    ) {
                        composable(
                            route = Screen.SelectServiceScreen.route
                        ) {

                            var selectDepartment =
                                navController.previousBackStackEntry?.savedStateHandle?.get<SelectDepartment?>(
                                    Constants.STATE_SELECT_DEPARTMENT
                                )
                            viewModel?.readCivilIdOff()
                            SelectServiceScreen(selectDepartment,navController, viewModel!!)
                        }
                        composable(
                            route = Screen.SettingScreen.route
                        ) {
                            viewModel?.readCivilIdOff()
                            SettingScreen(navController, viewModel!!)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MzrTelpoTestTheme {
        Greeting("Android")
    }
}
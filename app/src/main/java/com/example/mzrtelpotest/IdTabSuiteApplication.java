package com.example.mzrtelpotest;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.StrictMode;

import com.github.anrwatchdog.ANRWatchDog;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

@ReportsCrashes(
        formUri = "https://acra-20831b.smileupps.com/acra-myapp-4bc7c1/_design/acra-storage/_update/report",
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        formUriBasicAuthLogin="idtabsuite",
        formUriBasicAuthPassword="elyctis",
        // Your usual ACRA configuration
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.crash_toast_text,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. When defined, adds a user text field input with this text resource as a label
        resDialogEmailPrompt = R.string.crash_user_email_label, // optional. When defined, adds a user email text entry with this text resource as label. The email address will be populated from SharedPreferences and will be provided as an ACRA field if configured.
        resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
        //resDialogTheme = R.style.AppTheme_Dialog //optional. default is Theme.Dialog

        /*mailTo = "support@elyctis.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text*/
)

public class IdTabSuiteApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // Create an ConfigurationBuilder. It is prepopulated with values specified via annotation.
        // Set any additional value of the builder and then use it to construct an ACRAConfiguration.
        /*final ACRAConfiguration config = new ConfigurationBuilder(this)
                .setFoo(foo)
                .setBar(bar)
                .build();*/

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        if (BuildConfig.DEBUG) {
            StrictMode.enableDefaults();
            //DropBoxManager dpm = (DropBoxManager) getSystemService(DROPBOX_SERVICE);
            //dpm.addData(dd,);
        }

        super.onCreate();

        if (!BuildConfig.DEBUG) {
            // Run watchdog every 10sec
            new ANRWatchDog(10000).start();
            /*new ANRWatchDog().setANRListener(new ANRWatchDog.ANRListener() {
                @Override
                public void onAppNotResponding(ANRError error) {
                    // Handle the error. For example, log it to HockeyApp:
                    ExceptionHandler.saveException(error, new CrashManager());
                }
            }).start();*/
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }



    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}

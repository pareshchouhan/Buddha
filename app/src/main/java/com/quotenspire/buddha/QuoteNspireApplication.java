package com.quotenspire.buddha;

import android.app.Application;
import android.content.Context;

import com.tsengvn.typekit.Typekit;

//import org.acra.ACRA;
//import org.acra.ReportingInteractionMode;
//import org.acra.annotation.ReportsCrashes;
//import org.acra.sender.HttpSender;

/**
 * Created by Paresh on 7/23/2016.
 */


//@ReportsCrashes(
//        httpMethod = HttpSender.Method.PUT,
//        reportType = HttpSender.Type.JSON,
//        formUri = "http://reporting.futuretraxex.com/acra-quotenspire/_design/acra-storage/_update/report",
//        formUriBasicAuthLogin = "buddha",
//        formUriBasicAuthPassword = "vye5tAcCGp4r9V6Z8EvB",
//        resToastText = R.string.crash,
//        mode = ReportingInteractionMode.DIALOG,
//        resDialogText = R.string.crash_dialog_text,
//        resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
//        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
//        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. When defined, adds a user text field input with this text resource as a label
//        resDialogOkToast = R.string.crash_dialog_ok_toast, // optional. displays a Toast message
//        // when the user accepts to send a report.
//        resDialogTheme = R.style.AppTheme_Dialog //optional. default is Theme.Dialog
//)
public class QuoteNspireApplication extends Application {
    public QuoteNspireApplication() {
        super();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        ACRA.init(this);
    }

    @Override
    public void onCreate() {

//        if(BuildConfig.DEBUG)   {
//            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                    .detectDiskReads()
//                    .detectDiskWrites()
//                    .detectNetwork()   // or .detectAll() for all detectable problems
//                    .penaltyLog()
//                    .build());
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectLeakedSqlLiteObjects()
////                .detectLeakedClosableObjects()
//                    .penaltyLog()
////                    .penaltyDeath()
//                    .build());
//        }

        Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "Montserrat-Bold.ttf"));
        super.onCreate();

    }
}

package com.quotenspire.buddha.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by Paresh Chouhan on 8/24/2016.
 */

public class DeviceBootRecv extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //In case of reboot alarms get reset. so we set it again.
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Calendar time = Calendar.getInstance();
//            time.set(Calendar.DAY_OF_YEAR, 1);
            time.set(Calendar.HOUR_OF_DAY, 6);
            time.set(Calendar.MINUTE, 0);
            time.set(Calendar.SECOND, 0);
            time.set(Calendar.MILLISECOND, 0);
            if(Calendar.getInstance().after(time)){
                time.add(Calendar.DATE, 1);
            }

            Intent launchIntent = new Intent(context, QuoteNotificationReciever.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, launchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context
                    .ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    time.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }
    }
}

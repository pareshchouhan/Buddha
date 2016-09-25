package com.quotenspire.buddha.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.quotenspire.buddha.QuoteNSpireActivity;
import com.quotenspire.buddha.R;
import com.quotenspire.buddha.provider.quotes.QuotesContentValues;
import com.quotenspire.buddha.provider.quotes.QuotesCursor;
import com.quotenspire.buddha.provider.quotes.QuotesSelection;


/**
 * Created by Paresh on 8/24/2016.
 */

public class QuoteNotificationReciever extends BroadcastReceiver {

    public QuoteNotificationReciever() {
        super();
    }

    private Context cContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        cContext = context;
        getQuoteAndCreateNotification();
    }

    private Context getContext()    {
        return cContext;
    }


    private void getQuoteAndCreateNotification()    {
        QuotesSelection quotesSelection = new QuotesSelection();
        quotesSelection.status(false);
        quotesSelection.limit(1);
        QuotesCursor q = quotesSelection.query(getContext());
        if(q.moveToFirst() == false) {
            resetDb();
            q.close();
            q = new QuotesSelection().uid(0).query(getContext());
//                    q = quotesSelection.query(getContext());
            QuotesSelection where = new QuotesSelection();
            if(q.moveToFirst()) {
                where.id(q.getId());
                QuotesContentValues updatedValue = new QuotesContentValues();
                updatedValue.putStatus(true);
                updatedValue.update(getContext(), where);
            }
            else {
                return;
            }
        } else {
            //Update the selected id status to true, so it doesn't get selected again.
            QuotesSelection where = new QuotesSelection();
            where.id(q.getId());
            QuotesContentValues updatedValue = new QuotesContentValues();
            updatedValue.putStatus(true);
            updatedValue.update(getContext(), where);
        }
        final String quote = q.getQuote();
        final int quid = q.getUid();
        if(!q.isClosed())    {
            q.close();
        }
        Log.w("Authority", "Showing Notification");
        //Now show notification.
        createNotification(quid, quote);
    }

    private void resetDb()  {
        new Thread(new Runnable() {
            @Override
            public void run() {
                QuotesContentValues quotesContentValues = new QuotesContentValues();
                quotesContentValues.putStatus(false);
                QuotesSelection where = new QuotesSelection();
                where.status(true);
                quotesContentValues.update(getContext(), where);
            }
        }).start();
    }

    private void createNotification(int uid, String quote)   {
        Bitmap largeIcon = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap
                .ic_launcher);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setSound(alarmSound)
                        .setLargeIcon(largeIcon)
                        .setContentTitle("QuoteNSpire")
                        .setContentText(quote);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("Quote \"N\" Spire");
        bigTextStyle.setSummaryText("Quote of the day");
        bigTextStyle.bigText(quote);
        builder.setStyle(bigTextStyle);

        Intent launchAppIntent = new Intent(getContext(), QuoteNSpireActivity.class);
        launchAppIntent.putExtra(getContext().getString(R.string.uid), uid);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
        stackBuilder.addNextIntent(launchAppIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent
                .FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT < 16)  {
            notificationManager.notify(uid, builder.getNotification());
        } else {
            notificationManager.notify(uid, builder.build());
        }


    }
}

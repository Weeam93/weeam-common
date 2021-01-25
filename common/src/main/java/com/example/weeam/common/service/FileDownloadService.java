package com.example.weeam.common.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.webkit.URLUtil;

import com.example.weeam.common.receiver.DownloadFileServiceReceiver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Weeam Awad on 5/5/2018.
 */

public class FileDownloadService extends IntentService {
    public static final String NOTIFICATION_TITLE_STRING_KEY = "notification_title";
    public static final String NOTIFICATION_MESSAGE_STRING_KEY = "notification_message";
    public static final String NOTIFICATION_ICON_RESOURCE_KEY = "notification_icon_resource_id";
    public static final String DOWNLOAD_FILE_URL = "download_url";


    public static final String SERVICE_NAME = "FileDownloadService";
    private final String tag = getClass().getSimpleName();

    private Intent mIntent;
    private final int mServiceId = 1000;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;

    public FileDownloadService() {
        super(SERVICE_NAME);
    }


    @Override
    public void onCreate() {
        Log.d(tag, "Creating Service");
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(tag, "Starting Service");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(tag, "Inside Handle Intent");
        mIntent = intent;
        startForeground(mServiceId, buildNotification());
        downloadFile();
    }

    @Override
    public void onDestroy() {
        Log.d(tag, "Destroying Service");
        super.onDestroy();
    }

    private void downloadFile() {
        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
        String urlToDownload = mIntent.getStringExtra(DOWNLOAD_FILE_URL);
        String downloadFileName = URLUtil.guessFileName(urlToDownload, null, null);
        try {
            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect();
            // this will be useful so that you can show a typical 0-100% progress bar
            int fileLength = connection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream(new File(root.getPath(), downloadFileName));

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                updateDownloadPercent((int) (total * 100) / fileLength);
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateDownloadPercent(int currentPercent) {
        if (currentPercent < 100) {
            mBuilder.setProgress(100, currentPercent, false)
                    .setSubText(currentPercent + "%");
        } else {
            // Removes the progress bar
            mBuilder.setProgress(0, 0, false)
                    .setOngoing(false)
                    .setSubText("")
                    .setContentText("Download complete");
        }
        mNotificationManager.notify(mServiceId, mBuilder.build());
    }

    private Notification buildNotification() {

        Intent intent = getPackageManager()
                .getLaunchIntentForPackage(getPackageName())
                .setPackage(null)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        String title = mIntent.getStringExtra(NOTIFICATION_TITLE_STRING_KEY);
        String message = mIntent.getStringExtra(NOTIFICATION_MESSAGE_STRING_KEY);
        int iconResource = mIntent.getIntExtra(NOTIFICATION_ICON_RESOURCE_KEY, 0);


        mBuilder.setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setWhen(System.currentTimeMillis())
                .setContentText(message)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(iconResource);

        if (mBuilder.mActions.isEmpty()) {
            //Add Cancel Button and Associate BroadcastReceiver to handle the action
            Intent intentCancel = new Intent(this, DownloadFileServiceReceiver.class);
            PendingIntent cancelBroadcastIntent = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), intentCancel, PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationCompat.Action cancelAction = new NotificationCompat.Action(android.R.drawable.ic_menu_close_clear_cancel, "Cancel", cancelBroadcastIntent);
            mBuilder.addAction(cancelAction);
        }
        return mBuilder.build();
    }

}

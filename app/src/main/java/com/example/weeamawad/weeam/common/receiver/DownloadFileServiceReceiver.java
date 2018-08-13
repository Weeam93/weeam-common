package com.example.weeamawad.weeam.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Weeam Awad on 5/6/2018.
 */

public class DownloadFileServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, DownloadFileServiceReceiver.class);
        context.stopService(service);
    }
}

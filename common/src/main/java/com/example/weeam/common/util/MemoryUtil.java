package com.example.weeam.common.util;

import android.app.ActivityManager;
import android.content.Context;

public class MemoryUtil {

    private static final long MB_FACTOR = 1048576L;

    public static String getDeviceMemoryAsString(Context applicationContext) {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) applicationContext.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);

        final long maxHeapSizeMB = memoryInfo.totalMem / MB_FACTOR;
        final long availMemoryMB = memoryInfo.availMem / MB_FACTOR;
        final long currentMemoryUsageMB = maxHeapSizeMB - availMemoryMB;

        final double usagePercent = ((double) currentMemoryUsageMB / maxHeapSizeMB) * 100.0;

        return String.format("Device - MaxMemory = %d MB, CurrentMemoryUsage = %d MB, AvailableMemory = %d MB, UsagePercent = %4.2f", maxHeapSizeMB, currentMemoryUsageMB, availMemoryMB, usagePercent);
    }

    public static String getJVMMemoryAsString() {
        final Runtime runtime = Runtime.getRuntime();

        final long maxHeapSizeMB = runtime.maxMemory() / MB_FACTOR;
        final long currentMemoryUsageMB = (runtime.totalMemory() - runtime.freeMemory()) / MB_FACTOR;
        final long availableMemoryMB = maxHeapSizeMB - currentMemoryUsageMB;

        final double usagePercent = ((double) currentMemoryUsageMB / maxHeapSizeMB * 100.0);
        return String.format("JVM - MaxMemory = %d MB, CurrentMemoryUsage = %d MB, AvailableMemory = %d MB, UsagePercent = %4.2f", maxHeapSizeMB, currentMemoryUsageMB, availableMemoryMB, usagePercent);
    }
}

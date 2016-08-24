package com.udacity.gradle.builditbigger.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Helper class to handle device functionality
 *
 * @author Thomas Kioko
 */
public class DeviceUtils {

    /**
     * Helper method to check if the device has an internet connection
     *
     * @param context Context in which the application is called
     * @return {@link Boolean} True/False
     */
    public static boolean isNetworkConnected(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}

package com.thomaskioko.sunshine.widget.util;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.thomaskioko.sunshine.muezi.WeatherMuzeiSource;

/**
 * This class contains helper methods invoked to update Muzei and widgets
 *
 * @author Thomas Kioko
 */

public class WidgetUtils {

    public static final String ACTION_DATA_UPDATED = "com.thomaskioko.sunshine.ACTION_DATA_UPDATED";

    /**
     * Helper method invoked to update widgets.
     *
     * @param context Context the method is called
     */
    public static void updateWidgets(Context context) {
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED).setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }

    /**
     * Helper method invoked to update Muzei wallpaper
     *
     * @param context Context the method is called.
     */
    public static void updateMuzei(Context context) {
        // Muzei is only compatible with Jelly Bean MR1+ devices, so there's no need to update the
        // Muzei background on lower API level devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            context.startService(new Intent(ACTION_DATA_UPDATED).setClass(context, WeatherMuzeiSource.class));
        }
    }
}

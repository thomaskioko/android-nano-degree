package com.thomaskioko.sunshine.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.thomaskioko.sunshine.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Thomas Kioko
 */
public class Utility {

    /**
     * @param context Application context
     * @return {@link Boolean}
     */
    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_key_units),
                context.getString(R.string.pref_default_value_metric))
                .equals(context.getString(R.string.pref_default_value_metric));
    }

    /**
     * Method to format temperature
     *
     * @param temperature Temperature
     * @param isMetric    {@link Boolean} True/False
     * @return Formatted String
     */
    public static String formatTemperature(double temperature, boolean isMetric) {
        double temp;
        if (!isMetric) {
            temp = 9 * temperature / 5 + 32;
        } else {
            temp = temperature;
        }
        return String.format(Locale.getDefault(), "%.0f", temp);
    }

    /**
     * Method to format date
     *
     * @param dateInMillis Date
     * @return {@link String} Formatted date
     */
    public static String formatDate(long dateInMillis) {
        Date date = new Date(dateInMillis);
        return DateFormat.getDateInstance().format(date);
    }
}

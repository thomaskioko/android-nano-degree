package com.thomaskioko.sunshine.util;

import com.thomaskioko.sunshine.data.WeatherContract;

/**
 * This class contains constants used across the app.
 *
 * @author Thomas Kioko
 */
public class AppConstants {
    /**
     * Notification ID
     */
    public static final int NOTIFICATION_ID = 100;
    /**
     * GCM Token
     */
    public static final String GCM_SENDER_ID = "PUT_SENDER_ID_HERE";
    /**
     * Gcm Token broadcast receiver intent filter tag
     */
    public static final String GCM_TOKEN_KEY = "sentTokenToServer";
    /**
     * Gcm registration token broadcast receiver intent filter tag
     */
    public static final String GCM_REGISTRATION_COMPLETE = "registrationComplete";
    /**
     * Intent Bundle Key
     */
    public static final String BUNDLE_KEY = "key";
    /**
     * Intent Bundle Value
     */
    public static final String BUNDLE_REGISTER_KEY = "register";

    /**
     * Columns array
     */
    public static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    /**
     * These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
     * must change.
     */
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;
    public static final int COL_WEATHER_CONDITION_ID = 6;
    public static final int COL_COORD_LAT = 7;
    public static final int COL_COORD_LONG = 8;
}

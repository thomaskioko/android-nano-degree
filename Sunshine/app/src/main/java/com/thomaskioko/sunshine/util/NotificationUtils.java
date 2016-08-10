package com.thomaskioko.sunshine.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.thomaskioko.sunshine.MainActivity;
import com.thomaskioko.sunshine.R;
import com.thomaskioko.sunshine.data.WeatherContract;

import java.util.concurrent.ExecutionException;

/**
 * This class handles notification functions.
 *
 * @author Thomas Kioko
 */
public class NotificationUtils {

    private Context mContext;

    private static final String[] NOTIFY_WEATHER_PROJECTION = new String[]{
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC
    };
    // these indices must match the projection
    private static final int INDEX_WEATHER_ID = 0;
    private static final int INDEX_MAX_TEMP = 1;
    private static final int INDEX_MIN_TEMP = 2;
    private static final int INDEX_SHORT_DESC = 3;

    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int WEATHER_NOTIFICATION_ID = 3004;
    private static final String LOG_TAG = NotificationUtils.class.getSimpleName();

    /**
     * Constructor
     *
     * @param context {@link Context}
     */
    public NotificationUtils(Context context) {
        mContext = context;
    }

    public void notifyWeather() {

        //checking the last update and notify if it' the first of the day
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String displayNotificationsKey = mContext.getString(R.string.pref_enable_notifications_key);
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(mContext.getString(R.string.pref_enable_notifications_default)));

        if (displayNotifications) {

            SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
            String mLocation = mSharedPreferences.getString(mContext.getString(R.string.pref_key_location),
                    mContext.getString(R.string.pref_default_value_location));
            String lastNotificationKey = mContext.getString(R.string.pref_last_notification);
            long lastSync = prefs.getLong(lastNotificationKey, 0);

            if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {
                // Last sync was more than 1 day ago, let's send a notification with the weather.
                Uri weatherUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(mLocation, System.currentTimeMillis());

                // we'll query our contentProvider, as always
                Cursor cursor = mContext.getContentResolver().query(weatherUri, NOTIFY_WEATHER_PROJECTION, null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    int weatherId = cursor.getInt(INDEX_WEATHER_ID);
                    double high = cursor.getDouble(INDEX_MAX_TEMP);
                    double low = cursor.getDouble(INDEX_MIN_TEMP);
                    String desc = cursor.getString(INDEX_SHORT_DESC);

                    int iconId = StringUtils.getIconResourceForWeatherCondition(weatherId);
                    Resources resources = mContext.getResources();
                    int artResourceId = StringUtils.getArtResourceForWeatherCondition(weatherId);
                    String artUrl = StringUtils.getArtUrlForWeatherCondition(mContext, weatherId);

                    int largeIconWidth = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                            ? resources.getDimensionPixelSize(android.R.dimen.notification_large_icon_width)
                            : resources.getDimensionPixelSize(R.dimen.notification_large_icon_default);

                    int largeIconHeight = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                            ? resources.getDimensionPixelSize(android.R.dimen.notification_large_icon_width)
                            : resources.getDimensionPixelSize(R.dimen.notification_large_icon_default);

                    Bitmap largeIcon;
                    try {
                        largeIcon = Glide.with(mContext)
                                .load(artUrl)
                                .asBitmap()
                                .error(artResourceId)
                                .into(largeIconWidth, largeIconHeight)
                                .get();
                    } catch (InterruptedException | ExecutionException e) {
                        Log.e(LOG_TAG, e.getLocalizedMessage());
                        largeIcon = BitmapFactory.decodeResource(resources, artResourceId);
                    }
                    String title = mContext.getString(R.string.app_name);

                    // Define the text of the forecast.
                    String contentText = String.format(mContext.getString(R.string.format_notification),
                            desc,
                            StringUtils.formatTemperature(mContext, high),
                            StringUtils.formatTemperature(mContext, low));

                    // NotificationCompatBuilder is a very convenient way to build backward-compatible
                    // notifications.  Just throw in some data.
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(mContext)
                                    .setColor(resources.getColor(R.color.sunshine_light_blue))
                                    .setSmallIcon(iconId)
                                    .setLargeIcon(largeIcon)
                                    .setContentTitle(title)
                                    .setContentText(contentText);

                    // Make something interesting happen when the user clicks on the notification.
                    // In this case, opening the app is sufficient.
                    Intent resultIntent = new Intent(mContext, MainActivity.class);

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    mBuilder.setAutoCancel(true);

                    NotificationManager mNotificationManager =
                            (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
                    mNotificationManager.notify(WEATHER_NOTIFICATION_ID, mBuilder.build());

                    //refreshing last sync
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong(lastNotificationKey, System.currentTimeMillis());
                    editor.apply();
                    cursor.close();
                }
            }
        }
    }
}

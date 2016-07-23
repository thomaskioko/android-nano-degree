package com.thomaskioko.sunshine.data.tasks;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.thomaskioko.sunshine.data.WeatherContract;
import com.thomaskioko.sunshine.net.HttpHelper;

public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

    private final Context mContext;
    private String mLocation;

    /**
     * Constructor
     *
     * @param context  Application context
     * @param location Set location
     */
    public FetchWeatherTask(Context context, String location) {
        mContext = context;
        mLocation = location;
    }

    @Override
    protected String[] doInBackground(String... params) {

        HttpHelper httpHelper = new HttpHelper(mContext);

        return httpHelper.getWeatherForecast(params[0], params[1]);
    }

    @Override
    protected void onPostExecute(String[] result) {

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, System.currentTimeMillis());

        Cursor cursor = mContext.getContentResolver().query(weatherForLocationUri,
                null, null, null, sortOrder);

        new ForecastAdapter(mContext, cursor, 0);
    }
}
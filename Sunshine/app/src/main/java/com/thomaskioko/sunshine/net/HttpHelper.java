package com.thomaskioko.sunshine.net;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.thomaskioko.sunshine.BuildConfig;
import com.thomaskioko.sunshine.util.SharedPrefsManager;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class handles HTTP calls and parsing the JSON result from the server.
 *
 * @author Thomas Kioko
 */
public class HttpHelper {

    private Context mContext;
    private static final String LOG_TAG = HttpHelper.class.getSimpleName();

    /**
     * Constructor
     *
     * @param context Application context.
     */
    public HttpHelper(Context context) {
        mContext = context;
    }

    /**
     * Method to fetch and parse JSON data.
     *
     * @param location Preferred location.
     * @param unit     Unit of temperature measure
     * @return {@link java.util.ArrayList} Array of Weather forcasts for the week.
     */
    public String[] getWeatherForecast(String location, String unit) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String[] forecastResult = null;
        int numberOfDays = 14;
        String format = "json";
        try {

            final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
            final String QUERY_PARAM = "q";
            final String FORMAT_PARAM = "mode";
            final String UNITS_PARAM = "units";
            final String APP_ID = "appid";
            final String DAYS_PARAM = "cnt";

            Uri uri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, location)
                    .appendQueryParameter(UNITS_PARAM, unit)
                    .appendQueryParameter(DAYS_PARAM, String.valueOf(numberOfDays))
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(APP_ID, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                    .build();

            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL(uri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                SharedPrefsManager.setLocationStatus(mContext, SharedPrefsManager.LOCATION_STATUS_SERVER_DOWN);
                return null;
            }
            JsonParser jsonParser = new JsonParser(mContext);

            forecastResult = jsonParser.getWeatherDataFromJson(buffer.toString(), location);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            SharedPrefsManager.setLocationStatus(mContext, SharedPrefsManager.LOCATION_STATUS_SERVER_DOWN);
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            SharedPrefsManager.setLocationStatus(mContext, SharedPrefsManager.LOCATION_STATUS_SERVER_INVALID);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return forecastResult;
    }
}

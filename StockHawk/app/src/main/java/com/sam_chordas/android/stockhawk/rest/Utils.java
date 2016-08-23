package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by sam_chordas on 10/8/15.
 * Updated by Thomas Kioko
 */
public class Utils {

    private static String LOG_TAG = Utils.class.getSimpleName();
    public static boolean showPercent = true;

    /**
     * @param jsonString Json String
     * @return Array list containing stock data
     */
    public static ArrayList quoteJsonToContentVals(String jsonString) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
        JSONObject jsonObject;
        JSONArray resultsArray;
        try {
            jsonObject = new JSONObject(jsonString);
            if (jsonObject.length() != 0) {
                jsonObject = jsonObject.getJSONObject("query");
                int count = Integer.parseInt(jsonObject.getString("count"));

                if (count == 1) {
                    jsonObject = jsonObject.getJSONObject("results").getJSONObject("quote");

                    //Invalid Stock Symbol
                    if (jsonObject.getString("Bid") == null || jsonObject.getString("Bid").equals("null")) {
                        Log.e(LOG_TAG, "@quoteJsonToContentVals Invalid Stock Symbol : " + jsonObject.getString("symbol"));
                        return null;

                    } else {
                        //Valid Stock Symbol save the data
                        batchOperations.add(buildBatchOperation(jsonObject));
                    }
                } else {
                    resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");

                    if (resultsArray != null && resultsArray.length() != 0) {
                        for (int i = 0; i < resultsArray.length(); i++) {
                            jsonObject = resultsArray.getJSONObject(i);
                            batchOperations.add(buildBatchOperation(jsonObject));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "@quoteJsonToContentVals: String to JSON failed: " + e);
        }
        return batchOperations;
    }

    /**
     * Helper method to truncate bid price
     *
     * @param bidPrice Raw bid price string
     * @return Bid Price
     */
    public static String truncateBidPrice(String bidPrice) {
        bidPrice = String.format(Locale.getDefault(), "%.2f", Float.parseFloat(bidPrice));
        return bidPrice;
    }

    /**
     * Helper method to truncate change in stock
     *
     * @param change          Raw string.
     * @param isPercentChange {@link Boolean}
     * @return Change in stock
     */
    public static String truncateChange(String change, boolean isPercentChange) {
        if (change != null) {
            String weight = change.substring(0, 1);
            String ampersand = "";

            if (isPercentChange) {
                ampersand = change.substring(change.length() - 1, change.length());
                change = change.substring(0, change.length() - 1);
            }
            change = change.substring(1, change.length());
            try {
                double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
                change = String.format(Locale.getDefault(), "%.2f", round);
                StringBuilder changeBuffer = new StringBuilder(change);
                changeBuffer.insert(0, weight);
                changeBuffer.append(ampersand);
                change = changeBuffer.toString();
            } catch (NumberFormatException numberFormatException) {
                Log.e(LOG_TAG, "@truncateChange:: Error:: " + weight + " -- " + numberFormatException);
            }
        }
        return change;
    }

    /**
     * @param jsonObject Json Object
     * @return {@link ContentProviderOperation}
     */
    public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                QuoteProvider.Quotes.CONTENT_URI);
        try {
            String change = jsonObject.getString("Change");
            builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol"));
            builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
            builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(jsonObject.getString("ChangeinPercent"), true));
            builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
            builder.withValue(QuoteColumns.ISCURRENT, 1);
            if (change.charAt(0) == '-') {
                builder.withValue(QuoteColumns.ISUP, 0);
            } else {
                builder.withValue(QuoteColumns.ISUP, 1);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "@buildBatchOperation Json Exception " + e);
        }
        return builder.build();
    }

    /**
     * Checks if network is available or not.
     *
     * @param context Context in which the method is called.
     * @return {@link Boolean} True/False
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Helper method to format date
     *
     * @param inputDate Raw date format
     * @return Formatted date
     */
    public static String convertDate(String inputDate) {
        return inputDate.substring(6) + "/" + inputDate.substring(4, 6) + "/" + inputDate.substring(2, 4);
    }
}

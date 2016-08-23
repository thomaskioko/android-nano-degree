package com.sam_chordas.android.stockhawk.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;

import com.sam_chordas.android.stockhawk.interfaces.HistoricalDataCallback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * @author Thomas Kioko
 */
public class HistoricData {
    private final static String TAG = HistoricData.class.getSimpleName();
    public final static String HISTORICAL_DATA_STATUS = "historical_data_status";
    public HistoricalDataCallback historicalDataCallback;
    public ArrayList<StockSymbol> stockSymbols;
    public StockMeta stockMeta;
    public Context context;

    final String BASE_URL = "http://chartapi.finance.yahoo.com/instrument/1.0/";
    final String END_URL = "/chartdata;type=quote;range=1y/json";

    private static final String JSON_SERIES = "series";
    private static final String JSON_DATE = "Date";
    private static final String JSON_CLOSE = "close";

    private static final String JSON_META = "meta";
    private static final String JSON_COMPANY_NAME = "Company-Name";
    private static final String JSON_EXCHANGE_NAME = "Exchange-Name";
    private static final String JSON_FIRST_TRADE = "first-trade";
    private static final String JSON_LAST_TRADE = "last-trade";
    private static final String JSON_CURRENCY = "currency";
    private static final String JSON_CLOSE_PRICE = "previous_close_price";

    //to indicate errors encountered.
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_OK, STATUS_ERROR_JSON, STATUS_ERROR_NO_NETWORK, STATUS_ERROR_PARSE
            , STATUS_ERROR_SERVER, STATUS_ERROR_UNKNOWN})
    public @interface HistoricalDataStatuses {
    }

    public static final int STATUS_OK = 0;
    public static final int STATUS_ERROR_JSON = 1;
    public static final int STATUS_ERROR_SERVER = 2;
    public static final int STATUS_ERROR_PARSE = 3;
    public static final int STATUS_ERROR_NO_NETWORK = 4;
    public static final int STATUS_ERROR_UNKNOWN = 5;

    /**
     * @param context                Context in which the application is called
     * @param historicalDataCallback Callback interface instance
     */
    public HistoricData(Context context, HistoricalDataCallback historicalDataCallback) {
        this.context = context;
        this.historicalDataCallback = historicalDataCallback;
        this.stockSymbols = new ArrayList<>();
    }

    /**
     * @param status Status from the server
     */
    public void setHistoricalDataStatus(@HistoricalDataStatuses int status) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(HISTORICAL_DATA_STATUS, status);
        editor.commit();
    }

    private String fetchData(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    /**
     * Helper method to get stock historical data using the symbol passed.
     *
     * @param symbol Stock symbol
     */
    public void getHistoricData(String symbol) {
        final String url = BASE_URL + symbol + END_URL;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    String json = fetchData(url);
                    json = json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1);
                    JSONObject mainObject = new JSONObject(json);

                    JSONArray series_data = mainObject.getJSONArray(JSON_SERIES);
                    for (int i = 0; i < series_data.length(); i++) {
                        JSONObject singleObject = series_data.getJSONObject(i);
                        String date = singleObject.getString(JSON_DATE);
                        double close = singleObject.getDouble(JSON_CLOSE);
                        stockSymbols.add(new StockSymbol(date, close));
                    }

                    //Parse data
                    JSONObject meta_data = mainObject.getJSONObject(JSON_META);
                    String companyName = meta_data.getString(JSON_COMPANY_NAME);
                    String exchangeName = meta_data.getString(JSON_EXCHANGE_NAME);
                    String firstTrade = meta_data.getString(JSON_FIRST_TRADE);
                    String lastTrade = meta_data.getString(JSON_LAST_TRADE);
                    String currency = meta_data.getString(JSON_CURRENCY);
                    double previousClosePrice = meta_data.getDouble(JSON_CLOSE_PRICE);

                    //Create an instance of StockMeta object and pass data to in
                    stockMeta = new StockMeta(companyName, exchangeName, firstTrade, lastTrade,
                            currency, previousClosePrice, stockSymbols);

                    if (historicalDataCallback != null) {
                        setHistoricalDataStatus(STATUS_OK);
                    }

                } catch (IOException e) {
                    setHistoricalDataStatus(STATUS_ERROR_SERVER);

                } catch (JSONException e) {
                    setHistoricalDataStatus(STATUS_ERROR_JSON);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (historicalDataCallback != null) {
                    @HistoricData.HistoricalDataStatuses
                    int status = PreferenceManager.getDefaultSharedPreferences(context)
                            .getInt(HistoricData.HISTORICAL_DATA_STATUS, -1);
                    if (status == STATUS_OK) {
                        historicalDataCallback.onSuccess(stockMeta);
                    } else {
                        historicalDataCallback.onFailure();
                    }
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}

package com.thomaskioko.sunshine.watchface;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

public class WearableDataService extends WearableListenerService implements
        DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    protected static final String DATA_PATH = "/sunshine-watchface/data";

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }    }

    @Override
    public void onConnected(@Nullable Bundle connectionHint) {
        Log.d(SunshineWatchFaceService.TAG, "onConnected: " + connectionHint);
        Wearable.DataApi.addListener(mGoogleApiClient, this);

    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(SunshineWatchFaceService.TAG, "onConnectionSuspended: " + cause);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.d(SunshineWatchFaceService.TAG, "Reveived!!:" + dataEventBuffer);

        for (DataEvent dataEvent : dataEventBuffer) {
            if (dataEvent.getType() != DataEvent.TYPE_CHANGED) {
                continue;
            }

            DataItem dataItem = dataEvent.getDataItem();
            if (!dataItem.getUri().getPath().equals(
                    DATA_PATH)) {
                continue;
            }

            DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);
            DataMap dataMap = dataMapItem.getDataMap();
            Log.d(SunshineWatchFaceService.TAG, "Data item updated: " + dataMap);
            SunshineWatchFaceService.HIGH_TEMP_DATA = dataMap.getString("HIGH");
            SunshineWatchFaceService.LOW_TEMP_DATA = dataMap.getString("LOW");
            SunshineWatchFaceService.WEATHER_ID_DATA = dataMap.getInt("WEATHER_ID_DATA");
            Log.d(SunshineWatchFaceService.TAG, "new weather id:" + SunshineWatchFaceService.WEATHER_ID_DATA);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(SunshineWatchFaceService.TAG, "onConnectionFailed: " + connectionResult);
    }
}
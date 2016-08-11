package com.thomaskioko.sunshine.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.thomaskioko.sunshine.util.AppConstants;

/**
 * @author Thomas Kioko
 */
public class GcmIntentService extends IntentService {

    private static final String TAG = GcmIntentService.class.getSimpleName();

    public GcmIntentService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String key = intent.getStringExtra(AppConstants.BUNDLE_KEY);
        if (key != null) {
            switch (key) {
                case AppConstants.BUNDLE_REGISTER_KEY:
                    registerGCM();
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * Registering with GCM and obtaining the gcm registration id
     */
    private void registerGCM() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String token = null;

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            token = instanceID.getToken(AppConstants.GCM_SENDER_ID,
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i(TAG, "@registerGCM GCM Registration Token: " + token);

            sharedPreferences.edit().putBoolean(AppConstants.GCM_TOKEN_KEY, true).apply();
        } catch (Exception e) {
            Log.i(TAG, "Failed to complete token refresh" + e);
            sharedPreferences.edit().putBoolean(AppConstants.GCM_TOKEN_KEY, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(AppConstants.GCM_REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

}

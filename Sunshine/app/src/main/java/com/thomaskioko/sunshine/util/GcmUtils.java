package com.thomaskioko.sunshine.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.thomaskioko.sunshine.MainActivity;
import com.thomaskioko.sunshine.services.GcmIntentService;

/**
 * This class checks whether the device supports GCM and handles GCM Registration
 *
 * @author Thomas Kioko
 * @version Version 1.0
 */
public class GcmUtils {

    private Context mContext;
    private MainActivity mActivationActivity;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String LOG_TAG = GcmUtils.class.getSimpleName();

    /**
     * Default Constructor
     *
     * @param context Application context.
     */
    public GcmUtils(Context context) {
        mContext = context;
    }

    /**
     * Default Constructor
     *
     * @param activationActivity ActivationActivity class
     * @param context            Application context.
     */
    public GcmUtils(MainActivity activationActivity, Context context) {
        mContext = context;
        mActivationActivity = activationActivity;
    }

    /**
     * Invoke {@link GcmIntentService} to get GCM token
     */
    public void registerGCM() {
        Intent intent = new Intent(mContext, GcmIntentService.class);
        intent.putExtra(AppConstants.BUNDLE_KEY, AppConstants.BUNDLE_REGISTER_KEY);
        mContext.startService(intent);
    }

    /**
     * Check if device supports Play Services
     *
     * @return True/False
     */
    public boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(mActivationActivity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(LOG_TAG,
                        "@checkPlayServices: This device is not supported. Google Play Services not installed!");
            }
            return false;
        }
        return true;
    }
}

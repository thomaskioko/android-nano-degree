package com.thomaskioko.sunshine.util;

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
}

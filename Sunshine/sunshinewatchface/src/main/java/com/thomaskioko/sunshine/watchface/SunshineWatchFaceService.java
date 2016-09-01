package com.thomaskioko.sunshine.watchface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowInsets;


import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Digital watch face with seconds. In ambient mode, the seconds aren't displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 */
public class SunshineWatchFaceService extends CanvasWatchFaceService {
    private static final Typeface BOLD_TYPEFACE =
            Typeface.create("thin", Typeface.BOLD);
    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create("thin", Typeface.NORMAL);
    /**
     * Update rate in milliseconds for interactive mode. We update once a second since seconds are
     * displayed in interactive mode.
     */
    protected static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(5);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;
    protected static final String TAG = "SUNSHINE_WATCHFACE";
    public static String HIGH_TEMP_DATA = "26°";
    public static String LOW_TEMP_DATA = "22°";
    public static int WEATHER_ID_DATA = 500;

    private float mColonWidth;
    private String mColonString = ":";

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<Engine> mWeakReference;

        public EngineHandler(SunshineWatchFaceService.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            SunshineWatchFaceService.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        final Handler mUpdateTimeHandler = new EngineHandler(this);
        boolean mRegisteredTimeZoneReceiver = false;
        Paint mBackgroundPaint;
        Paint mHoursPaint;
        Paint mColonPaint;
        Paint mMinutesPaint;
        Paint mDatePaint;
        Paint mDividerPaint;
        Paint mWeatherIconPaint;
        Paint mWeatherHighTemp;
        Paint mWeatherLowTemp;
        boolean mAmbient;

        Calendar mCalendar;
        Date mDate;
        SimpleDateFormat mDayOfWeekFormat;
        java.text.DateFormat mDateFormat;


        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                mTime.clear(intent.getStringExtra("time-zone"));
//                mTime.setToNow();
                mCalendar.setTimeZone(TimeZone.getDefault());
                initFormats();
            }
        };
        float mClockXOffset;
        float mDateXOffset;
        float mXLowTempTextOffset;
        float mYClockOffset;
        float mYDateOffset;
        float mYWeatherOffset;
        float mYWeatherIconOffset;
        float mXWeatherIconPaintOffset;
        float mYDividerOffsetToCenter;
        float mDividerLength;
        float mDividerThickness;

        private void initFormats() {
            mDayOfWeekFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            mDayOfWeekFormat.setCalendar(mCalendar);
            mDateFormat = DateFormat.getDateFormat(SunshineWatchFaceService.this);
            mDateFormat.setCalendar(mCalendar);
        }

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(SunshineWatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());
            Resources resources = SunshineWatchFaceService.this.getResources();
            mYClockOffset = resources.getDimension(R.dimen.clock_y_offset);
            mYDateOffset = resources.getDimension(R.dimen.date_y_offset);
            mYWeatherOffset = resources.getDimension(R.dimen.weather_y_offset);
            mYWeatherIconOffset = resources.getDimension(R.dimen.weather_icon_y_offset);
            mYDividerOffsetToCenter = resources.getDimension(R.dimen.divider_y_offset_to_center);
            mDividerLength = resources.getDimension(R.dimen.divider_length);
            mDividerThickness = resources.getDimension(R.dimen.divider_thickness);

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(resources.getColor(R.color.background));

            mHoursPaint = new Paint();
            mColonPaint = new Paint();
            mMinutesPaint = new Paint();
            mHoursPaint = createTextPaint(resources.getColor(R.color.clock_text), BOLD_TYPEFACE);
            mColonPaint = createTextPaint(resources.getColor(R.color.clock_text), NORMAL_TYPEFACE);
            mMinutesPaint = createTextPaint(resources.getColor(R.color.clock_text), NORMAL_TYPEFACE);
            mDatePaint = createTextPaint(resources.getColor(R.color.date_text), NORMAL_TYPEFACE);
            mWeatherHighTemp = createTextPaint(resources.getColor(R.color.date_text), BOLD_TYPEFACE);
            mWeatherLowTemp = createTextPaint(resources.getColor(R.color.date_text), NORMAL_TYPEFACE);
            mDividerPaint = new Paint();
            mDividerPaint.setColor(resources.getColor(R.color.divider));
            mWeatherIconPaint = new Paint();
            mCalendar = Calendar.getInstance();
            mDate = new Date();
            initFormats();
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        private Paint createTextPaint(int textColor, Typeface tf) {
            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTypeface(tf);
            paint.setAntiAlias(true);
            return paint;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
//                mTime.clear(TimeZone.getDefault().getID());
//                mTime.setToNow();
            } else {
                unregisterReceiver();
//                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//                    Wearable.DataApi.removeListener(mGoogleApiClient, this);
//                    mGoogleApiClient.disconnect();
//                }
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            SunshineWatchFaceService.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            SunshineWatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            // Load resources that have alternate values for round watches.
            Resources resources = SunshineWatchFaceService.this.getResources();
            boolean isRound = insets.isRound();
            mClockXOffset = resources.getDimension(R.dimen.clock_x_offset);
            mDateXOffset = resources.getDimension(R.dimen.date_x_offset);
            mXLowTempTextOffset = resources.getDimension(R.dimen.low_temp_x_offset);
            mXWeatherIconPaintOffset = resources.getDimension(R.dimen.weather_icon_x_offset);
            float textSize = resources.getDimension(R.dimen.clock_text_size);
            float dateTextSize = resources.getDimension(R.dimen.date_text_size);
            float weatherTempTextSize = resources.getDimension(R.dimen.temp_text_size);

            mHoursPaint.setTextSize(textSize);
            mColonPaint.setTextSize(textSize);
            mMinutesPaint.setTextSize(textSize);
            mDatePaint.setTextSize(dateTextSize);
            mWeatherHighTemp.setTextSize(weatherTempTextSize);
            mWeatherLowTemp.setTextSize(weatherTempTextSize);

            mColonWidth = mColonPaint.measureText(mColonString);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                if (mLowBitAmbient) {
//                    mTextPaint.setAntiAlias(!inAmbientMode);
                    mHoursPaint.setAntiAlias(!inAmbientMode);
                    mColonPaint.setAntiAlias(!inAmbientMode);
                    mMinutesPaint.setAntiAlias(!inAmbientMode);
                    mDatePaint.setAntiAlias(!inAmbientMode);
                    mDividerPaint.setAntiAlias(!inAmbientMode);
                    mWeatherIconPaint.setAntiAlias(!inAmbientMode);
                    mWeatherHighTemp.setAntiAlias(!inAmbientMode);
                    mWeatherLowTemp.setAntiAlias(!inAmbientMode);
                }
                invalidate();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            // Draw the background.
            if (isInAmbientMode()) {
                canvas.drawColor(Color.BLACK);
            } else {
                canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
            }

            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);
            mDate.setTime(now);

            String hourString;
            hourString = formatTwoDigitNumber(mCalendar.get(Calendar.HOUR_OF_DAY));
            float x = bounds.width() / 2 - mHoursPaint.measureText(hourString) - mClockXOffset;

            canvas.drawText(hourString, x, mYClockOffset, mHoursPaint);

            x += mHoursPaint.measureText(hourString);

            canvas.drawText(mColonString, x, mYClockOffset, mColonPaint);

            x += mColonWidth;

            // Draw the minutes.
            String minuteString = formatTwoDigitNumber(mCalendar.get(Calendar.MINUTE));
            canvas.drawText(minuteString, x, mYClockOffset, mMinutesPaint);

            // Draw the date.
            String dateString = String.format("%s, %s %d %d",
                    mCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US).toUpperCase(),
                    mCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US).toUpperCase(),
                    mCalendar.get(Calendar.DAY_OF_MONTH),
                    mCalendar.get(Calendar.YEAR)
            );

            x = bounds.width() / 2 - mDatePaint.measureText(dateString) / 2 - mDateXOffset;

            canvas.drawText(dateString, x, mYDateOffset, mDatePaint);

            canvas.drawRoundRect(
                    new RectF(bounds.width() / 2 - mDividerLength / 2,  (bounds.height() / 2 + mYDividerOffsetToCenter) + 10,
                            bounds.width() / 2 + mDividerLength / 2,  bounds.height() / 2 + mYDividerOffsetToCenter + mDividerThickness),
                    6, 6, mDividerPaint);

            if (!isInAmbientMode()) {
                int weatherIconDrawableId = getArtResourceForWeatherCondition(
                        WEATHER_ID_DATA > 0 ? WEATHER_ID_DATA : 500);
                Bitmap weatherIconBitmap = BitmapFactory.decodeResource(getResources(),
                        weatherIconDrawableId);
                canvas.drawBitmap(weatherIconBitmap, bounds.width() / 2 - mXWeatherIconPaintOffset, mYWeatherIconOffset, mWeatherIconPaint);
            }

            String highTemp = String.valueOf(HIGH_TEMP_DATA);
            canvas.drawText(highTemp, bounds.width() / 2 - mWeatherHighTemp.measureText(highTemp) / 3, mYWeatherOffset, mWeatherHighTemp);
            x = bounds.width() / 2 + mWeatherHighTemp.measureText(highTemp) / 3 + mXLowTempTextOffset;

            String lowTemp = String.valueOf(LOW_TEMP_DATA);
            canvas.drawText(lowTemp, x, mYWeatherOffset, mWeatherLowTemp);


        }

        private String formatTwoDigitNumber(int hour) {
            return String.format("%02d", hour);
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }
    }

    /**
     * Helper method to provide the art resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     *
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getArtResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes

        Log.d(TAG, "get drawable from weather id:" + weatherId);
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }

}

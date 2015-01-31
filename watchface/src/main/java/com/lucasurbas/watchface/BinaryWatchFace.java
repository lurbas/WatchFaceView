package com.lucasurbas.watchface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lucas on 1/30/15.
 */
public class BinaryWatchFace extends WatchFace {

    static final String TAG = BinaryWatchFace.class.getSimpleName();

    /**
     * Update rate in milliseconds for interactive mode. We update once a second to advance the
     * second hand.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    static final int MSG_UPDATE_TIME = 0;

    Paint backgroundPaint;
    Paint activePrimaryDotPaint;
    Paint activeSecondaryDotPaint;
    Paint disabledDotPaint;
    Paint cardProtectionPaint;

    int regularBackgroundColor;
    int regularActivePrimaryColor;
    int regularActiveSecondaryColor;
    int regularDisabledColor;

    int ambientBackgroundColor;
    int ambientActivePrimaryColor;
    int ambientActiveSecondaryColor;
    int ambientDisabledColor;

    int shadowColor;

    RectF innerBounds;


    boolean firstDraw = true;
    Time mTime;

    /**
     * Handler to update the time once a second in interactive mode.
     */
    final Handler mUpdateTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case MSG_UPDATE_TIME:
                    if (Log.isLoggable(TAG, Log.VERBOSE)) {
                        Log.v(TAG, "updating time");
                    }
                    invalidate();
                    if (shouldTimerBeRunning()) {
                        long timeMs = System.currentTimeMillis();
                        long delayMs = INTERACTIVE_UPDATE_RATE_MS
                                - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                        mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                    }
                    break;
            }
        }
    };

    final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mTime.clear(intent.getStringExtra("time-zone"));
            mTime.setToNow();
        }
    };
    boolean mRegisteredTimeZoneReceiver;

    public BinaryWatchFace(){
        super();
    }

    @Override
    public void onCreate(Resources resources) {
        super.onCreate(resources);

        regularBackgroundColor = resources.getColor(R.color.jelly_bean);
        regularActivePrimaryColor = resources.getColor(R.color.concrete);
        regularActiveSecondaryColor = resources.getColor(R.color.hippie_blue);
        regularDisabledColor = resources.getColor(R.color.blumine);
        ambientBackgroundColor = Color.BLACK;
        ambientActivePrimaryColor = Color.WHITE;
        ambientActiveSecondaryColor = resources.getColor(R.color.gray);
        ambientDisabledColor = resources.getColor(R.color.mine_shaft);
        shadowColor = resources.getColor(R.color.shadow);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(regularBackgroundColor);

        activePrimaryDotPaint = new Paint();
        activePrimaryDotPaint.setColor(regularActivePrimaryColor);
        activePrimaryDotPaint.setStrokeWidth(1.5f);
        activePrimaryDotPaint.setStyle(Paint.Style.FILL);
        activePrimaryDotPaint.setAntiAlias(true);

        activeSecondaryDotPaint = new Paint();
        activeSecondaryDotPaint.setColor(regularActiveSecondaryColor);
        activeSecondaryDotPaint.setAntiAlias(true);

        disabledDotPaint = new Paint();
        disabledDotPaint.setColor(regularDisabledColor);
        disabledDotPaint.setStrokeWidth(1.5f);
        disabledDotPaint.setStyle(Paint.Style.FILL);
        disabledDotPaint.setAntiAlias(true);

        cardProtectionPaint = new Paint();

        innerBounds = new RectF();

        mTime = new Time();

        mRegisteredTimeZoneReceiver = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
    }

    @Override
    public void updateState() {

        if (isLowBitAmbient()) {
            activePrimaryDotPaint.setAntiAlias(!isInAmbientMode());
            activeSecondaryDotPaint.setAntiAlias(!isInAmbientMode());
            disabledDotPaint.setAntiAlias(!isInAmbientMode());
        }

        if (isInAmbientMode()) {

            backgroundPaint.setColor(ambientBackgroundColor);
            activePrimaryDotPaint.setColor(ambientActivePrimaryColor);

            if (isBurnInProtection()) {
                activeSecondaryDotPaint.setColor(ambientBackgroundColor);

                disabledDotPaint.setColor(ambientBackgroundColor);
                activePrimaryDotPaint.setStyle(Paint.Style.STROKE);

            } else if (isLowBitAmbient()) {
                activeSecondaryDotPaint.setColor(ambientActivePrimaryColor);
                activePrimaryDotPaint.setStyle(Paint.Style.FILL);

                disabledDotPaint.setColor(ambientActivePrimaryColor);
                disabledDotPaint.setStyle(Paint.Style.STROKE);

            } else {
                activeSecondaryDotPaint.setColor(ambientActiveSecondaryColor);

                activePrimaryDotPaint.setStyle(Paint.Style.FILL);
                disabledDotPaint.setStyle(Paint.Style.FILL);
                disabledDotPaint.setColor(ambientDisabledColor);
            }
        } else {

            backgroundPaint.setColor(regularBackgroundColor);
            activePrimaryDotPaint.setColor(regularActivePrimaryColor);
            activePrimaryDotPaint.setStyle(Paint.Style.FILL);
            activeSecondaryDotPaint.setColor(regularActiveSecondaryColor);
            disabledDotPaint.setColor(regularDisabledColor);
            disabledDotPaint.setStyle(Paint.Style.FILL);
        }

        activePrimaryDotPaint.setAlpha(isInMuteMode() && !isInAmbientMode() ? 100 : 255);
        activeSecondaryDotPaint.setAlpha(isInMuteMode() && !isInAmbientMode() ? 100 : 255);
        disabledDotPaint.setAlpha(isInMuteMode() && !isInAmbientMode() ? 100 : 255);

        // Whether the timer should be running depends on whether we're in ambient mode (as well
        // as whether we're visible), so we may need to start or stop the timer.
        updateTimer();
    }

    @Override
    public void onDraw(Canvas canvas, Rect bounds) {

        int width = bounds.width();
        int height = bounds.height();
        final float UNIT = (width <= height ? width : height) / 240f;
        final float BORDER_WIDTH = 1f * UNIT;
        innerBounds.set((width / 2f) - (81f * UNIT), (height / 2f) + (53f * UNIT), (width / 2f) + (81f * UNIT), (height / 2f) - (53f * UNIT));

        //if (firstDraw) {
        //    firstDraw = false;
        activePrimaryDotPaint.setShadowLayer(2f * UNIT, 0, 1f * UNIT, shadowColor);
        activeSecondaryDotPaint.setShadowLayer(2f * UNIT, 0, 1f * UNIT, shadowColor);
        //}
        // Draw the background, scaled to fit.
        canvas.drawRect(bounds, backgroundPaint);

        mTime.setToNow();

        boolean[] secondsBinary = toBinary(mTime.second, 6);
        boolean[] minutesBinary = toBinary(mTime.minute, 6);
        boolean[] hoursBinary = toBinary(mTime.hour, 6);
        boolean[] daysBinary = toBinary(mTime.monthDay, 6);

        for (int x = 1; x <= 6; x++) {
            for (int y = 1; y <= 4; y++) {
                Paint paint;
                switch (y) {
                    case 1:
                        paint = daysBinary[x - 1] && !(isBurnInProtection() && isInAmbientMode()) ? activeSecondaryDotPaint : disabledDotPaint;
                        break;
                    case 2:
                        paint = hoursBinary[x - 1] ? activePrimaryDotPaint : disabledDotPaint;
                        break;
                    case 3:
                        paint = minutesBinary[x - 1] ? activePrimaryDotPaint : disabledDotPaint;
                        break;
                    case 4:
                        paint = secondsBinary[x - 1] && !isInAmbientMode() ? activeSecondaryDotPaint : disabledDotPaint;
                        break;
                    default:
                        paint = disabledDotPaint;
                        break;
                }
                canvas.drawCircle(innerBounds.left - (17f * UNIT) + (22f * UNIT * x) + (6f * UNIT * (x)), innerBounds.bottom - (17f * UNIT) + (22f * UNIT * y) + (6f * UNIT * (y)), (11f * UNIT), paint);
            }
        }

        if (isInAmbientMode()) {
            // Fill area under card.
            cardProtectionPaint.setColor(Color.BLACK);
            Rect cardBounds = getCardBounds();
            canvas.drawRect(cardBounds, cardProtectionPaint);

            // Draw border around card in interactive mode.
            cardProtectionPaint.setColor(isLowBitAmbient() ? ambientActivePrimaryColor : ambientActiveSecondaryColor);
            canvas.drawRect(cardBounds.left,
                    cardBounds.top,
                    cardBounds.right,
                    cardBounds.top - BORDER_WIDTH, cardProtectionPaint);
        }
    }

    @Override
    public void onVisibilityChanged(Context context, boolean visible) {
        super.onVisibilityChanged(context, visible);

        if (visible) {
            registerReceiver(context);

            // Update time zone in case it changed while we weren't visible.
            mTime.clear(TimeZone.getDefault().getID());
            mTime.setToNow();
        } else {
            unregisterReceiver(context);
        }

        // Whether the timer should be running depends on whether we're visible (as well as
        // whether we're in ambient mode), so we may need to start or stop the timer.
        updateTimer();
    }

    private void registerReceiver(Context context) {
        if (mRegisteredTimeZoneReceiver) {
            return;
        }
        mRegisteredTimeZoneReceiver = true;
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
        context.registerReceiver(mTimeZoneReceiver, filter);
    }

    private void unregisterReceiver(Context context) {
        if (!mRegisteredTimeZoneReceiver) {
            return;
        }
        mRegisteredTimeZoneReceiver = false;
        context.unregisterReceiver(mTimeZoneReceiver);
    }

    /**
     * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
     * or stops it if it shouldn't be running but currently is.
     */
    private void updateTimer() {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "updateTimer");
        }
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

    private boolean[] toBinary(int number, int base) {
        final boolean[] ret = new boolean[base];
        for (int i = 0; i < base; i++) {
            ret[base - 1 - i] = (1 << i & number) != 0;
        }
        return ret;
    }
}

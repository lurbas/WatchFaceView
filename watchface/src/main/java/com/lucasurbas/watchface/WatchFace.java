package com.lucasurbas.watchface;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.wearable.watchface.WatchFaceService;
import android.util.Log;

/**
 * Created by Lucas on 1/30/15.
 */
public abstract class WatchFace {

    private static final String TAG = WatchFace.class.getSimpleName();
    private static final boolean LOG = true;

    private OnInvalidateListener invalidateListener;
    /**
     * Whether the display supports fewer bits for each color in ambient mode. When true, we
     * disable anti-aliasing in ambient mode.
     */
    private boolean isInAmbientMode;
    private boolean isLowBitAmbient;
    private boolean isBurnInProtection;
    private boolean isInMuteMode;
    private boolean isVisible;
    private boolean isRound;
    private int chinSize;
    private Rect cardBounds;

    public interface OnInvalidateListener {
        public void onInvalidate();
    }

    public boolean isInAmbientMode() {
        return isInAmbientMode;
    }

    public void setInAmbientMode(boolean isInAmbientMode) {
        this.isInAmbientMode = isInAmbientMode;
    }

    public boolean isLowBitAmbient() {
        return isLowBitAmbient;
    }

    public void setLowBitAmbient(boolean isLowBitAmbient) {
        this.isLowBitAmbient = isLowBitAmbient;
    }

    public boolean isBurnInProtection() {
        return isBurnInProtection;
    }

    public void setBurnInProtection(boolean isBurnInProtection) {
        this.isBurnInProtection = isBurnInProtection;
    }

    public boolean isInMuteMode() {
        return isInMuteMode;
    }

    public void setInMuteMode(boolean isInMuteMode) {
        this.isInMuteMode = isInMuteMode;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean isRound() {
        return isRound;
    }

    public void setRound(boolean isRound) {
        this.isRound = isRound;
    }

    public int getChinSize() {
        return chinSize;
    }

    public void setChinSize(int chinSize) {
        this.chinSize = chinSize;
    }

    public Rect getCardBounds() {
        return cardBounds;
    }

    public void setCardBounds(Rect cardBounds) {
        this.cardBounds = cardBounds;
    }

    public void setInvalidateListener(OnInvalidateListener invalidateListener) {
        this.invalidateListener = invalidateListener;
    }

    public WatchFace() {
        this.cardBounds = new Rect();
    }

    public void onCreate(Resources resources) {
        if (LOG) {
            Log.d(TAG, "onCreate");
        }
    }

    public void invalidate() {
        if (LOG) {
            Log.d(TAG, "invalidate");
        }

        if (invalidateListener != null) {
            invalidateListener.onInvalidate();
        }
    }

    public void onDestroy() {
        if (LOG) {
            Log.d(TAG, "onDestroy");
        }
    }

    public void onApplyWindowInsets(boolean isRound, int chinSize) {
        if (LOG) {
            Log.d(TAG, "onApplyWindowInsets: Round: " + isRound());
        }

        setRound(isRound);
        setChinSize(chinSize);
        updateState();
        invalidate();
    }

    public void onPropertiesChanged(Bundle properties) {

        setLowBitAmbient(properties.getBoolean(WatchFaceService.PROPERTY_LOW_BIT_AMBIENT, isLowBitAmbient));
        setBurnInProtection(properties.getBoolean(WatchFaceService.PROPERTY_BURN_IN_PROTECTION,
                isBurnInProtection));
        updateState();
        invalidate();

        if (LOG) {
            Log.d(TAG, "onPropertiesChanged: LowBitAmbient: " + isLowBitAmbient() + ", BurnInProtection: " + isBurnInProtection());
        }
    }

    public void onTimeTick() {
        if (LOG) {
            Log.d(TAG, "onTimeTick");
        }

        invalidate();
    }

    public void onAmbientModeChanged(boolean inAmbientMode) {
        if (LOG) {
            Log.d(TAG, "onAmbientModeChanged: " + inAmbientMode);
        }

        setInAmbientMode(inAmbientMode);
        updateState();
        invalidate();
    }

    public void onInterruptionFilterChanged(int interruptionFilter) {

        setInMuteMode(interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE);
        updateState();
        invalidate();

        if (LOG) {
            Log.d(TAG, "onInterruptionFilterChanged: MuteMode:  " + isInMuteMode());
        }
    }

    public void onPeekCardPositionUpdate(Rect bounds) {
        if (LOG) {
            Log.d(TAG, "onPeekCardPositionUpdate: " + bounds);
        }

        if (!bounds.equals(cardBounds)) {
            cardBounds.set(bounds);
            invalidate();
        }
    }

    public void onVisibilityChanged(Context context, boolean visible) {
        if (LOG) {
            Log.d(TAG, "onVisibilityChanged: " + visible);
        }

        setVisible(visible);
        updateState();
        invalidate();
    }

    public abstract void updateState();

    public abstract void onDraw(Canvas canvas, Rect bounds);
}

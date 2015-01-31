package com.lucasurbas.binarywatchface;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.lucasurbas.watchface.BinaryWatchFace;
import com.lucasurbas.watchface.WatchFace;

/**
 * Created by Lucas on 12/13/14.
 */
public class BinaryWatchFaceService extends CanvasWatchFaceService {

    private static final String TAG = BinaryWatchFaceService.class.getSimpleName();

    @Override
    public Engine onCreateEngine() {
        return new BinaryWatchEngine();
    }

    public class BinaryWatchEngine extends CanvasWatchFaceService.Engine {

        BinaryWatchFace binaryWatchFace;

        public BinaryWatchEngine() {
            binaryWatchFace = new BinaryWatchFace();
        }

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            binaryWatchFace.onCreate(getResources());
            binaryWatchFace.setInvalidateListener(new WatchFace.OnInvalidateListener() {
                @Override
                public void onInvalidate() {
                    invalidate();
                }
            });

            setWatchFaceStyle(new WatchFaceStyle.Builder(BinaryWatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());
        }

        @Override
        public void onDestroy() {
            binaryWatchFace.onDestroy();
            super.onDestroy();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            binaryWatchFace.onPropertiesChanged(properties);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);
            binaryWatchFace.onApplyWindowInsets(insets.isRound(), insets.getSystemWindowInsetBottom());
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            binaryWatchFace.onTimeTick();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            binaryWatchFace.onAmbientModeChanged(inAmbientMode);
        }

        @Override
        public void onInterruptionFilterChanged(int interruptionFilter) {
            super.onInterruptionFilterChanged(interruptionFilter);
            binaryWatchFace.onInterruptionFilterChanged(interruptionFilter);
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            binaryWatchFace.onDraw(canvas, bounds);
        }

        @Override
        public void onPeekCardPositionUpdate(Rect bounds) {
            binaryWatchFace.onPeekCardPositionUpdate(bounds);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            binaryWatchFace.onVisibilityChanged(BinaryWatchFaceService.this, visible);
        }
    }
}

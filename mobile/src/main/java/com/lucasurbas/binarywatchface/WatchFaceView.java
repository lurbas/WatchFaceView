package com.lucasurbas.binarywatchface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import com.lucasurbas.watchface.WatchFace;

/**
 * Created by Lucas on 1/30/15.
 */
public class WatchFaceView extends View {

    private static final String TAG = WatchFaceView.class.getSimpleName();

    private WatchFace watchface;
    private Paint paint;

    public WatchFaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }

    public void setWatchFace(WatchFace watchface) {
        this.watchface = watchface;
        this.watchface.setInvalidateListener(new WatchFace.OnInvalidateListener() {
            @Override
            public void onInvalidate() {
                invalidate();
            }
        });
    }

//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        if (watchface != null) {
//            watchface.onVisibilityChanged(getContext(), false);
//            watchface.onDestroy();
//        }
//    }
//
//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        if (watchface != null) {
//            watchface.onCreate(getResources());
//            watchface.onVisibilityChanged(getContext(), true);
//        }
//    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (watchface != null) {
            watchface.onVisibilityChanged(getContext(), visibility == View.VISIBLE);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (watchface != null) {

//            int sc = canvas.saveLayer(0, 0,
//                    canvas.getWidth(), canvas.getHeight(), null,
//                    Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
//                            | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
//                            | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
//                            | Canvas.CLIP_TO_LAYER_SAVE_FLAG);


            watchface.onDraw(canvas, new Rect(0, 0, canvas.getWidth(), canvas.getHeight()));

            int size = canvas.getWidth() < canvas.getHeight() ? canvas.getWidth() : canvas.getHeight();
            if (watchface.isRound()) {
                canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, size / 2, paint);
            } else {
                int left = (canvas.getWidth() - size) / 2;
                canvas.drawRect(left, 0, left + size, size, paint);
            }

//            canvas.restoreToCount(sc);
        }
    }

    public void onApplyWindowInsets(boolean isRound, int chinSize) {
        if (watchface != null) {
            watchface.onApplyWindowInsets(isRound, chinSize);
        }
    }

    public void onAmbientModeChanged(boolean isAmbientMode) {
        if (watchface != null) {
            watchface.onAmbientModeChanged(isAmbientMode);
        }
    }

    public void onPropertiesChanged(Bundle properties) {
        if (watchface != null) {
            watchface.onPropertiesChanged(properties);
        }
    }

    public void onInterruptionFilterChanged(int interruptionFilter) {
        if (watchface != null) {
            watchface.onInterruptionFilterChanged(interruptionFilter);
        }
    }
}

package com.lucasurbas.binarywatchface;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Lucas on 12/13/14.
 */
public class BinaryWatchFaceConfigActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_frame);

        if (savedInstanceState == null) {
            WatchFacePreviewFragment watchFacePreviewFragment = new WatchFacePreviewFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.content_frame, watchFacePreviewFragment).commit();
        }
    }
}

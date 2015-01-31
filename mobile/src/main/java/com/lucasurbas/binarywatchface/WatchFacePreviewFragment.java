package com.lucasurbas.binarywatchface;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.wearable.watchface.WatchFaceService;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.lucasurbas.watchface.BinaryWatchFace;

/**
 * Created by Lucas on 1/30/15.
 */
public class WatchFacePreviewFragment extends Fragment {

    private WatchFaceView watchFaceView;
    private BinaryWatchFace watchFace;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        watchFace = new BinaryWatchFace();
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_preview, container, false);
        watchFaceView = (WatchFaceView) root.findViewById(R.id.watchFaceView);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        watchFace.onCreate(getResources());
        watchFaceView.setWatchFace(watchFace);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.watch_face_menu, menu);
        menu.findItem(R.id.action_round).setChecked(watchFace.isRound());
        menu.findItem(R.id.action_ambient_mode).setChecked(watchFace.isInAmbientMode());
        menu.findItem(R.id.action_low_bit_ambient).setChecked(watchFace.isLowBitAmbient());
        menu.findItem(R.id.action_burn_in_protection).setChecked(watchFace.isBurnInProtection());
        menu.findItem(R.id.action_mute_mode).setChecked(watchFace.isInMuteMode());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean newState;
        Bundle bundle;
        switch (item.getItemId()) {
            case R.id.action_round:
                newState = !item.isChecked();
                item.setChecked(newState);
                watchFaceView.onApplyWindowInsets(newState ,0);
                return true;

            case R.id.action_ambient_mode:
                newState = !item.isChecked();
                item.setChecked(newState);
                watchFaceView.onAmbientModeChanged(newState);
                return true;

            case R.id.action_low_bit_ambient:
                newState = !item.isChecked();
                item.setChecked(newState);
                bundle = new Bundle();
                bundle.putBoolean(WatchFaceService.PROPERTY_LOW_BIT_AMBIENT, newState);
                watchFaceView.onPropertiesChanged(bundle);
                return true;

            case R.id.action_burn_in_protection:
                newState = !item.isChecked();
                item.setChecked(newState);
                bundle = new Bundle();
                bundle.putBoolean(WatchFaceService.PROPERTY_BURN_IN_PROTECTION, newState);
                watchFaceView.onPropertiesChanged(bundle);
                return true;

            case R.id.action_mute_mode:
                newState = !item.isChecked();
                item.setChecked(newState);
                watchFaceView.onInterruptionFilterChanged(newState ? WatchFaceService.INTERRUPTION_FILTER_NONE : WatchFaceService.INTERRUPTION_FILTER_ALL);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

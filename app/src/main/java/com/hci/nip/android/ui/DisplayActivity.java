package com.hci.nip.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.hci.nip.android.BaseActivity;
import com.hci.nip.android.IntentActionType;
import com.hci.nip.android.service.BroadcastService;
import com.hci.nip.android.util.TextUtil;
import com.hci.nip.base.actuator.model.DisplayData;
import com.hci.nip.glass.R;

import java.util.Arrays;
import java.util.List;

/**
 * TODO: change these activities to fragments & view model (ref: https://developer.android.com/guide/components/fragments.html#java)
 */
public class DisplayActivity extends BaseActivity {

    private static final String TAG = DisplayActivity.class.getName();

    private TextView textViewHeading;
    private TextView textViewSubheading;
    private TextView textViewContent;

    private final List<IntentActionType> intentActionTypes = Arrays.asList(
            IntentActionType.DISPLAY_UPDATE,
            IntentActionType.CAMERA_ENABLE_PREVIEW);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        initializeUIElements();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // ref: https://developer.android.com/training/system-ui/immersive#java
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        // keep screen on
                        | View.KEEP_SCREEN_ON
        );
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onIntentReceive(Context context, IntentActionType intentActionType, Intent intent) {
        Log.d(TAG, "[DISPLAY] onIntentReceive");
        switch (intentActionType) {
            case DISPLAY_UPDATE:
                updateUI(intent);
                break;
            case CAMERA_ENABLE_PREVIEW:
                enableCameraPreview();
                break;
        }
    }

    @Override
    public List<IntentActionType> getIntentActionTypes() {
        return intentActionTypes;
    }

    private void initializeUIElements() {
        textViewHeading = findViewById(R.id.displayProfileHeading);
        textViewSubheading = findViewById(R.id.displayProfileSubheading);
        textViewContent = findViewById(R.id.displayProfileContent);
    }

    private void updateUI(Intent intent) {
        Log.v(TAG, "[DISPLAY] Updating UI");
        long intentId = BroadcastService.getBroadcastIntentId(intent);
        // get request from dataRepository
        DisplayData displayData = (DisplayData) dataRepository.getRequest(intentId);
//        Log.d(TAG, "[DATA] intentId:" + intentId + ", data:" + displayData);
        // process it
        updateUIElements(displayData);
        //send the response (for this we wil directly send the same request as response)
        dataRepository.addResponse(intentId, displayData);
    }

    private void updateUIElements(DisplayData displayData) {
        runOnUiThread(() -> {
            if (!displayData.isHtml()) {
                textViewHeading.setText(displayData.getHeading());
                textViewSubheading.setText(displayData.getSubheading());
                textViewContent.setText(displayData.getContent());
            } else {
                textViewHeading.setText(TextUtil.getFormattedHtmlString(displayData.getHeading()));
                textViewSubheading.setText(TextUtil.getFormattedHtmlString((displayData.getSubheading())));
                textViewContent.setText(TextUtil.getFormattedHtmlString(displayData.getContent()));
            }
        });
    }

    private void enableCameraPreview() {
        Log.i(TAG, "[CAMERA] enableCameraPreview");
        startActivity(new Intent(this, CameraActivity.class));
    }

}

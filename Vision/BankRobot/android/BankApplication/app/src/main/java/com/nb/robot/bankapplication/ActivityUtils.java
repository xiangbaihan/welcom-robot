package com.nb.robot.bankapplication;

import android.content.Context;
import android.content.Intent;

// Utility methods for activity controls.
public class ActivityUtils {
    private static final String TAG = ActivityUtils.class.getName();

    static public void startAdsActivity(Context context) {
        Intent intent = new Intent(context, AdsActivity.class);
        context.startActivity(intent);
    }

    static public void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}

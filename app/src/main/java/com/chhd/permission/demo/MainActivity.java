package com.chhd.permission.demo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.chhd.permission.PermissionConstants;
import com.chhd.permission.PermissionUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doRequest(null);
    }

    public void doRequest(View v) {
        PermissionUtils.getInstance(this)
                .permission(PermissionConstants.PHONE,
                        PermissionConstants.STORAGE,
                        PermissionConstants.CAMERA,
                        PermissionConstants.CONTACTS)
                .callback(new PermissionUtils.Callback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        Log.i(TAG, "permissionsGranted: " + permissionsGranted);
                    }

                    @Override
                    public void onDenied(List<String> permissionsDenied,
                                         List<String> permissionsDeniedForever) {
                        Log.i(TAG, "permissionsDenied: " + permissionsDenied);
                        Log.i(TAG, "permissionsDeniedForever: " + permissionsDeniedForever);
                    }
                })
                .request();
    }

    public void doSwitch(View v) {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public void doJump(View v) {
        startActivity(new Intent(this, SecondActivity.class));
    }
}

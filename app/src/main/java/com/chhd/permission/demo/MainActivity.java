package com.chhd.permission.demo;

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

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request();
            }
        });

    }

    private void request() {
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
}

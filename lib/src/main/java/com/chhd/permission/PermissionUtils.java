package com.chhd.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 权限工具类
 *
 * @author 陈伟强 (2019/4/12)
 */
public class PermissionUtils {

    private final String TAG = this.getClass().getSimpleName();

    private static PermissionUtils mInstance;

    private WeakReference<Context> mContextRef;
    /** 将要申请的权限 */
    private Set<String> mPermissions;
    /** 正在申请的权限 */
    private ArrayList<String> mPermissionsRequest;
    /** 授权的权限 */
    private ArrayList<String> mPermissionsGranted;
    /** 拒绝的权限 */
    private ArrayList<String> mPermissionsDenied;
    /** 永久拒绝的权限 */
    private ArrayList<String> mPermissionsDeniedForever;
    private Callback mCallback;

    private PermissionUtils() {
    }

    private PermissionUtils(Context context) {
        mInstance = this;
    }

    public static PermissionUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new PermissionUtils(context);
        }
        mInstance.mContextRef = new WeakReference<>(context);
        return mInstance;
    }

    public PermissionUtils permission(@PermissionConstants.Permission final String... permissions) {
        mPermissions = new LinkedHashSet<>();
        for (String permission : permissions) {
            for (String aPermission : PermissionConstants.getPermissions(permission)) {
                if (getManifestsPermissions().contains(aPermission)) {
                    mPermissions.add(aPermission);
                }
            }
        }
        return this;
    }

    public PermissionUtils callback(final Callback callback) {
        mCallback = callback;
        return this;
    }

    public void request() {
        mPermissionsGranted = new ArrayList<>();
        mPermissionsRequest = new ArrayList<>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mPermissionsGranted.addAll(mPermissions);
            requestCallback();
        } else {
            for (String permission : mPermissions) {
                if (isGranted(permission)) {
                    mPermissionsGranted.add(permission);
                } else {
                    mPermissionsRequest.add(permission);
                }
            }
            if (mPermissionsRequest.isEmpty()) {
                requestCallback();
            } else {
                startPermissionActivity();
            }
        }
    }

    private void startPermissionActivity() {
        mPermissionsDenied = new ArrayList<>();
        mPermissionsDeniedForever = new ArrayList<>();
        PermissionActivity.start(mContextRef.get(), mPermissionsRequest);
    }

    /** 判断是否授权权限 */
    private boolean isGranted(final String... permissions) {
        for (String permission : permissions) {
            if (!isGranted(permission)) {
                return false;
            }
        }
        return true;
    }

    private boolean isGranted(final String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || PackageManager.PERMISSION_GRANTED
                == ContextCompat.checkSelfPermission(mContextRef.get(), permission);
    }

    private void requestCallback() {
        if (mCallback != null) {
            if (mPermissionsRequest.size() == 0
                    || mPermissions.size() == mPermissionsGranted.size()) {
                mCallback.onGranted(mPermissionsGranted);
            } else {
                if (!mPermissionsDenied.isEmpty()) {
                    mCallback.onDenied(mPermissionsDenied, mPermissionsDeniedForever);
                }
            }
            mCallback = null;
        }
    }

    /** Manifests申请的权限 */
    private List<String> getManifestsPermissions() {
        return getManifestsPermissions(mContextRef.get().getPackageName());
    }

    private List<String> getManifestsPermissions(final String packageName) {
        PackageManager pm = mContextRef.get().getPackageManager();
        try {
            return Arrays.asList(
                    pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
                            .requestedPermissions
            );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /** 统计各个状态的权限 */
    private void getPermissionsStatus(final Activity activity) {
        for (String permission : mPermissionsRequest) {
            if (isGranted(permission)) {
                mPermissionsGranted.add(permission);
            } else {
                mPermissionsDenied.add(permission);
                if (!activity.shouldShowRequestPermissionRationale(permission)) {
                    mPermissionsDeniedForever.add(permission);
                }
            }
        }
    }

    public interface Callback {

        void onGranted(List<String> permissionsGranted);

        void onDenied(List<String> permissionsDenied,
                      List<String> permissionsDeniedForever);
    }

    public static class PermissionActivity extends Activity {

        public static void start(Context context, ArrayList<String> permissionsRequest) {
            Intent intent = new Intent(context, PermissionActivity.class);
            intent.putExtra("permissionsRequest", permissionsRequest);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }


        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            ArrayList<String> permissionsRequest =
                    (ArrayList<String>) getIntent().getSerializableExtra("permissionsRequest");

            ActivityCompat.requestPermissions(this,
                    permissionsRequest.toArray(new String[permissionsRequest.size()]),
                    0);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               @Nullable String[] permissions,
                                               @Nullable int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            PermissionUtils.mInstance.getPermissionsStatus(this);
            PermissionUtils.mInstance.requestCallback();
            finish();
            overridePendingTransition(0, 0);
        }

    }
}

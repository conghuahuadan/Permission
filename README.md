## PermissionUtils （权限工具类）

## 依赖
[ ![Download](https://api.bintray.com/packages/conghuahuadan/maven/permission/images/download.svg) ](https://bintray.com/conghuahuadan/maven/permission/_latestVersion)
```java
dependencies {
    compile 'com.conghuahuadan:permission:latestVersion'
}
```

### 使用
```java
PermissionUtils.getInstance(this)
        .permission(PermissionConstants.PHONE,
                PermissionConstants.STORAGE,
                PermissionConstants.CAMERA,
                PermissionConstants.CONTACTS)
        .callback(new PermissionUtils.Callback() {
            @Override
            public void onGranted(List<String> permissionsGranted) {
            }

            @Override
            public void onDenied(List<String> permissionsDenied,
                                 List<String> permissionsDeniedForever) {
            }
        })
        .request();
```

## 参考
> * [PermissionUtils](https://github.com/Blankj/AndroidUtilCode/blob/master/utilcode/lib/src/main/java/com/blankj/utilcode/util/PermissionUtils.java)

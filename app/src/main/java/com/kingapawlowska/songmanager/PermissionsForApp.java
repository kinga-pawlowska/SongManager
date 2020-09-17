package com.kingapawlowska.songmanager;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Kinga on 16.01.2018.
 */

public class PermissionsForApp {

    public static final int PERMISSIONS_MULTIPLE_REQUEST_CODE = 123;

    Activity activity;

    public PermissionsForApp(Activity activity) {
        this.activity = activity;
    }

    public boolean checkMultiplePermissions() {

        int result1 = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(activity, Manifest.permission.INTERNET);
        int result4 = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE);
        int result5 = ContextCompat.checkSelfPermission(activity, Manifest.permission.GET_ACCOUNTS);

        if (
                (result1 == PackageManager.PERMISSION_GRANTED) &&
                (result2 == PackageManager.PERMISSION_GRANTED) &&
                (result3 == PackageManager.PERMISSION_GRANTED) &&
                (result4 == PackageManager.PERMISSION_GRANTED) &&
                (result5 == PackageManager.PERMISSION_GRANTED)) {
            return true;
        } else {
            return false;
        }
    }

    public void requestMultiplePermissions(){
        if (
                (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) &&
                (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) &&
                (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.INTERNET)) &&
                (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_NETWORK_STATE)) &&
                (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.GET_ACCOUNTS))
                ){
            //
        } else {
            ActivityCompat.requestPermissions(activity,new String[]
                    {
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.GET_ACCOUNTS
                    }
                    ,PERMISSIONS_MULTIPLE_REQUEST_CODE);
        }
    }
}

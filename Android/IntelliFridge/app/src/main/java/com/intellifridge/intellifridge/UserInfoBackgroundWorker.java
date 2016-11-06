package com.intellifridge.intellifridge;

import android.os.AsyncTask;

/**
 * Created by franc on 05-11-16.
 */

public class UserInfoBackgroundWorker extends AsyncTask <String,String,String>{
    private String jsonResult;
    public String getUserData_url = "http://192.168.0.163/android_api/getProfileData.php";
    @Override
    protected String doInBackground(String... params) {
        return null;
    }
}

package ovh.intellifridge.intellifridge;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static ovh.intellifridge.intellifridge.Config.FRIDGE_LIST_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.KEY_VERSION;
import static ovh.intellifridge.intellifridge.Config.LAST_UPDATE_CHECK;
import static ovh.intellifridge.intellifridge.Config.UPDATE_APP_URL;
import static ovh.intellifridge.intellifridge.Config.UPDATE_AVAILABLE;
import static ovh.intellifridge.intellifridge.Config.UPDATE_NOTIF_PREFS;
import static ovh.intellifridge.intellifridge.Config.updateNotification_id;


public class CheckAppUpdate extends Service {
    String version;
    private final static String TAG = "CheckRecentPlay";
    private static Long MILLISECS_PER_DAY = 86400000L;
    //private static long delay = 60000;                 // 1 minute (for testing)
    private static long delay = MILLISECS_PER_DAY/12;

    @Override
    public void onCreate() {
        super.onCreate();

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = pInfo.versionName;

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, MODE_PRIVATE);

        if (sharedPreferences.getBoolean(UPDATE_NOTIF_PREFS, true)) {
            // And was action not recently done?
            Long lastTimeDone = sharedPreferences.getLong(LAST_UPDATE_CHECK, 0);
            if ((System.currentTimeMillis() - lastTimeDone) >= delay) {
                checkForUpdate();
            }
        } else {
            Log.i(TAG, "Notifications are disabled");
        }

        // Set an alarm for the next time this service should run:
        setAlarm();

        stopSelf(); //stop service
    }

    private void checkForUpdate() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_APP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals(UPDATE_AVAILABLE)){
                            sendNotification();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY ERROR",error.toString());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put(KEY_VERSION,version);
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest, FRIDGE_LIST_REQUEST_TAG);
    }

    public void setAlarm() {
        Intent serviceIntent = new Intent(this, CheckAppUpdate.class);
        PendingIntent pi = PendingIntent.getService(this, 131313, serviceIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, pi);
    }

    public void sendNotification() {
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, AboutActivity.class);

        Intent downIntent = new Intent(this, AboutActivity.class);
        downIntent.setAction(getResources().getString(R.string.download_notif_btn));
        PendingIntent piDownload = PendingIntent.getActivity(this, 0, downIntent, 0);

        NotificationCompat.Builder builder = (android.support.v7.app.NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_intellifridge_notif)
                .setContentTitle(getResources().getString(R.string.update_available))
                .setContentText(getResources().getString(R.string.update_available_detail))
                .setDefaults(Notification.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getResources().getString(R.string.download_notif_big)))
                .addAction(R.drawable.ic_file_download_black_24dp,getString(R.string.notif_down),piDownload);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationManager notificationManager
                = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(updateNotification_id, builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

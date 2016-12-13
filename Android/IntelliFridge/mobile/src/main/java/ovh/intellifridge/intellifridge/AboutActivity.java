package ovh.intellifridge.intellifridge;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.google.android.gms.vision.text.Text;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import static ovh.intellifridge.intellifridge.Config.DATA;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_LIST_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_REMOVE_URL;
import static ovh.intellifridge.intellifridge.Config.JWT_KEY;
import static ovh.intellifridge.intellifridge.Config.JWT_POST;
import static ovh.intellifridge.intellifridge.Config.KEY_VERSION;
import static ovh.intellifridge.intellifridge.Config.SERVER_STATUS;
import static ovh.intellifridge.intellifridge.Config.SERVER_SUCCESS;
import static ovh.intellifridge.intellifridge.Config.UPDATE_APP_URL;
import static ovh.intellifridge.intellifridge.Config.UPDATE_AVAILABLE;
import static ovh.intellifridge.intellifridge.Config.UP_TO_DATE;

/**
 * Classe qui affiche les informations de l'application.
 * Récupère la version du fichier gradle-mobile
 * @author Francis O. Makokha
 */
public class AboutActivity extends AppCompatActivity {
    String version;
    AppCompatButton updateBtn,downloadBtn;
    TextView versionTxt,updateResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        versionTxt = (TextView)findViewById(R.id.version);
        updateBtn = (AppCompatButton)findViewById(R.id.update_btn);
        downloadBtn = (AppCompatButton)findViewById(R.id.download_update_btn);
        updateResponse = (TextView)findViewById(R.id.update_response);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAppVersion();
            }
        });

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = pInfo.versionName;
        versionTxt.setText(version);
    }

    private void checkAppVersion() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_APP_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals(UPDATE_AVAILABLE)){
                            updateResponse.setText(UPDATE_AVAILABLE);
                            updateResponse.setTextColor(getResources().getColor(R.color.colorAccent));
                            downloadBtn.setVisibility(View.VISIBLE);
                            downloadBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    downloadUpdate();
                                }
                            });
                        }else if (response.equals(UP_TO_DATE)){
                            updateResponse.setText(UP_TO_DATE);
                            updateResponse.setTextColor(getResources().getColor(R.color.btn_add));
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

    private void downloadUpdate() {
        //get destination to update file and set Uri
        //TODO: First I wanted to store my update .apk file on internal storage for my app but apparently android does not allow you to open and install
        //aplication with existing package from there. So for me, alternative solution is Download directory in external storage. If there is better
        //solution, please inform us in comment
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = "intellifridge.apk";
        destination += fileName;
        final Uri uri = Uri.parse("file://" + destination);

        //Delete update file if exists
        File file = new File(destination);
        if (file.exists())
            //file.delete() - test this, I think sometimes it doesnt work
            file.delete();

        //get url of app on server
        String url = "https://intellifridge.franmako.com/if-update.apk";

        //set downloadmanager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(getResources().getString(R.string.download_update_progress));
        request.setTitle(getResources().getString(R.string.download_update_progress));

        //set destination
        request.setDestinationUri(uri);

        // get download service and enqueue file
        if (isStoragePermissionGranted()){
            final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            final long downloadId = manager.enqueue(request);

            //set BroadcastReceiver to install app when .apk is downloaded
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    install.setDataAndType(uri,
                            manager.getMimeTypeForDownloadedFile(downloadId));
                    startActivity(install);

                    unregisterReceiver(this);
                    finish();
                }
            };
            //register receiver for when .apk download is compete
            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
}

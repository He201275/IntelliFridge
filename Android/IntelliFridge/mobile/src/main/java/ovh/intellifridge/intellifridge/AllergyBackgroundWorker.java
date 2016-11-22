package ovh.intellifridge.intellifridge;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by franc on 16-11-16.
 */

public class AllergyBackgroundWorker extends AsyncTask<String,String,String> {
    private String url_allergy = "http://intellifridge.franmako.com/getAllergyList.php";
    ProgressDialog progressDialog;
    Context context;
    JSONObject api_response;
    String server_status, reponse_status;

    AllergyBackgroundWorker(Context ctx){
        context = ctx;
    }

    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, "", "Fetching allergy list...",true);
    }

    @Override
    protected String doInBackground(String... params) {
        String userId = params[0];
        try {
            URL url = new URL(url_allergy);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

            String post_data = URLEncoder.encode("userId","UTF-8")+"="+URLEncoder.encode(String.valueOf(userId),"UTF-8");
            bufferedWriter.write(post_data);

            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            String result="";
            StringBuilder stringBuilder = new StringBuilder();

            while ((result = bufferedReader.readLine()) != null){
                stringBuilder.append(result);
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return stringBuilder.toString().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        progressDialog.dismiss();
        try {
            api_response = new JSONObject(result);
            server_status = api_response.getString("server-status");
            reponse_status = api_response.getString("reponse-status");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (server_status.equals("Database not accessible!")){
            Toast.makeText(context,R.string.db_connect_error,Toast.LENGTH_LONG).show();
        }else if (reponse_status.equals("No List")){
            Toast.makeText(context,R.string.allergy_list_empty,Toast.LENGTH_LONG).show();
        }else {
            try {
                JSONArray allergyList = api_response.getJSONArray("reponse-data");
                saveAllergyList(allergyList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveAllergyList(JSONArray allergyList) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("allergy_list",allergyList.toString());
        editor.apply();
    }
}

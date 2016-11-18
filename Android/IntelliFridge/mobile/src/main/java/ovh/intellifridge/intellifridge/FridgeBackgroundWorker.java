package ovh.intellifridge.intellifridge;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by franc on 16-11-16.
 */

class FridgeBackgroundWorker extends AsyncTask<String,String,String> {
    Context context;
    JSONObject api_response;
    String server_status, reponse_status, type, fridge_name;

    FridgeBackgroundWorker(Context ctx){
        context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {
        String userId = params[0];
        type = params[1];

        if (type.equals("add_fridge")){ //Add fridge
            fridge_name = params[2];
            String fridgeAdd_url = "http://intellifridge.franmako.com/addFridge.php";
            try { // TODO: 17-11-16
                URL url = new URL(fridgeAdd_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

                String post_data = URLEncoder.encode("userId","UTF-8")+"="+URLEncoder.encode(userId,"UTF-8")+"&"
                        +URLEncoder.encode("fridgeName","UTF-8")+"="+URLEncoder.encode(fridge_name,"UTF-8");
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
        }else if (type.equals("get_list")){ //Get fridge list
            String fridgeList_url = "http://intellifridge.franmako.com/getFridgeList.php";
            try {
                URL url = new URL(fridgeList_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

                String post_data = URLEncoder.encode("userId","UTF-8")+"="+URLEncoder.encode(userId,"UTF-8");
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
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            api_response = new JSONObject(result);
            server_status = api_response.getString("server-status");
            reponse_status = api_response.getString("reponse-status");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (server_status.equals("Database not accessible!")){
            Toast.makeText(context,R.string.db_connect_error,Toast.LENGTH_LONG).show();
        }else if (reponse_status.equals("No Fridges")){
            Toast.makeText(context,R.string.fridge_list_empty,Toast.LENGTH_LONG).show();
        }else if (reponse_status.equals("Fridge list found")){
            try {
                JSONArray fridgeData = api_response.getJSONArray("reponse-data");
                saveFridgeList(fridgeData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (reponse_status.equals("Fridge added")){
            Toast.makeText(context,R.string.add_fridge_success,Toast.LENGTH_LONG).show();
        }else if (reponse_status.equals("Fridge add error")){
            Toast.makeText(context,R.string.add_fridge_error,Toast.LENGTH_LONG).show();
        }
    }

    private void saveFridgeList(JSONArray fridgeData) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("fridge_list",fridgeData.toString());
        editor.apply();
    }
}

package com.intellifridge.intellifridge;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * Created by franc on 05-11-16.
 */

public class RegisterBackgroundWorker extends AsyncTask<String,String,String> {
    String email;
    Context context;
    ProgressDialog progressDialog;
    String register_url = "http://intellifridge.franmako.com/register.php";

    public RegisterBackgroundWorker(Context ctx) {
        context = ctx;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, "", "Creating account...",true);
    }

    @Override
    protected void onProgressUpdate(String... values){
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... params) {
        email = params[0];
        String password = params[1];
        String fName = params[2];
        String lName = params[3];
        String langue = Locale.getDefault().getDisplayLanguage();
        //String langueId;

        try {
            URL url = new URL(register_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

            String post_data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"
                    +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8")+"&"
                    +URLEncoder.encode("fName","UTF-8")+"="+URLEncoder.encode(fName,"UTF-8")+"&"
                    +URLEncoder.encode("lName","UTF-8")+"="+URLEncoder.encode(lName,"UTF-8")+"&"
                    +URLEncoder.encode("langue","UTF-8")+"="+URLEncoder.encode(langue,"UTF-8");

            bufferedWriter.write(post_data);

            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            String result="", line="";

            while ((line = bufferedReader.readLine()) != null){
                result += line;
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();

            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result){
        progressDialog.dismiss();

        switch (result){
            case "Registration Successful!":
                Toast.makeText(context, "Registration Successful", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context,LoginActivity.class);
                intent.putExtra("new_user_email",email);
                intent.putExtra("activity_id","Registration");
                context.startActivity(intent);
                break;
            case "Registration Error!":
                Toast.makeText(context, "Registration Error!", Toast.LENGTH_LONG).show();
                break;
            case "Connection Error!":
                Toast.makeText(context, "Server Connection Error!", Toast.LENGTH_LONG).show();
        }
    }
}
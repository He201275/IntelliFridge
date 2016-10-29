package com.intellifridge.intellifridge;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class LoginActivity extends AppCompatActivity{
    EditText etEmail,etPassword;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = (EditText) findViewById(R.id.email);
        etPassword = (EditText) findViewById(R.id.password);
        button = (Button) findViewById(R.id.login_btn);
    }

    public void OnLogin(View view){
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        String type = "login";

        new BackgroundWorker(this).execute(type,email,password);
    }

    private class BackgroundWorker extends AsyncTask<String,String,String> {
        Context context;
        ProgressDialog pd = new ProgressDialog(LoginActivity.this);

        BackgroundWorker(Context ctx){
            context = ctx;
        }
        @Override
        protected void onPreExecute(){
            pd.setMessage("\tLoading...");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String type = params[0];
            String email = params[1];
            String password = params[2];

            String login_url = "http://192.168.0.164/login.php";

            if (type.equals("login")){
                try {
                    URL url = new URL(login_url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

                    String post_data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"
                            +URLEncoder.encode("password","UTF-8")+"="+URLEncoder.encode(password,"UTF-8");
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
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values){
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result){
            pd.dismiss();
            if (result.equals("Login Success")){
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }else if (result.equals("Login Unsuccesful")){
                Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
            }
        }
    }
}
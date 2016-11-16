package ovh.intellifridge.intellifridge;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Activité de login
 * Appele la classe LoginBackgroundWorker pour les requêtes à la base de données
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etEmail,etPassword;
    AppCompatButton button;
    Button signup_link;
    String email_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = (EditText)findViewById(R.id.email_login);
        etPassword = (EditText)findViewById(R.id.password_login);
        button = (AppCompatButton) findViewById(R.id.login_btn);
        signup_link = (Button)findViewById(R.id.link_signup);

        button.setOnClickListener(this);
        signup_link.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            email_register = extras.getString("new_user_email");
            etEmail.setText(email_register);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean isLogged = sharedPreferences.getBoolean("isLoggedIn",false);
        if (isLogged){
            startMainActivty();
        }
    }

    private void startMainActivty() {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                onLogin(view);
                break;
            case R.id.link_signup:
                startRegisterActivity();
                break;
        }
    }

    private void startRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void onLogin(View view) {
        String user_email, user_pass;
        user_email = etEmail.getText().toString();
        user_pass = etPassword.getText().toString();

        LoginBackgroundWorker backgroundWorker = new LoginBackgroundWorker(this);
        backgroundWorker.execute(user_email,user_pass);
    }

    /**
     * Fait des requêtes à la bdd pour LoginActivity
     * Vérifie si l'email existe et le mot de passe correspond
     * Si c'est le cas, l'utilisateur accède au MainActivity
     */

    public class LoginBackgroundWorker extends AsyncTask<String,String,String> {
        String server_status, reponse_status;
        Context context;
        ProgressDialog progressDialog;
        String email,password;
        JSONObject api_response;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(context, "", "Signing in...",true);
        }

        LoginBackgroundWorker(Context ctx){
            context = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            email = params[0];
            password = params[1];
            String login_url = "http://intellifridge.franmako.com/login.php";

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
                String result="";
                StringBuilder stringBuilder = new StringBuilder();

                while ((result = bufferedReader.readLine()) != null){
                    stringBuilder.append(result);
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values){
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result){
            progressDialog.dismiss();
            try {
                api_response = new JSONObject(result);
                Log.wtf("Test",api_response.toString());
                server_status = api_response.getString("server-status");
                reponse_status = api_response.getString("reponse-status");
                Log.wtf("Test1",server_status);
                Log.wtf("Test2",reponse_status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.wtf("Test3",server_status);
            Log.wtf("Test4",reponse_status);

            if (reponse_status.equals("Login Unsuccessful!")){
                Toast.makeText(context, R.string.login_error, Toast.LENGTH_LONG).show();
            }else if (reponse_status.equals("Login Successful!")){
                try {
                    JSONObject userData = api_response.getJSONObject("reponse-data");
                    saveSessionData(userData);
                    startMainActivity();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if (server_status.equals("Database not accessible!")){
                Toast.makeText(context,R.string.db_connect_error,Toast.LENGTH_LONG).show();
            }
        }

        private void startMainActivity() {
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
        }

        private void saveSessionData(JSONObject userJson) throws JSONException {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putBoolean("isLoggedIn",true);
            int userId = userJson.getInt("UserId");
            editor.putInt("user_id",userId);
            String uEmail = userJson.getString("UserAdresseMail");
            editor.putString("user_email",uEmail);
            String fName = userJson.getString("UserPrenom");
            editor.putString("user_fName",fName);
            String lName = userJson.getString("UserNom");
            editor.putString("user_lName",lName);
            String loc = userJson.getString("UserLocalite");
            editor.putString("user_localite",loc);
            String gen = userJson.getString("UserGenre");
            editor.putString("user_genre",gen);
            String lang = userJson.getString("UserLangue");
            editor.putString("user_language",lang);
            editor.apply();
        }
    }
}

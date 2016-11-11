package ovh.intellifridge.intellifridge;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

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
 * Fait des requêtes à la bdd pour LoginActivity
 * Vérifie si l'email existe et le mot de passe correspond
 * Si c'est le cas, l'utilisateur accède au MainActivity
 */

public class LoginBackgroundWorker extends AsyncTask<String,String,String> {
    Context context;
    ProgressDialog progressDialog;
    String type,email,password, jsonString;
    public JSONObject jsonObject;

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
    protected void onProgressUpdate(String... values){
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result){
        progressDialog.dismiss();
        switch (result){
            case "Login Success!":
                saveSessionData(email,true);
                Intent intent = new Intent(context,MainActivity.class);
                context.startActivity(intent);
                break;
            case "Login Unsuccessful!":
                Toast.makeText(context, R.string.login_error, Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void saveSessionData(String email,Boolean isLogged){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_email",email);
        editor.putBoolean("isLoggedIn",isLogged);
        editor.apply();
    }
}
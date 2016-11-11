package ovh.intellifridge.intellifridge;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Activité qui permet de récupérer les infos utilisateur et de les afficher
 * Elle utilise la classe interne BackgroundWorker pour faire les requêtes à la bdd
 */
public class ProfileActivity extends AppCompatActivity {
    TextView fullName,email,firstName,lastName,locale,gender,language;
    String user_email;
    String json_string;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        fullName = (TextView)findViewById(R.id.user_profile_fullName);
        email = (TextView)findViewById(R.id.user_profile_email);
        firstName = (TextView)findViewById(R.id.user_profile_fName);
        lastName = (TextView)findViewById(R.id.user_profile_lName);
        locale = (TextView)findViewById(R.id.user_profile_locale);
        gender = (TextView)findViewById(R.id.user_profile_gender);
        language = (TextView)findViewById(R.id.user_profile_language);

        user_email = getUserEmail();

        new BackgroundTask().execute(user_email);

        email.setText(user_email);
    }

    private String getUserEmail(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userMail = sharedPreferences.getString("user_email","");
        return userMail;
    }

    /**
     * Classe permettant de faire les requêtes à la bdd pour récupérer les infos utilisateur, pour ProfileActivity
     */
    class BackgroundTask extends AsyncTask<String,String,String>{
        String json_url;
        @Override
        protected void onPreExecute() {
            json_url = "http://intellifridge.franmako.com/getProfileData.php";
        }

        @Override
        protected String doInBackground(String... params) {
            String email_user = params[0];
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));
                String post_data = URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email_user,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();


                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                while ((json_string = bufferedReader.readLine()) != null){
                    stringBuilder.append(json_string);
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
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                jsonObject = new JSONObject(result);
                updateUserDataViews(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void updateUserDataViews(JSONObject userJson) throws JSONException {
            JSONArray dataArray = userJson.getJSONArray("server_response");
            JSONObject test = dataArray.getJSONObject(0);

            String fName = test.getString("UserPrenom");
            String lName = test.getString("UserNom");
            String loc = test.getString("UserLocalite");
            String gen = test.getString("UserAdresseMail");
            String lang = test.getString("UserLangue");

            firstName.setText(fName);
            lastName.setText(lName);
            locale.setText(loc);
            gender.setText(gen);
            language.setText(lang);
        }
    }
}

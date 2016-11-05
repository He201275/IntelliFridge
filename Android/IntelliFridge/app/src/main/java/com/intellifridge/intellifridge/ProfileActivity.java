package com.intellifridge.intellifridge;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    TextView fullName,email,firstName,lastName,locale,gender,language;
    String[] userData;
    String user_email;
    JSONObject userJson;

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

        try {
            getUserDataDb();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        email.setText(user_email);
        firstName.setText(userData[0]);
        lastName.setText(userData[1]);
        locale.setText(userData[2]);
        gender.setText(userData[3]);
        language.setText(userData[4]);
    }

    private String getUserEmail(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String user_email = sharedPreferences.getString("user_email","");
        return user_email;
    }

    public void getUserDataDb() throws JSONException {
        user_email = getUserEmail();
        String type = "getUserData";
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type,user_email);
        userJson = backgroundWorker.jsonObject;
        userData[0] = userJson.getString("UserPrenom");
        userData[1] = userJson.getString("UserNom");
        userData[2] = userJson.getString("UserLocalite");
        userData[3] = userJson.getString("UserGenre");
        userData[4] = userJson.getString("UserLangue");
    }
}

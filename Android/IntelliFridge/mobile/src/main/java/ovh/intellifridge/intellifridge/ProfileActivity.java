package ovh.intellifridge.intellifridge;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Activité qui permet de récupérer les infos utilisateur et de les afficher
 * Elle utilise la classe interne BackgroundWorker pour faire les requêtes à la bdd
 */
public class ProfileActivity extends AppCompatActivity {
    TextView fullName,email,firstName,lastName,locale,gender,language;
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

        getUserData();
    }

    public void getUserData(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String user_email = sharedPreferences.getString("user_email","");
        email.setText(user_email);
        String user_fName = sharedPreferences.getString("user_fName","");
        firstName.setText(user_fName);
        String user_lName = sharedPreferences.getString("user_lName","");
        lastName.setText(user_lName);
        fullName.setText(user_fName+" "+user_lName);
        String localite = sharedPreferences.getString("user_localite","");
        if (isEmptyString(localite)){
            locale.setText(R.string.location_empty);
        }else {
            locale.setText(localite);
        }

        String genre = sharedPreferences.getString("user_genre","");
        if (isEmptyString(genre)){
            gender.setText(R.string.gender_empty);
        }else {
            gender.setText(genre);
        }
        String langue = sharedPreferences.getString("user_language","");
        language.setText(langue);
    }

    public static boolean isEmptyString(String text) {
        return (text == null || text.trim().equals("null") || text.trim()
                .length() <= 0);
    }
}

package ovh.intellifridge.intellifridge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import static ovh.intellifridge.intellifridge.Config.USER_EMAIL_PREFS;
import static ovh.intellifridge.intellifridge.Config.USER_GENRE_PREFS;
import static ovh.intellifridge.intellifridge.Config.USER_LANG_PREFS;
import static ovh.intellifridge.intellifridge.Config.USER_LOCALITE_PREFS;
import static ovh.intellifridge.intellifridge.Config.USER_NOM_PREFS;
import static ovh.intellifridge.intellifridge.Config.USER_PRENOM_PREFS;

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
        SharedPreferences preferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String user_email = preferences.getString(USER_EMAIL_PREFS,"");
        email.setText(user_email);
        String user_fName = preferences.getString(USER_PRENOM_PREFS,"");
        firstName.setText(user_fName);
        String user_lName = preferences.getString(USER_NOM_PREFS,"");
        lastName.setText(user_lName);
        fullName.setText(user_fName+" "+user_lName);
        String localite = preferences.getString(USER_LOCALITE_PREFS,"");
        if (isEmptyString(localite)){
            locale.setText(R.string.location_empty);
        }else {
            locale.setText(localite);
        }

        String genre = preferences.getString(USER_GENRE_PREFS,"");
        if (isEmptyString(genre)){
            gender.setText(R.string.gender_empty);
        }else {
            gender.setText(genre);
        }
        String langue = preferences.getString(USER_LANG_PREFS,"");
        language.setText(langue);
    }

    public static boolean isEmptyString(String text) {
        return (text == null || text.trim().equals("null") || text.trim()
                .length() <= 0);
    }
}

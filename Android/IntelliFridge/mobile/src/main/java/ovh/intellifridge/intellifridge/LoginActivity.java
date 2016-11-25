package ovh.intellifridge.intellifridge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static ovh.intellifridge.intellifridge.Config.DATA;
import static ovh.intellifridge.intellifridge.Config.DB_CONNECTION_ERROR;
import static ovh.intellifridge.intellifridge.Config.LOGGEDIN_SHARED_PREF;
import static ovh.intellifridge.intellifridge.Config.LOGIN_ERROR;
import static ovh.intellifridge.intellifridge.Config.LOGIN_REGISTER_EXTRA;
import static ovh.intellifridge.intellifridge.Config.LOGIN_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.LOGIN_SUCCESS;
import static ovh.intellifridge.intellifridge.Config.LOGIN_URL;
import static ovh.intellifridge.intellifridge.Config.SERVER_RESPONSE;
import static ovh.intellifridge.intellifridge.Config.SERVER_STATUS;
import static ovh.intellifridge.intellifridge.Config.USER_EMAIL_DB;
import static ovh.intellifridge.intellifridge.Config.USER_EMAIL_PREFS;
import static ovh.intellifridge.intellifridge.Config.USER_GENRE_DB;
import static ovh.intellifridge.intellifridge.Config.USER_GENRE_PREFS;
import static ovh.intellifridge.intellifridge.Config.USER_ID_DB;
import static ovh.intellifridge.intellifridge.Config.USER_ID_PREFS;
import static ovh.intellifridge.intellifridge.Config.USER_LANG_DB;
import static ovh.intellifridge.intellifridge.Config.USER_LANG_PREFS;
import static ovh.intellifridge.intellifridge.Config.USER_LOCALITE_DB;
import static ovh.intellifridge.intellifridge.Config.USER_LOCALITE_PREFS;
import static ovh.intellifridge.intellifridge.Config.USER_NOM_DB;
import static ovh.intellifridge.intellifridge.Config.USER_NOM_PREFS;
import static ovh.intellifridge.intellifridge.Config.USER_PRENOM_DB;
import static ovh.intellifridge.intellifridge.Config.USER_PRENOM_PREFS;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText editTextEmail;
    private EditText editTextPassword;
    private JSONObject jsonObject;
    AppCompatButton buttonLogin;
    Button signup_link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = (EditText) findViewById(R.id.email_login);
        editTextPassword = (EditText) findViewById(R.id.password_login);
        buttonLogin = (AppCompatButton) findViewById(R.id.login_btn);
        signup_link = (Button)findViewById(R.id.link_signup);

        buttonLogin.setOnClickListener(this);
        signup_link.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        boolean loggedIn = sharedPreferences.getBoolean(LOGGEDIN_SHARED_PREF, false);

        if(loggedIn){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            String email_register = extras.getString(LOGIN_REGISTER_EXTRA);
            editTextEmail.setText(email_register);
        }
    }

    private void login(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonObject = new JSONObject(response);
                            String server_status = jsonObject.getString(SERVER_STATUS);
                            String server_response = jsonObject.getString(SERVER_RESPONSE);

                            if (server_response.equals(LOGIN_ERROR)){
                                Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_LONG).show();
                            }else if (server_response.equals(LOGIN_SUCCESS)){
                                try {
                                    JSONObject userData = jsonObject.getJSONObject(DATA);
                                    saveUserData(userData);
                                    startMainActivity();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else if (server_status.equals(DB_CONNECTION_ERROR)){
                                Toast.makeText(getApplicationContext(),R.string.db_connect_error,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: 21-11-16
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                params.put(Config.KEY_EMAIL, email);
                params.put(Config.KEY_PASSWORD, password);
                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, LOGIN_REQUEST_TAG);
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }

    private void saveUserData(JSONObject userJson) throws JSONException {
        SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(LOGGEDIN_SHARED_PREF, true);
        int userId = userJson.getInt(USER_ID_DB);
        editor.putInt(USER_ID_PREFS,userId);
        String uEmail = userJson.getString(USER_EMAIL_DB);
        editor.putString(USER_EMAIL_PREFS,uEmail);
        String fName = userJson.getString(USER_PRENOM_DB);
        editor.putString(USER_PRENOM_PREFS,fName);
        String lName = userJson.getString(USER_NOM_DB);
        editor.putString(USER_NOM_PREFS,lName);
        String loc = userJson.getString(USER_LOCALITE_DB);
        editor.putString(USER_LOCALITE_PREFS,loc);
        String gen = userJson.getString(USER_GENRE_DB);
        editor.putString(USER_GENRE_PREFS,gen);
        String lang = userJson.getString(USER_LANG_DB);
        editor.putString(USER_LANG_PREFS,lang);
        editor.apply();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                login();
                break;
            case R.id.link_signup:
                startRegisterActivity();
                break;
        }
    }

    private void startRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
}

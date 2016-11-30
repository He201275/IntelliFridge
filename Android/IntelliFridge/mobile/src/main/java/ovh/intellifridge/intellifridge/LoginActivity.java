package ovh.intellifridge.intellifridge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import static ovh.intellifridge.intellifridge.Config.DATA;
import static ovh.intellifridge.intellifridge.Config.JWT_KEY;
import static ovh.intellifridge.intellifridge.Config.JWT_POST;
import static ovh.intellifridge.intellifridge.Config.KEY_EMAIL;
import static ovh.intellifridge.intellifridge.Config.KEY_PASSWORD;
import static ovh.intellifridge.intellifridge.Config.LOGGEDIN_SHARED_PREF;
import static ovh.intellifridge.intellifridge.Config.LOGIN_REGISTER_EXTRA;
import static ovh.intellifridge.intellifridge.Config.LOGIN_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.LOGIN_URL;
import static ovh.intellifridge.intellifridge.Config.MOD_ALLERGY_KEY;
import static ovh.intellifridge.intellifridge.Config.MOD_FRIDGE_KEY;
import static ovh.intellifridge.intellifridge.Config.SERVER_STATUS;
import static ovh.intellifridge.intellifridge.Config.SERVER_SUCCESS;
import static ovh.intellifridge.intellifridge.Config.USER_API_KEY;
import static ovh.intellifridge.intellifridge.Config.USER_API_KEY_DB;
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
    AppCompatButton buttonLogin;
    Button signup_link;
    private String server_status;
    private JSONObject server_response;
    private JSONObject userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getAllergyModStatus() && !getFridgeModStatus()){
            setContentView(R.layout.activity_login_allerance);
        }else {
            setContentView(R.layout.activity_login);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        editTextEmail = (EditText) findViewById(R.id.email_login);
        editTextPassword = (EditText) findViewById(R.id.password_login);
        buttonLogin = (AppCompatButton) findViewById(R.id.login_btn);
        signup_link = (Button)findViewById(R.id.link_signup);

        buttonLogin.setOnClickListener(this);
        signup_link.setOnClickListener(this);
    }

    public Boolean getFridgeModStatus() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getBoolean(MOD_FRIDGE_KEY,true);
    }
    public Boolean getAllergyModStatus(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getBoolean(MOD_ALLERGY_KEY,true);
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
                        final String secret = JWT_KEY;
                        try {
                            final JWTVerifier verifier = new JWTVerifier(secret);
                            final Map<String, Object> claims= verifier.verify(response);
                            server_response = new JSONObject(claims);
                            server_status = server_response.getString(SERVER_STATUS);
                            userData = server_response.getJSONObject(DATA);
                        } catch (JWTVerifyException e) {
                            // Invalid Token
                            // TODO: 30-11-16  
                        } catch (NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException | JSONException e) {
                            e.printStackTrace();
                        }

                        if (server_status.equals(SERVER_SUCCESS)){
                            try {
                                saveUserData(userData);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            startMainActivity();
                        }else{
                            Toast.makeText(getApplicationContext(),R.string.login_error,Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.wtf("ERROR",error.toString());
                        // TODO: 30-11-16
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String jwt = signParams(email,password);
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                params.put(JWT_POST,jwt);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, LOGIN_REQUEST_TAG);
    }

    private String signParams(String email, String password) {
        final String secret = JWT_KEY;
        String jwt = "";

        final JWTSigner signer = new JWTSigner(secret);
        final HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put(KEY_EMAIL, email);
        claims.put(KEY_PASSWORD, password);
        return jwt = signer.sign(claims);
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
        String apiKey = userJson.getString(USER_API_KEY_DB);
        editor.putString(USER_API_KEY,apiKey);
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

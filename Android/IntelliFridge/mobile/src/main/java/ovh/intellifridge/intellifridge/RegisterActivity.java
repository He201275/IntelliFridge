package ovh.intellifridge.intellifridge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
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
import java.util.Locale;
import java.util.Map;

import static ovh.intellifridge.intellifridge.Config.JWT_KEY;
import static ovh.intellifridge.intellifridge.Config.JWT_POST;
import static ovh.intellifridge.intellifridge.Config.KEY_EMAIL;
import static ovh.intellifridge.intellifridge.Config.KEY_FNAME;
import static ovh.intellifridge.intellifridge.Config.KEY_LANGUE;
import static ovh.intellifridge.intellifridge.Config.KEY_LNAME;
import static ovh.intellifridge.intellifridge.Config.KEY_PASSWORD;
import static ovh.intellifridge.intellifridge.Config.LOGIN_REGISTER_EXTRA;
import static ovh.intellifridge.intellifridge.Config.MOD_ALLERGY_KEY;
import static ovh.intellifridge.intellifridge.Config.MOD_FRIDGE_KEY;
import static ovh.intellifridge.intellifridge.Config.REGISTER_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.REGISTER_URL;
import static ovh.intellifridge.intellifridge.Config.SERVER_STATUS;
import static ovh.intellifridge.intellifridge.Config.SERVER_SUCCESS;

/**
 * @author Francis O. Makokha
 * Permet de gérer l'inscription d'un utilisateur
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    EditText editTextFname,editTextLname,editTextEmail,editTextPassword;
    AppCompatButton buttonRegister;
    Button login_link;
    private String server_status;
    private JSONObject server_response;
    String email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_activity_register);
        if (getAllergyModStatus() && !getFridgeModStatus()){
            setContentView(R.layout.activity_register_allerance);
        }else {
            setContentView(R.layout.activity_register);
        }

        editTextFname = (EditText) findViewById(R.id.fName);
        editTextLname = (EditText) findViewById(R.id.lName);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        buttonRegister = (AppCompatButton)findViewById(R.id.btnRegister);
        login_link = (Button)findViewById(R.id.login_link);
        login_link.setPaintFlags(login_link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);//underline

        buttonRegister.setOnClickListener(this);
        login_link.setOnClickListener(this);
    }

    public Boolean getFridgeModStatus() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getBoolean(MOD_FRIDGE_KEY,true);
    }
    public Boolean getAllergyModStatus(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getBoolean(MOD_ALLERGY_KEY,true);
    }

    private void register(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final String secret = JWT_KEY;
                        try {
                            final JWTVerifier verifier = new JWTVerifier(secret);
                            final Map<String, Object> claims= verifier.verify(response);
                            server_response = new JSONObject(claims);
                            server_status = server_response.getString(SERVER_STATUS);
                        } catch (JWTVerifyException e) {
                            // Invalid Token
                            Log.e("JWT ERROR",e.toString());
                        } catch (NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException | JSONException e) {
                            e.printStackTrace();
                        }

                        if (server_status.equals(SERVER_SUCCESS)){
                            Toast.makeText(getApplicationContext(),R.string.register_success,Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                            intent.putExtra(LOGIN_REGISTER_EXTRA,email);
                            startActivity(intent);
                        }else if (server_status == null){
                            Toast.makeText(getApplicationContext(),R.string.login_error,Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(getApplicationContext(),R.string.login_error,Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.wtf("ERR",error.toString());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String fName = editTextFname.getText().toString();
                String lName = editTextLname.getText().toString();
                email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String langue = Locale.getDefault().getDisplayLanguage();
                String jwt = signParams(fName,lName,email,password,langue);
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                params.put(JWT_POST,jwt);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, REGISTER_REQUEST_TAG);
    }

    private String signParams(String fName, String lName, String email, String password, String langue) {
        final String secret = JWT_KEY;
        String jwt = "";
        String lang_code = getLangCode(langue);

        final JWTSigner signer = new JWTSigner(secret);
        final HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put(KEY_FNAME,fName);
        claims.put(KEY_LNAME,lName);
        claims.put(KEY_EMAIL,email);
        claims.put(KEY_LANGUE,lang_code);
        claims.put(KEY_PASSWORD,password);
        return jwt = signer.sign(claims);
    }

    private String getLangCode(String langue) {
        switch (langue){
            case "English":
                return "EN";
            case "Français":
                return "FR";
            case "Nederlands":
                return "NL";
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnRegister:
                register();
                break;
            case R.id.login_link:
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                break;
        }
    }
}

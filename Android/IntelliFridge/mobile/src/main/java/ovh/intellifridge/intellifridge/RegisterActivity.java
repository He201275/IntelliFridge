package ovh.intellifridge.intellifridge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static ovh.intellifridge.intellifridge.Config.DB_CONNECTION_ERROR;
import static ovh.intellifridge.intellifridge.Config.KEY_EMAIL;
import static ovh.intellifridge.intellifridge.Config.KEY_FNAME;
import static ovh.intellifridge.intellifridge.Config.KEY_LANGUE;
import static ovh.intellifridge.intellifridge.Config.KEY_LNAME;
import static ovh.intellifridge.intellifridge.Config.KEY_PASSWORD;
import static ovh.intellifridge.intellifridge.Config.LOGIN_REGISTER_EXTRA;
import static ovh.intellifridge.intellifridge.Config.LOGIN_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.REGISTER_ERROR;
import static ovh.intellifridge.intellifridge.Config.REGISTER_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.REGISTER_SUCCESS;
import static ovh.intellifridge.intellifridge.Config.REGISTER_URL;
import static ovh.intellifridge.intellifridge.Config.REQUIRED_FIELD_ERROR;
import static ovh.intellifridge.intellifridge.Config.SERVER_RESPONSE;
import static ovh.intellifridge.intellifridge.Config.SERVER_STATUS;

/**
 * Created by franc on 22-11-16.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    EditText editTextFname,editTextLname,editTextEmail,editTextPassword;
    AppCompatButton buttonRegister;
    Button login_link;
    JSONObject jsonObject;
    String email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextFname = (EditText) findViewById(R.id.fName);
        editTextLname = (EditText) findViewById(R.id.lName);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        buttonRegister = (AppCompatButton)findViewById(R.id.btnRegister);
        login_link = (Button)findViewById(R.id.login_link);

        buttonRegister.setOnClickListener(this);
        login_link.setOnClickListener(this);
    }

    private void register(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            jsonObject = new JSONObject(response);
                            String server_status = jsonObject.getString(SERVER_STATUS);
                            String server_response = jsonObject.getString(SERVER_RESPONSE);

                            if (server_response.equals(REGISTER_SUCCESS)){
                                Toast.makeText(getApplicationContext(), R.string.register_success, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                intent.putExtra(LOGIN_REGISTER_EXTRA,email);
                                startActivity(intent);
                            }else if (server_response.equals(REGISTER_ERROR)){
                                Toast.makeText(getApplicationContext(), R.string.register_error, Toast.LENGTH_LONG).show();
                            }else if (server_status.equals(DB_CONNECTION_ERROR)){
                                Toast.makeText(getApplicationContext(),R.string.db_connect_error,Toast.LENGTH_LONG).show();
                            }else if (server_status.equals(REQUIRED_FIELD_ERROR)){
                                Toast.makeText(getApplicationContext(), R.string.field_error, Toast.LENGTH_LONG).show();
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
                String fName = editTextFname.getText().toString();
                String lName = editTextLname.getText().toString();
                email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String langue = Locale.getDefault().getDisplayLanguage();
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                params.put(KEY_EMAIL, email);
                params.put(KEY_PASSWORD, password);
                params.put(KEY_FNAME,fName);
                params.put(KEY_LNAME,lName);
                params.put(KEY_LANGUE,langue);
                return params;
            }
        };

        //Adding the string request to the queue
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, REGISTER_REQUEST_TAG);
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

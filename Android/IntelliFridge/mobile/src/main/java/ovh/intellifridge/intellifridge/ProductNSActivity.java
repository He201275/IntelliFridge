package ovh.intellifridge.intellifridge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.auth0.jwt.JWTSigner;

import java.util.HashMap;
import java.util.Map;

import static ovh.intellifridge.intellifridge.Config.FRIDGE_CONTENT_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.GET_PRODUCT_NS_URL;
import static ovh.intellifridge.intellifridge.Config.JWT_KEY;
import static ovh.intellifridge.intellifridge.Config.JWT_POST;
import static ovh.intellifridge.intellifridge.Config.KEY_API_KEY;
import static ovh.intellifridge.intellifridge.Config.KEY_OFFSET;
import static ovh.intellifridge.intellifridge.Config.KEY_USERID;
import static ovh.intellifridge.intellifridge.Config.SHARED_PREF_NAME;
import static ovh.intellifridge.intellifridge.Config.USER_API_KEY;
import static ovh.intellifridge.intellifridge.Config.USER_ID_PREFS;

/**
 * Activité pour la gestion de l'ajout des produits non-scannables dans les frigos et la liste
 */
public class ProductNSActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_ns);
        getProductNSData();
    }

    private void getProductNSData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_PRODUCT_NS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.wtf("NS",response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY ERROR",error.toString());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String apiKey = getApiKey();
                int user_id = getUserId();
                String offset = "0";
                String jwt = signParamsProductNS(apiKey,user_id,offset);
                Log.wtf("REQ",jwt);
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                params.put(JWT_POST,jwt);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, FRIDGE_CONTENT_REQUEST_TAG);
    }

    private String signParamsProductNS(String apiKey, int user_id, String offset) {
        final JWTSigner signer = new JWTSigner(JWT_KEY);
        final HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put(KEY_USERID,user_id);
        claims.put(KEY_API_KEY,apiKey);
        claims.put(KEY_OFFSET,offset);
        return signer.sign(claims);
    }

    private int getUserId() {
        SharedPreferences preferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(USER_ID_PREFS,0);
    }

    private String getApiKey(){
        SharedPreferences preferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(USER_API_KEY,"");
    }
}

package ovh.intellifridge.intellifridge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import static ovh.intellifridge.intellifridge.Config.DATA;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_CONTENT_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_CONTENT_URL;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_NAME_EXTRA;
import static ovh.intellifridge.intellifridge.Config.GET_FRIDGECONTENT_INFO;
import static ovh.intellifridge.intellifridge.Config.JWT_KEY;
import static ovh.intellifridge.intellifridge.Config.JWT_POST;
import static ovh.intellifridge.intellifridge.Config.KEY_API_KEY;
import static ovh.intellifridge.intellifridge.Config.KEY_BARCODE;
import static ovh.intellifridge.intellifridge.Config.KEY_FRIDGE_NAME;
import static ovh.intellifridge.intellifridge.Config.KEY_USERID;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_S_ID;
import static ovh.intellifridge.intellifridge.Config.SERVER_STATUS;
import static ovh.intellifridge.intellifridge.Config.SERVER_SUCCESS;
import static ovh.intellifridge.intellifridge.Config.SHARED_PREF_NAME;
import static ovh.intellifridge.intellifridge.Config.USER_API_KEY;
import static ovh.intellifridge.intellifridge.Config.USER_ID_PREFS;

public class FridgeContentActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout swipeLayout;
    String fridge;
    private String server_status;
    private JSONObject server_response;
    private JSONArray jsonArray;
    private String[] prodNameArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fridge_content);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fridge = extras.getString(FRIDGE_NAME_EXTRA);
            setTitle(fridge);
        }

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_fridge_content);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_red_dark));

        getFridgeContentData();
    }



    private String getApiKey() {
        SharedPreferences preferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(USER_API_KEY,"");
    }

    private void getFridgeContentData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FRIDGE_CONTENT_URL,
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
                            // TODO: 30-11-16
                        } catch (NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException | JSONException e) {
                            e.printStackTrace();
                        }

                        if (server_status.equals(SERVER_SUCCESS)){
                            try {
                                jsonArray = server_response.getJSONArray(DATA);
                                displayFridgeContent(jsonArray);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            // TODO: 01-12-16
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
                String apiKey = getApiKey();
                int user_id = getUserId();
                String jwt = signParamsFridgeContent(fridge,apiKey,user_id);
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                params.put(JWT_POST,jwt);
                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, FRIDGE_CONTENT_REQUEST_TAG);
    }

    private String signParamsFridgeContent(String fridge,String apiKey,int userId) {
        final String secret = JWT_KEY;
        String jwt = "";

        final JWTSigner signer = new JWTSigner(secret);
        final HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put(KEY_FRIDGE_NAME,fridge);
        claims.put(KEY_USERID,userId);
        claims.put(KEY_API_KEY,apiKey);
        return jwt = signer.sign(claims);
    }

    private int getUserId() {
        SharedPreferences preferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(USER_ID_PREFS,0);
    }

    private void displayFridgeContent(JSONArray jsonArray){
        String[] list= null;
        list = getStringArrayProduct(jsonArray);

        final ListView listView = (ListView) findViewById(R.id.fridge_content_list);
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                list
        );
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // TODO: 01-12-16
            }
        });
    }

    private String[] getStringArrayProduct(JSONArray jsonArray) {
        String string;
        String[] stringArray = null;
        int length = jsonArray.length();
        stringArray = new String[length];
        for(int i=0;i<length;i++){
            string = jsonArray.optString(i);
            try {
                JSONObject jsonObj = new JSONObject(string);
                stringArray[i] = jsonObj.getString(PRODUCT_S_ID);
                for (int x = 0;x<length;x++){
                    getProductInfo(stringArray[i]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return stringArray;
    }

    private void getProductInfo(String barcode) {
        int userId = getUserId();
        String api_key = getApiKey();
        String jwt = signParamsGetProdInfo(barcode,api_key,userId);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_FRIDGECONTENT_INFO+"?jwt="+jwt,
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
                            // TODO: 30-11-16
                        } catch (NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException | JSONException e) {
                            e.printStackTrace();
                        }

                        if (server_status.equals(SERVER_SUCCESS)){
                            try {
                                JSONObject jsonObjectProdInfo = server_response.getJSONObject(DATA);
                                Log.wtf("TEST",jsonObjectProdInfo.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            // TODO: 01-12-16
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: 21-11-16
                    }
                });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, FRIDGE_CONTENT_REQUEST_TAG);
    }

    private String signParamsGetProdInfo(String barcode, String api_key, int userId) {
        final String secret = JWT_KEY;
        String jwt = "";

        final JWTSigner signer = new JWTSigner(secret);
        final HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put(KEY_BARCODE,barcode);
        claims.put(KEY_API_KEY,api_key);
        claims.put(KEY_USERID,userId);
        return jwt = signer.sign(claims);
    }

    @Override
    public void onRefresh() {
        getFridgeContentData();
        //displayFridgeContent(jsonArray);
        swipeLayout.setRefreshing(false);
    }
}

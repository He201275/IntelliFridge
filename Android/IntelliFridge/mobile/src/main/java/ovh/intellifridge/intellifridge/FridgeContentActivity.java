package ovh.intellifridge.intellifridge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
import static ovh.intellifridge.intellifridge.Config.JWT_KEY;
import static ovh.intellifridge.intellifridge.Config.JWT_POST;
import static ovh.intellifridge.intellifridge.Config.KEY_API_KEY;
import static ovh.intellifridge.intellifridge.Config.KEY_FRIDGE_NAME;
import static ovh.intellifridge.intellifridge.Config.KEY_USERID;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_ID_DB;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_NAME_DB;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_QUANTITY_DB;
import static ovh.intellifridge.intellifridge.Config.SERVER_FRIDGE_EMPTY;
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
                        Log.wtf("RESPONSE",response);
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
                            try {
                                jsonArray = server_response.getJSONArray(DATA);
                                getFridgeContentList(jsonArray);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else if (server_status.equals(SERVER_FRIDGE_EMPTY)){
                            Toast.makeText(getApplicationContext(),R.string.fridge_empty,Toast.LENGTH_LONG).show();
                        }
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
                String jwt = signParamsFridgeContent(fridge,apiKey,user_id);
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                params.put(JWT_POST,jwt);
                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, FRIDGE_CONTENT_REQUEST_TAG);
    }

    private void getFridgeContentList(JSONArray jsonArray) {
        Product[] fridgeContent = getFridgeContentArray(jsonArray);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.fridge_content_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        FridgeContentRVAdapter adapter = new FridgeContentRVAdapter(fridgeContent);
        recyclerView.setAdapter(adapter);
    }

    private Product[] getFridgeContentArray(JSONArray jsonArray){
        int length = jsonArray.length();
        Product[] fridgeContent = new Product[length];

        for(int i=0;i<length;i++){
            try {
                fridgeContent[i]= new Product("",0,0);
                fridgeContent[i].productName = jsonArray.optJSONObject(i).optString(PRODUCT_NAME_DB);
                fridgeContent[i].productId = jsonArray.optJSONObject(i).optInt(PRODUCT_ID_DB);
                fridgeContent[i].productQuantity = jsonArray.getJSONObject(i).optInt(PRODUCT_QUANTITY_DB);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return fridgeContent;
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

    @Override
    public void onRefresh() {
        getFridgeContentData();
        //getFridgeContentList(jsonArray);
        swipeLayout.setRefreshing(false);
    }
}

package ovh.intellifridge.intellifridge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
import static ovh.intellifridge.intellifridge.Config.GET_PRODUCT_NS_URL;
import static ovh.intellifridge.intellifridge.Config.JWT_KEY;
import static ovh.intellifridge.intellifridge.Config.JWT_POST;
import static ovh.intellifridge.intellifridge.Config.KEY_API_KEY;
import static ovh.intellifridge.intellifridge.Config.KEY_OFFSET;
import static ovh.intellifridge.intellifridge.Config.KEY_USERID;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_ID_NS_DB;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_NS_NAME_FR_DB;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_NS_TYPE_DB;
import static ovh.intellifridge.intellifridge.Config.SERVER_STATUS;
import static ovh.intellifridge.intellifridge.Config.SERVER_SUCCESS;
import static ovh.intellifridge.intellifridge.Config.SHARED_PREF_NAME;
import static ovh.intellifridge.intellifridge.Config.USER_API_KEY;
import static ovh.intellifridge.intellifridge.Config.USER_ID_PREFS;

/**
 * Activité pour la gestion de l'ajout des produits non-scannables dans les frigos et la liste
 */
public class ProductNSActivity extends AppCompatActivity {
    private EndlessRecyclerViewScrollListener scrollListener;
    private JSONObject server_response;
    JSONArray jsonArray;
    String server_status;
    Product[] productNsList;
    int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_ns);
        page = 0;
        getProductNSData(page);
    }

    private void getProductNSData(final int offset) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_PRODUCT_NS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final String secret = JWT_KEY;
                        try {
                            final JWTVerifier verifier = new JWTVerifier(secret);
                            final Map<String, Object> claims= verifier.verify(response);
                            server_response = new JSONObject(claims);
                            server_status = server_response.getString(SERVER_STATUS);
                            jsonArray = server_response.getJSONArray(DATA);
                        } catch (JWTVerifyException e) {
                            // Invalid Token
                            Log.e("JWT ERROR",e.toString());
                        } catch (NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException | JSONException e) {
                            e.printStackTrace();
                        }

                        if (server_status.equals(SERVER_SUCCESS)){
                            getProductNSCardList(jsonArray);
                        }else{
                            // TODO: 13-12-16
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
                String jwt = signParamsProductNS(apiKey,user_id,offset);
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                params.put(JWT_POST,jwt);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, FRIDGE_CONTENT_REQUEST_TAG);
    }

    private void getProductNSCardList(JSONArray jsonArray) {
        productNsList = getProductNSArray(jsonArray);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.product_ns_recyclerview);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        ProductNSRVAdapter adapter = new ProductNSRVAdapter(productNsList,this);
        recyclerView.setAdapter(adapter);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                getProductNSData(page+1);
            }
        };
        recyclerView.addOnScrollListener(scrollListener);
    }

    private Product[] getProductNSArray(JSONArray jsonArray) {
        int length = jsonArray.length();
        Product[] productList = new Product[length];

        for (int i=0;i<length;i++){
            productList[i] = new Product();
            productList[i].setProductNSId(jsonArray.optJSONObject(i).optInt(PRODUCT_ID_NS_DB));
            productList[i].setProductNameNS_fr(jsonArray.optJSONObject(i).optString(PRODUCT_NS_NAME_FR_DB));
            productList[i].setProductNSType(jsonArray.optJSONObject(i).optString(PRODUCT_NS_TYPE_DB));
        }
        return productList;
    }

    private String signParamsProductNS(String apiKey, int user_id, int offset) {
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

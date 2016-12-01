package ovh.intellifridge.intellifridge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import static ovh.intellifridge.intellifridge.Config.ADD_PRODUCT_LOCAL_DB;
import static ovh.intellifridge.intellifridge.Config.ADD_PRODUCT_LOCAL_DB_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.BARCODE_CHECK_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.BARCODE_EXTRA;
import static ovh.intellifridge.intellifridge.Config.DATA;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_LIST_PREFS;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_NAME_DB;
import static ovh.intellifridge.intellifridge.Config.GET_INFO_LOCAL_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.GET_INFO_OFF_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.GET_PRODUCT_INFO_LOCAL_URL;
import static ovh.intellifridge.intellifridge.Config.JWT_KEY;
import static ovh.intellifridge.intellifridge.Config.JWT_POST;
import static ovh.intellifridge.intellifridge.Config.KEY_API_KEY;
import static ovh.intellifridge.intellifridge.Config.KEY_BARCODE;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_BRAND;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_IMAGEURL;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_NAME;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_QUANTITY;
import static ovh.intellifridge.intellifridge.Config.KEY_USERID;
import static ovh.intellifridge.intellifridge.Config.OFF_BRANDS;
import static ovh.intellifridge.intellifridge.Config.OFF_IMAGE_URL;
import static ovh.intellifridge.intellifridge.Config.OFF_PRODUCT;
import static ovh.intellifridge.intellifridge.Config.OFF_PRODUCTNAME;
import static ovh.intellifridge.intellifridge.Config.OFF_PRODUCTNAME_EN;
import static ovh.intellifridge.intellifridge.Config.OFF_PRODUCTNAME_FR;
import static ovh.intellifridge.intellifridge.Config.OFF_QUANTITY;
import static ovh.intellifridge.intellifridge.Config.OFF_STATUS_FOUND;
import static ovh.intellifridge.intellifridge.Config.OFF_STATUS_VERBOSE;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_DB_LOCATION_CHECK_URL;
import static ovh.intellifridge.intellifridge.Config.SERVER_STATUS;
import static ovh.intellifridge.intellifridge.Config.SERVER_SUCCESS;
import static ovh.intellifridge.intellifridge.Config.SHARED_PREF_NAME;
import static ovh.intellifridge.intellifridge.Config.USER_API_KEY;
import static ovh.intellifridge.intellifridge.Config.USER_ID_PREFS;

public class ProductScanActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener{
    String barcode,url_off,imageUrl,productName,quantity, off_status, fridge_selected,brands;
    TextView txtQuantity, txtProduct;
    JSONObject product;
    AppCompatButton addProductFridge,cancelAddProduct;
    private JSONObject server_response;
    private String server_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_scan);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            barcode = extras.getString(BARCODE_EXTRA);
        }

        url_off = "http://fr.openfoodfacts.org/api/v0/product/" + barcode + ".json";

        isInDb();

        Spinner spinner = (Spinner)findViewById(R.id.fridge_spinner);
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        JSONArray fridge_list_json = null;
        String[] list= null;
        String fridge_list_string = prefs.getString(FRIDGE_LIST_PREFS,"");
        try {
            fridge_list_json = new JSONArray(fridge_list_string);
            list = getStringArrayFridge(fridge_list_json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> spinnerArrayAdapter= new ArrayAdapter(this,android.R.layout.simple_spinner_item,list);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    private String[] getStringArrayFridge(JSONArray fridge_list_json) {
        String string;
        String[] stringArray = null;
        int length = fridge_list_json.length();
        stringArray = new String[length];
        for(int i=0;i<length;i++){
            string = fridge_list_json.optString(i);
            try {
                JSONObject jsonObj = new JSONObject(string);
                stringArray[i] = jsonObj.getString(FRIDGE_NAME_DB);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return stringArray;
    }

    private void isInDb() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PRODUCT_DB_LOCATION_CHECK_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.wtf("Barcode",barcode);
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
                            getProductInfoLocalDb(barcode);
                        }else{
                            getProductInfoOFF();
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
                String jwt = signParamsIsInDb(barcode);
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                params.put(JWT_POST, jwt);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, BARCODE_CHECK_REQUEST_TAG);
    }

    private String signParamsIsInDb(String produitSId) {
        final String secret = JWT_KEY;
        String jwt = "";

        final JWTSigner signer = new JWTSigner(secret);
        final HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put(KEY_BARCODE, produitSId);
        return jwt = signer.sign(claims);
    }

    private void getProductInfoLocalDb(final String product_id_localdb) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_PRODUCT_INFO_LOCAL_URL,
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
                                Log.wtf("TEST", String.valueOf(server_response.getJSONObject(DATA)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
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
                String jwt = signParamsGetProdInfoLocalDb(barcode);
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                params.put(JWT_POST, jwt);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, GET_INFO_LOCAL_REQUEST_TAG);
    }

    private String signParamsGetProdInfoLocalDb(String barcode) {
        final String secret = JWT_KEY;
        String jwt = "";

        final JWTSigner signer = new JWTSigner(secret);
        final HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put(KEY_BARCODE, barcode);
        return jwt = signer.sign(claims);
    }

    private void getProductInfoOFF() {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url_off,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            displayProductInfoOFF(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest, GET_INFO_OFF_REQUEST_TAG);
    }

    private void displayProductInfoOFF(JSONObject response) throws JSONException {
        //copyProductLocalDb();
        off_status = response.getString(OFF_STATUS_VERBOSE);

        if (off_status.equals(OFF_STATUS_FOUND)){
            product = response.getJSONObject(OFF_PRODUCT);

            if (product.has(OFF_IMAGE_URL)) {
                imageUrl = product.getString(OFF_IMAGE_URL);
                ImageView pic1 = (ImageView) findViewById(R.id.pic1);
                Picasso.with(getBaseContext()).load(imageUrl)
                        .fit().centerInside()
                        .into(pic1);
            }

            if (product.has(OFF_BRANDS)){
                brands = product.getString(OFF_BRANDS);
            }

            if (product.has(OFF_PRODUCTNAME)){
                productName = product.getString(OFF_PRODUCTNAME);
            }else if (product.has(OFF_PRODUCTNAME_EN)){
                productName = product.getString(OFF_PRODUCTNAME_EN);
            }else if (product.has(OFF_PRODUCTNAME_FR)){
                productName = product.getString(OFF_PRODUCTNAME_FR);
            }
            txtProduct = (TextView)findViewById(R.id.productName);
            txtProduct.setText(productName);

            quantity = product.getString(OFF_QUANTITY);
            txtQuantity = (TextView)findViewById(R.id.quantityName);
            txtQuantity.setText(quantity);

            addProductFridge = (AppCompatButton)findViewById(R.id.add_product_btn);
            cancelAddProduct = (AppCompatButton)findViewById(R.id.cancel_add_btn);
            addProductFridge.setOnClickListener(this);
            cancelAddProduct.setOnClickListener(this);
        }else {
            // TODO: 26-11-16  
        }
    }

    private void copyProductLocalDb() {
        // TODO: 26-11-16
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        fridge_selected = adapterView.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // TODO: 26-11-16
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_product_btn:
                addProductLocalDb();
                addProductFridge();
                Toast.makeText(getApplicationContext(),getString(R.string.product_add_fridge_success),Toast.LENGTH_LONG).show();
                break;
            case R.id.cancel_add_btn:
                startMainActivity();
                break;
        }
    }

    private void addProductLocalDb() {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, ADD_PRODUCT_LOCAL_DB,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        final String secret = JWT_KEY;
                        try {
                            final JWTVerifier verifier = new JWTVerifier(secret);
                            final Map<String, Object> claims= verifier.verify(String.valueOf(response));
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
                                Log.wtf("TEST", String.valueOf(server_response.getJSONObject(DATA)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            // TODO: 01-12-16
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String jwt = signParamsAddLocalDb(productName,imageUrl,brands,quantity);
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                params.put(JWT_POST,jwt);
                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest, ADD_PRODUCT_LOCAL_DB_REQUEST_TAG);
    }

    private String signParamsAddLocalDb(String productName, String imageUrl, String brands, String quantity) {
        int userId = getUserId();
        String apiKey = getApiKey();
        final String secret = JWT_KEY;
        String jwt = "";

        final JWTSigner signer = new JWTSigner(secret);
        final HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put(KEY_PRODUCT_BRAND, brands);
        claims.put(KEY_PRODUCT_IMAGEURL,imageUrl);
        claims.put(KEY_PRODUCT_QUANTITY,quantity);
        claims.put(KEY_PRODUCT_NAME,productName);
        claims.put(KEY_USERID,userId);
        claims.put(KEY_API_KEY,apiKey);
        return jwt = signer.sign(claims);
    }
    private String getApiKey() {
        SharedPreferences preferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(USER_API_KEY,"");
    }
    private int getUserId() {
        SharedPreferences preferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(USER_ID_PREFS,0);
    }

    private void addProductFridge() {

    }

    private void startMainActivity() {
        Intent intent = new Intent(ProductScanActivity.this,MainActivity.class);
        startActivity(intent);
    }
}

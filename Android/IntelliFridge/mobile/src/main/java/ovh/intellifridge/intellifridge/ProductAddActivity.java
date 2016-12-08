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
import com.google.gson.Gson;
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

import static ovh.intellifridge.intellifridge.Config.ADD_PRODUCT_URL;
import static ovh.intellifridge.intellifridge.Config.BARCODE_CHECK_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.BARCODE_EXTRA;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_ID_EXTRA;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_NAME_EXTRA;
import static ovh.intellifridge.intellifridge.Config.GET_INFO_OFF_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.JWT_KEY;
import static ovh.intellifridge.intellifridge.Config.JWT_POST;
import static ovh.intellifridge.intellifridge.Config.KEY_API_KEY;
import static ovh.intellifridge.intellifridge.Config.KEY_FRIDGE_NAME;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_BRAND;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_IMAGEURL;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_NAME;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_QUANTITY;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_S_ID;
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
import static ovh.intellifridge.intellifridge.Config.PRODUCT_ADD_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_DB_LOCATION_CHECK_URL;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_S_ID_DB;
import static ovh.intellifridge.intellifridge.Config.SERVER_PROD_NOTINDB;
import static ovh.intellifridge.intellifridge.Config.SERVER_STATUS;
import static ovh.intellifridge.intellifridge.Config.SERVER_SUCCESS;
import static ovh.intellifridge.intellifridge.Config.SHARED_PREF_FRIDGES_NAME;
import static ovh.intellifridge.intellifridge.Config.SHARED_PREF_NAME;
import static ovh.intellifridge.intellifridge.Config.USER_API_KEY;
import static ovh.intellifridge.intellifridge.Config.USER_FRIDGE_PREFS;
import static ovh.intellifridge.intellifridge.Config.USER_ID_PREFS;
import static ovh.intellifridge.intellifridge.Config.USER_NB_FRIDGES_PREFS;
import static ovh.intellifridge.intellifridge.Config.VOLLEY_ERROR_TAG;

public class ProductAddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener{
    String barcode,imageUrl,productName,quantity, off_status, fridge_selected,brands, server_status;
    TextView txtQuantity, txtProduct;
    JSONObject product,server_response;
    AppCompatButton addProductFridge,cancelAddProduct;
    String secret = JWT_KEY;
    Boolean prodInDb = false;
    int fridgeId;
    Fridge[] fridges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_scan);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            barcode = extras.getString(BARCODE_EXTRA);
            if (extras.getString(FRIDGE_NAME_EXTRA) != null){
                fridge_selected = extras.getString(FRIDGE_NAME_EXTRA);
            }

            if (extras.getString(FRIDGE_ID_EXTRA) != null){
                fridgeId = extras.getInt(FRIDGE_ID_EXTRA);
            }
        }

        if (isInDb()){
            Log.wtf("INDB","Product in db");
        }else {
            Log.wtf("INDB","Product not in db");
            getProductInfoOFF();
        }

        fridges = loadFridgeList();
        loadSpinner();
    }

    private void loadSpinner() {
        Spinner spinner = (Spinner)findViewById(R.id.fridge_spinner);
        ArrayAdapter spinnerArrayAdapter= new ArrayAdapter(this,android.R.layout.simple_spinner_item,fridges);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
    }

    public Fridge[] loadFridgeList(){
        SharedPreferences preferences = this.getSharedPreferences(SHARED_PREF_FRIDGES_NAME, Context.MODE_PRIVATE);
        int size = preferences.getInt(USER_NB_FRIDGES_PREFS,0);
        Fridge[] fridgeList = new Fridge[size];
        for (int i=0;i<size;i++){
            Gson gson = new Gson();
            String json = preferences.getString(USER_FRIDGE_PREFS+i,"");
            fridgeList[i] = gson.fromJson(json,Fridge.class);
        }
        return fridgeList;
    }

    private int getUserId() {
        SharedPreferences preferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(USER_ID_PREFS,0);
    }

    private String getApiKey(){
        SharedPreferences preferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(USER_API_KEY,"");
    }

    private Boolean isInDb() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, PRODUCT_DB_LOCATION_CHECK_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.wtf("RESISiNDB",response);
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

                        if (server_status.equals(SERVER_PROD_NOTINDB)){
                            prodInDb = false;
                        }else if (server_status.equals(SERVER_SUCCESS)){
                            prodInDb = true;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(VOLLEY_ERROR_TAG,error.toString());
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
        return prodInDb;
    }

    private String signParamsIsInDb(String barcode) {
        String jwt = "";

        final JWTSigner signer = new JWTSigner(secret);
        final HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put(PRODUCT_S_ID_DB,barcode);
        return jwt = signer.sign(claims);
    }

    private void getProductInfoOFF() {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "http://fr.openfoodfacts.org/api/v0/product/" + barcode + ".json",new Response.Listener<JSONObject>() {
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
                        Log.e(VOLLEY_ERROR_TAG,error.toString());
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.add_product_btn:
                addProductSFridge();
                break;
            case R.id.cancel_add_btn:
                startMainActivity();
                break;
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    public void addProductSFridge() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_PRODUCT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final JWTVerifier verifier = new JWTVerifier(secret);
                            final Map<String, Object> claims= verifier.verify(response);
                            server_response = new JSONObject(claims);
                            server_status = server_response.getString(SERVER_STATUS);
                        } catch (JWTVerifyException e) {
                            Log.e("JWT ERROR",e.toString());
                        } catch (NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException | JSONException e) {
                            e.printStackTrace();
                        }

                        if (server_status.equals(SERVER_SUCCESS)){
                            Toast.makeText(getApplicationContext(),getString(R.string.product_add_fridge_success)+" : "+fridge_selected,Toast.LENGTH_LONG).show();
                            startMainActivity();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(VOLLEY_ERROR_TAG,error.toString());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String apiKey = getApiKey();
                int userId = getUserId();
                String jwt = signParamsAddProductS(apiKey,userId,barcode,productName,brands,fridge_selected,imageUrl,quantity);
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                params.put(JWT_POST, jwt);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, PRODUCT_ADD_REQUEST_TAG);
    }

    public String signParamsAddProductS(String apiKey, int userId, String barcode, String productName, String brands, String fridge_selected, String imageUrl, String quantity) {
        final String secret = JWT_KEY;
        String jwt = "";

        final JWTSigner signer = new JWTSigner(secret);
        final HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put(KEY_USERID,userId);
        claims.put(KEY_API_KEY,apiKey);
        claims.put(KEY_PRODUCT_S_ID,barcode);
        claims.put(KEY_PRODUCT_NAME,productName);
        claims.put(KEY_PRODUCT_BRAND,brands);
        claims.put(KEY_FRIDGE_NAME,fridge_selected);
        claims.put(KEY_PRODUCT_IMAGEURL,imageUrl);
        claims.put(KEY_PRODUCT_QUANTITY,quantity);
        return jwt = signer.sign(claims);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        fridge_selected = adapterView.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // TODO: 26-11-16
    }
}
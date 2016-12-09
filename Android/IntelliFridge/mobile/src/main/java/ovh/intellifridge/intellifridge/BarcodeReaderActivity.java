package ovh.intellifridge.intellifridge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

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
import static ovh.intellifridge.intellifridge.Config.FRIDGE_ID_EXTRA;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_NAME_EXTRA;
import static ovh.intellifridge.intellifridge.Config.GET_INFO_OFF_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.JWT_KEY;
import static ovh.intellifridge.intellifridge.Config.JWT_POST;
import static ovh.intellifridge.intellifridge.Config.KEY_API_KEY;
import static ovh.intellifridge.intellifridge.Config.KEY_FRIDGE_ID;
import static ovh.intellifridge.intellifridge.Config.KEY_FRIDGE_NAME;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_BRAND;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_IMAGEURL;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_NAME;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_NS_ID;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_PRESENT;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_QUANTITY;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_SCANNABLE;
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
import static ovh.intellifridge.intellifridge.Config.SCAN_ALLERGY;
import static ovh.intellifridge.intellifridge.Config.SCAN_FRIDGE;
import static ovh.intellifridge.intellifridge.Config.SCAN_TYPE_EXTRA;
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

public class BarcodeReaderActivity extends AppCompatActivity {
    String scan_type, fridge_selected_name,fridge_selected_id;
    String barcode,imageUrl,productName,quantity, off_status,brands, server_status;
    JSONObject product,server_response;
    String secret = JWT_KEY;
    Boolean prodInDb = false;
    int fridgeId;
    Fridge[] fridges;
    TextView txtProduct,txtQuantity;
    View addProductCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reader);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            scan_type = extras.getString(SCAN_TYPE_EXTRA);
            if (extras.getString(FRIDGE_NAME_EXTRA) != null){
                fridge_selected_name = extras.getString(FRIDGE_NAME_EXTRA);
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
        initiateScan();
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(BarcodeReaderActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        addProductCard = inflater.inflate(R.layout.add_product_s_card,null);
        if (off_status.equals(OFF_STATUS_FOUND)){
            product = response.getJSONObject(OFF_PRODUCT);
            if (product.has(OFF_IMAGE_URL)) {
                imageUrl = product.getString(OFF_IMAGE_URL);
                ImageView productImage = (ImageView) addProductCard.findViewById(R.id.product_image);
                Picasso.with(getBaseContext()).load(imageUrl)
                        .fit().centerInside()
                        .into(productImage);
            }
            if (product.has(OFF_PRODUCTNAME)){
                productName = product.getString(OFF_PRODUCTNAME);
            }else if (product.has(OFF_PRODUCTNAME_EN)){
                productName = product.getString(OFF_PRODUCTNAME_EN);
            }else if (product.has(OFF_PRODUCTNAME_FR)){
                productName = product.getString(OFF_PRODUCTNAME_FR);
            }
            if (product.has(OFF_BRANDS)){
                brands = product.getString(OFF_BRANDS);
            }
            txtProduct = (TextView)addProductCard.findViewById(R.id.productName);
            txtProduct.setText(productName);
            quantity = product.getString(OFF_QUANTITY);
            txtQuantity = (TextView)addProductCard.findViewById(R.id.quantityName);
            txtQuantity.setText(quantity);
        }else {
            // TODO: 26-11-16
        }

        builder.setView(addProductCard);
        builder.setTitle(R.string.add_product_title);
        builder.setPositiveButton(R.string.add_fridge_addBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Log.wtf("ADD","PRESSED");
                addProductSFridge();
                startMainActivity();
            }
        });
        builder.setNegativeButton(R.string.add_fridge_cancelBtn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startMainActivity();
            }
        });
        final AlertDialog alertDialog = builder.create();
        final Spinner spinner = (Spinner)addProductCard.findViewById(R.id.fridge_spinner);
        ArrayAdapter spinnerArrayAdapter= new ArrayAdapter(this,android.R.layout.simple_spinner_item,fridges);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                fridge_selected_name = adapterView.getItemAtPosition(position).toString();
                for (int i=0;i<fridges.length;i++){
                    if (fridge_selected_name.equals(fridges[i].getFridgeName())){
                        fridgeId = fridges[i].getFridgeId();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void addProductSFridge() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_PRODUCT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.wtf("RES",response);
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
                            Toast.makeText(getApplicationContext(),getString(R.string.product_add_fridge_success)+" : "+fridge_selected_name,Toast.LENGTH_LONG).show();
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
                int produit_ns_id = 1;
                int isScannable = 1;
                int isPresent = 1;
                fridge_selected_id = String.valueOf(fridgeId);
                String jwt = signParamsAddProductS(apiKey,userId,fridge_selected_id,produit_ns_id,barcode,isPresent,isScannable,quantity,imageUrl,fridge_selected_name);
                Log.wtf("req",jwt);
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                params.put(JWT_POST, jwt);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, PRODUCT_ADD_REQUEST_TAG);
    }

    public String signParamsAddProductS(String apiKey, int userId, String fridge_selected_id, int produit_ns_id, String barcode, int isPresent, int isScannable, String quantity, String imageUrl, String fridge_selected_name) {
        final String secret = JWT_KEY;
        String jwt = "";

        final JWTSigner signer = new JWTSigner(secret);
        final HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put(KEY_USERID,userId);
        claims.put(KEY_API_KEY,apiKey);
        claims.put(KEY_PRODUCT_NS_ID,produit_ns_id);
        claims.put(KEY_PRODUCT_S_ID,barcode);
        claims.put(KEY_PRODUCT_NAME,productName);
        claims.put(KEY_PRODUCT_BRAND,brands);
        claims.put(KEY_FRIDGE_ID,fridge_selected_id);
        claims.put(KEY_PRODUCT_IMAGEURL,imageUrl);
        claims.put(KEY_PRODUCT_QUANTITY,quantity);
        claims.put(KEY_PRODUCT_SCANNABLE,isScannable);
        claims.put(KEY_PRODUCT_PRESENT,isPresent);
        claims.put(KEY_FRIDGE_NAME,fridge_selected_name);
        return jwt = signer.sign(claims);
    }

    public void initiateScan(){
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        setScannerOptions(scanIntegrator);
        scanIntegrator.initiateScan();
    }

    public void setScannerOptions(IntentIntegrator intentIntegrator){
        if (scan_type.equals(SCAN_FRIDGE)){
            intentIntegrator.setPrompt(getString(R.string.scanner_prompt_fridge));
        }else if (scan_type.equals(SCAN_ALLERGY)){
            intentIntegrator.setPrompt(getString(R.string.scanner_prompt_allergy));
        }
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setBeepEnabled(true);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null){
            barcode = scanningResult.getContents();
            if (scan_type.equals(SCAN_FRIDGE)){
                getProductInfoOFF();
            }else if (scan_type.equals(SCAN_ALLERGY)){
                // TODO: 09-12-16
            }
        }else {
            Toast.makeText(getApplicationContext(), R.string.scan_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        startMainActivity();
    }
}

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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static ovh.intellifridge.intellifridge.Config.ADD_PRODUCT_LOCAL_DB;
import static ovh.intellifridge.intellifridge.Config.ADD_PRODUCT_LOCAL_DB_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.BARCODE_CHECK_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.BARCODE_EXTRA;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_LIST_PREFS;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_NAME_DB;
import static ovh.intellifridge.intellifridge.Config.GET_INFO_LOCAL_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.GET_INFO_OFF_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.GET_PRODUCT_INFO_LOCAL_URL;
import static ovh.intellifridge.intellifridge.Config.KEY_BARCODE;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_BRAND;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_ID;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_IMAGEURL;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_QUANTITY;
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
import static ovh.intellifridge.intellifridge.Config.SHARED_PREF_NAME;

public class ProductScanActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,View.OnClickListener{
    String barcode,url_off,imageUrl,productName,quantity, off_status, fridge_selected,brands;
    TextView txtQuantity, txtProduct;
    JSONObject product;
    AppCompatButton addProductFridge,cancelAddProduct;

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
                        /*try {
                            JSONObject jsonObject = new JSONObject(response);
                            String server_status = jsonObject.getString(SERVER_STATUS);
                            String server_response = jsonObject.getString(SERVER_RESPONSE);
                            if (server_response.equals(BARCODE_NOT_IN_LOCALDB)){
                                getProductInfoOFF();
                            }else if (server_response.equals(BARCODE_IN_LOCALDB)){
                                JSONObject product_id = jsonObject.getJSONObject(DATA);
                                int product_id_localdb = product_id.getInt(PRODUCT_S_ID);
                                getProductInfoLocalDb(product_id_localdb);
                            }else if (server_status.equals(DB_CONNECTION_ERROR)){
                                Toast.makeText(getApplicationContext(),R.string.db_connect_error,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
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
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                params.put(KEY_BARCODE, barcode);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, BARCODE_CHECK_REQUEST_TAG);
    }

    private void getProductInfoLocalDb(final int product_id_localdb) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_PRODUCT_INFO_LOCAL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /*try {
                            JSONObject jsonObject = new JSONObject(response);
                            String server_status = jsonObject.getString(SERVER_STATUS);
                            String server_response = jsonObject.getString(SERVER_RESPONSE);
                            if (server_response.equals(PRODUCT_INFO_AVAILABLE)){
                                JSONObject product_info = jsonObject.getJSONObject(DATA);
                                //displayProductInfo(product_info);
                            }else if (server_response.equals(PRODUCT_INFO_UNAVAILABLE)){
                                Toast.makeText(getApplicationContext(),getString(R.string.product_info_unavailable),Toast.LENGTH_LONG).show();
                            }else if (server_status.equals(DB_CONNECTION_ERROR)){
                                Toast.makeText(getApplicationContext(),R.string.db_connect_error,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
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
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                params.put(KEY_PRODUCT_ID, String.valueOf(product_id_localdb));
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, GET_INFO_LOCAL_REQUEST_TAG);
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
                        /*try {
                            String server_status = response.getString(SERVER_STATUS);
                            String server_response = response.getString(SERVER_RESPONSE);
                            Log.wtf("TEST",response.toString());
                            if (server_response.equals(PRODUCT_LOCAL_ADD_ERROR)){
                                Toast.makeText(getApplicationContext(),getString(R.string.product_add_local_db_error),Toast.LENGTH_LONG).show();
                            }else if (server_status.equals(DB_CONNECTION_ERROR)){
                                Toast.makeText(getApplicationContext(),R.string.db_connect_error,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
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

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                Log.wtf("BARCODE",barcode);
                params.put(KEY_BARCODE,barcode);
                params.put(KEY_PRODUCT_BRAND,brands);
                params.put(KEY_PRODUCT_QUANTITY,quantity);
                params.put(KEY_PRODUCT_IMAGEURL,imageUrl);
                return params;
            }
        };

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsObjRequest, ADD_PRODUCT_LOCAL_DB_REQUEST_TAG);
    }

    private void addProductFridge() {

    }

    private void startMainActivity() {
        Intent intent = new Intent(ProductScanActivity.this,MainActivity.class);
        startActivity(intent);
    }
}

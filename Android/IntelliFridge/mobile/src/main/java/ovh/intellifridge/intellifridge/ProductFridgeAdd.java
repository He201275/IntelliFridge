package ovh.intellifridge.intellifridge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static ovh.intellifridge.intellifridge.Config.BARCODE_EXTRA;
import static ovh.intellifridge.intellifridge.Config.DATA;
import static ovh.intellifridge.intellifridge.Config.DB_CONNECTION_ERROR;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_LIST_PREFS;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_NAME_DB;
import static ovh.intellifridge.intellifridge.Config.LOGIN_ERROR;
import static ovh.intellifridge.intellifridge.Config.LOGIN_SUCCESS;
import static ovh.intellifridge.intellifridge.Config.LOGIN_URL;
import static ovh.intellifridge.intellifridge.Config.OFF_DATA_PREFS;
import static ovh.intellifridge.intellifridge.Config.SERVER_RESPONSE;
import static ovh.intellifridge.intellifridge.Config.SERVER_STATUS;
import static ovh.intellifridge.intellifridge.Config.SHARED_PREF_NAME;

public class ProductFridgeAdd extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    TextView txtProduct, txtQuantity;
    String genericName, imageUrl, cat, ingredients, productName, quantity, barcode, url_off;
    JSONObject product,json,code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_fridge_add);
        getBarcodeNumber();
        url_off = "http://fr.openfoodfacts.org/api/v0/product/" +barcode+ ".json";
        Log.wtf("URL",url_off);
        getDataOff();

        txtProduct = (TextView) findViewById(R.id.productName);
        txtQuantity = (TextView) findViewById(R.id.quantityName);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        String off = prefs.getString(OFF_DATA_PREFS,"");
        /*try {
            if (json.has("product")) {
                product = json.getJSONObject("product");
                if (product.has("generic_name")){
                    genericName = product.getString("generic_name");
                }
                if (product.has("image_url")) {
                    imageUrl = product.getString("image_url");
                    ImageView pic1 = (ImageView) findViewById(R.id.pic1);
                    Picasso.with(getBaseContext()).load(imageUrl)
                            .fit().centerInside()
                            .into(pic1);
                }
                if (product.has("categories")) {
                    cat = product.getString("categories");
                    TextView txtProductCat = (TextView) findViewById(R.id.productCat);
                    txtProductCat.setText(cat);
                }
                if (product.has("ingredients")){
                    ingredients = product.getString("ingredients");
                    TextView txtIngredients = (TextView) findViewById(R.id.productIngredients);
                    txtIngredients.setText(ingredients);
                }else if (product.has("ingredients_text_with_allergens_en")){
                    ingredients = product.getString("ingredients_text_with_allergens_en");
                    TextView txtIngredients = (TextView) findViewById(R.id.productIngredients);
                    txtIngredients.setText(ingredients);
                }
                if (product.has("quantity")){
                    quantity = product.getString("quantity");
                    txtQuantity = (TextView) findViewById(R.id.quantityName);
                    txtQuantity.setText(quantity);
                }
                if (product.has("brands")){
                    String brands = product.getString("brands");
                    TextView txtBrands = (TextView) findViewById(R.id.productBrands);
                    txtBrands.setText(brands);
                }
                if (product.has("product_name")) {
                    productName = product.getString("product_name");
                } else if (product.has("product_name_en")) {
                    productName = product.getString("product_name_en");
                } else if (product.has("product_name_fr")) {
                    productName = product.getString("product_name_fr");
                }

                txtProduct.setText(productName);
            } else {
                Log.e("ProductScan", "Error retrieving product!");
            }

            if (json.has("code")) {
                code = json.getJSONObject("code");
            } else {
                //TODO
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        Spinner spinner = (Spinner)findViewById(R.id.fridge_spinner);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        JSONArray fridge_list_json = null;
        String[] list= null;
        String fridge_list_string = sharedPreferences.getString(FRIDGE_LIST_PREFS,"");
        try {
            fridge_list_json = new JSONArray(fridge_list_string);
            list = getStringArrayFridge(fridge_list_json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> fridgeListAdapter= new ArrayAdapter(this,android.R.layout.simple_spinner_item,list);
        fridgeListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(fridgeListAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void getBarcodeNumber() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            barcode = extras.getString(BARCODE_EXTRA);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    private void getDataOff(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url_off, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.wtf("TEST", String.valueOf(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonObjectRequest);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

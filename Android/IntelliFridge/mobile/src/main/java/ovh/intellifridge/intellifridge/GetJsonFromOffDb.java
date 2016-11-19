package ovh.intellifridge.intellifridge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Classe permettant de récolter les infos à propos du numéro de code barres reçu par la classe BarcodeReader, de la bdd OpenFoodFacts
 * Reçoit les infos en JSON
 * Affiche les informations récoltées dans une vue
 */
public class GetJsonFromOffDb extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView txtProduct, txtQuantity;
    ProgressDialog pd;
    String resBarcodeReader;
    JSONObject code, product, json;
    String genericName, imageUrl, cat, ingredients, productName, quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_json_from_off_db);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            resBarcodeReader = extras.getString("Scanned Barcode");
        }

        new JsonTask().execute("http://fr.openfoodfacts.org/api/v0/product/" + resBarcodeReader + ".json");
        txtProduct = (TextView) findViewById(R.id.productName);
        txtQuantity = (TextView) findViewById(R.id.quantityName);

        Spinner spinner = (Spinner)findViewById(R.id.fridge_spinner);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        JSONArray fridge_list_json = null;
        String[] list= null;
        String fridge_list_string = prefs.getString("fridge_list","");
        try {
            fridge_list_json = new JSONArray(fridge_list_string);
            list = getStringArrayFridge(fridge_list_json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> fridgeList= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,list);
        fridgeList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(fridgeList);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GetJsonFromOffDb.this,MainActivity.class);
        startActivity(intent);
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
                stringArray[i] = jsonObj.getString("FrigoNom");
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

    private class JsonTask extends AsyncTask <String,String,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(GetJsonFromOffDb.this);
            pd.setMessage(getString(R.string.pdMessage));
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader((new InputStreamReader(stream)));
                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            try {
                json = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
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
            }
        }
    }
}

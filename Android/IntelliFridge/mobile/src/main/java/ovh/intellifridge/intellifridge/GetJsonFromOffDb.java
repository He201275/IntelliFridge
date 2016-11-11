package ovh.intellifridge.intellifridge;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetJsonFromOffDb extends AppCompatActivity {
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
                    Log.d("Response: ", "> " + line); //here u ll get whole response...... :-)
                }

                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException /*| JSONException*/ e) {
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
                    Log.d("tamere", product.toString());
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
                        String brands = new String();
                        brands = product.getString("brands");
                        TextView txtBrands = (TextView) findViewById(R.id.productBrands);
                        txtBrands.setText(brands);
                    }
                    /*
                    A optimiser si possible :-)
                     */
                    if (product.has("product_name")) {
                        Log.d("ProductScan", "Product_name excist");
                        productName = product.getString("product_name");
                    } else if (product.has("product_name_en")) {
                        Log.d("ProductScan", "Product_name_en excist");
                        productName = product.getString("product_name_en");

                    } else if (product.has("product_name_fr")) {
                        Log.d("ProductScan", "Product_name_fr excist");
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

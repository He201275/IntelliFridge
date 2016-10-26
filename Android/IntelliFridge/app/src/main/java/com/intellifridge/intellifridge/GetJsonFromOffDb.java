package com.intellifridge.intellifridge;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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
    TextView txtJson, txtProduct, txtQuantity;
    ProgressDialog pd;
    String resBarcodeReader;
    JSONObject code, product, genericName, imageUrl, imageSmallUrl, imageFrontUrl, ingredients, productName, quantity, json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_json_from_off_db);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            resBarcodeReader = extras.getString("Scanned Barcode");
        }

        new JsonTask().execute("http://fr.openfoodfacts.org/api/v0/product/"+resBarcodeReader+".json");
        txtJson = (TextView) findViewById(R.id.textViewGPO);
        txtProduct = (TextView) findViewById(R.id.productName);
        txtQuantity = (TextView) findViewById(R.id.quantityName);
    }

    private class JsonTask extends AsyncTask<String, String, String>{
        protected  void onPreExecute(){
            super.onPreExecute();

            pd = new ProgressDialog(GetJsonFromOffDb.this);
            pd.setMessage(getString(R.string.pdMessage));
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params){
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

                while ((line = reader.readLine()) != null){
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line); //here u ll get whole response...... :-)
                }

                //Getting required fields from JSON
                /**/

                return buffer.toString();
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException /*| JSONException*/ e){
                e.printStackTrace();
            }finally {
                if (connection != null){
                    connection.disconnect();
                }try {
                    if (reader != null){
                        reader.close();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }
            try {
                json = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //txtJson.setText(result);
            Log.d("IntelliFridge", json.toString());

            try {
                if (json.has("product")){
                    product = json.getJSONObject("product");
                    Log.d("IntelliFridge",product.toString());
                    genericName = product.getJSONObject("generic_name");
                    imageUrl = product.getJSONObject("image_url");
                    imageSmallUrl = product.getJSONObject("image_small_url");
                    imageFrontUrl = product.getJSONObject("image_front_url");
                    ingredients = product.getJSONObject("ingredients");
                    quantity = product.getJSONObject("quantity");
                    productName = product.getJSONObject("product_name");
                    productName = product.getJSONObject("product");
                    quantity = product.getJSONObject("quantity");

                    txtProduct.setText(productName.toString());
                    txtQuantity.setText(quantity.toString());
                }else {
                    Log.e("IntelliFridge","Error retrieving product!");
                }

                if (json.has("code")){
                    code = json.getJSONObject("code");
                }else {
                    //TODO
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
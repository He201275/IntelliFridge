package com.intellifridge.intellifridge;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetProdOFF extends AppCompatActivity {
    TextView txtJson;
    ProgressDialog pd;
    String resBarcodeReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_prod_off);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            resBarcodeReader = extras.getString("Scanned Barcode");
        }

        new JsonTask().execute("http://fr.openfoodfacts.org/api/v0/product/"+resBarcodeReader+".json");
        txtJson = (TextView) findViewById(R.id.textViewGPO);
    }

    private class JsonTask extends AsyncTask<String, String, String>{
        protected  void onPreExecute(){
            super.onPreExecute();

            pd = new ProgressDialog(GetProdOFF.this);
            pd.setMessage("Please Wait...");
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

                return buffer.toString();
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
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
            txtJson.setText(result);
        }
    }
}

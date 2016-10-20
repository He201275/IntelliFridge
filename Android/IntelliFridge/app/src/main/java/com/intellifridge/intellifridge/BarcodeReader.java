package com.intellifridge.intellifridge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class BarcodeReader extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initiateScan();
    }

    public void initiateScan(){
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.setPrompt(getString(R.string.scanner_prompt));
        scanIntegrator.setOrientationLocked(false);
        scanIntegrator.setBeepEnabled(true);
        scanIntegrator.initiateScan();
    }

    public void onClick(View view){
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null){
            String scanContent = scanningResult.getContents();
            Intent gpoIntent = new Intent(getApplicationContext(),GetJsonFromOffDb.class);
            gpoIntent.putExtra("Scanned Barcode",scanContent);
            startActivity(gpoIntent);
        }else {
            Toast.makeText(getApplicationContext(), "No Scan Data Received", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.intellifridge.intellifridge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class BarcodeReader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initiateScan();
    }

    public void initiateScan(){
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        setScannerOptions(scanIntegrator);
        scanIntegrator.initiateScan();
    }

    public void setScannerOptions(IntentIntegrator intentIntegrator){
        intentIntegrator.setPrompt(getString(R.string.scanner_prompt));
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setBeepEnabled(true);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null){
            String scanContent = scanningResult.getContents();
            Intent gpoIntent = new Intent(getApplicationContext(),GetJsonFromOffDb.class);
            gpoIntent.putExtra("Scanned Barcode",scanContent);
            startActivity(gpoIntent);
        }else {
            Toast.makeText(getApplicationContext(), R.string.scan_error, Toast.LENGTH_SHORT).show();
        }
    }
}

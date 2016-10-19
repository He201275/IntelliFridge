package com.intellifridge.intellifridge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.security.Permission;

import static java.util.logging.Logger.global;

public class BarcodeReader extends AppCompatActivity implements View.OnClickListener {
    private Button scanBtn;
    private TextView formatTxt, contextTxt;
    private String in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_reader);

        scanBtn = (Button)findViewById(R.id.scan_button);
        formatTxt = (TextView)findViewById(R.id.scan_format);
        contextTxt = (TextView)findViewById(R.id.scan_content);

        scanBtn.setOnClickListener(this);
    }

    public void onClick(View view){
        if(view.getId()==R.id.scan_button){
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.setPrompt(String.valueOf(R.string.scanner_prompt));
            scanIntegrator.setOrientationLocked(false);
            scanIntegrator.setBeepEnabled(true);
            scanIntegrator.initiateScan();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null){
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            formatTxt.setText("Format: " + scanFormat);
            contextTxt.setText("Content: " + scanContent);
        }else {
            Toast.makeText(getApplicationContext(), "No Scan Data Received", Toast.LENGTH_SHORT).show();
        }
    }
}

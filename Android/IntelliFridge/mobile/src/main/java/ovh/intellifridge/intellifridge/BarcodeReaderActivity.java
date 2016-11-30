package ovh.intellifridge.intellifridge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import static ovh.intellifridge.intellifridge.Config.BARCODE_EXTRA;
import static ovh.intellifridge.intellifridge.Config.SCAN_ALLERGY;
import static ovh.intellifridge.intellifridge.Config.SCAN_FRIDGE;
import static ovh.intellifridge.intellifridge.Config.SCAN_TYPE_EXTRA;

public class BarcodeReaderActivity extends AppCompatActivity {
    String scan_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            scan_type = extras.getString(SCAN_TYPE_EXTRA);
        }
        initiateScan();
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
        }else {
            intentIntegrator.setPrompt(getString(R.string.scanner_prompt_info));
        }
        intentIntegrator.setOrientationLocked(false);
        intentIntegrator.setBeepEnabled(true);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null){
            String scanContent = scanningResult.getContents();
            if (scan_type.equals(SCAN_FRIDGE)){
                Intent intent1 = new Intent(BarcodeReaderActivity.this,ProductScanActivity.class);
                intent1.putExtra(BARCODE_EXTRA,scanContent);
                startActivity(intent1);
            }else if (scan_type.equals(SCAN_ALLERGY)){
                Intent intent1 = new Intent(BarcodeReaderActivity.this,AllergyScanActivity.class);
                intent1.putExtra(BARCODE_EXTRA,scanContent);
                startActivity(intent1);
            }else {
                // TODO: 26-11-16
            }  
        }else {
            Toast.makeText(getApplicationContext(), R.string.scan_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BarcodeReaderActivity.this,MainActivity.class);
        startActivity(intent);
    }
}

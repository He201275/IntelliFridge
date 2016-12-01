package ovh.intellifridge.intellifridge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static ovh.intellifridge.intellifridge.Config.BARCODE_EXTRA;

public class ProductAllergyAnalysis extends AppCompatActivity {
    String barcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_allergy_analysis);
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            barcode = extras.getString(BARCODE_EXTRA);
        }
    }
}

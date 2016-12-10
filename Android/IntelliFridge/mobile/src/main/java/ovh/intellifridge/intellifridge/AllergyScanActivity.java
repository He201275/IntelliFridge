package ovh.intellifridge.intellifridge;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * @author Francis O. Makokha
 * Permet de gérer le scan pour la détection d'allergènes
 */
public class AllergyScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergy_scan);
    }
}

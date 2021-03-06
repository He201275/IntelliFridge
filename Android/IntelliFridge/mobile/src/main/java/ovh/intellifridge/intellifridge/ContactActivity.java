package ovh.intellifridge.intellifridge;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import static ovh.intellifridge.intellifridge.Config.CONTACT_URL;
import static ovh.intellifridge.intellifridge.Config.DOMAIN_URL;

/**
 * @author Francis O. Makokha
 * Activité de contact.
 * Affiche une vue de la page de contact du site vitrine @see <a href="http://intellifridge.ovh">IntelliFridge</a>
 */
public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        setTitle(R.string.title_activity_contact);

        WebView webView = (WebView)findViewById(R.id.contact_webview);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(CONTACT_URL);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    /**
     * Permet de créer le client web interne à l'activité
     * Reste dans l'application tant qu'on reste sur le domaine d'IntelliFridge
     * Renvoie au navigateur par défaut du smartphone, si on sort du domaine d'IntelliFridge
     */
    private class MyWebViewClient extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals(DOMAIN_URL)) {
                // This is my web site, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){
            if (request.getUrl().getHost().equals(DOMAIN_URL)){
                // This is my web site, so do not override; let my WebView load the page
                return false;
            }

            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
            startActivity(intent);
            return true;
        }
    }
}

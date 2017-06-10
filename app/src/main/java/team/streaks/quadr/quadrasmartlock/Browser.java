package team.streaks.quadr.quadrasmartlock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class Browser extends AppCompatActivity {
    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        String url = getIntent().getStringExtra("url");
        final String name = getIntent().getStringExtra("name");
        myWebView = (WebView) findViewById(R.id.myWebView);

        myWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                //Make the bar disappear after URL is loaded, and changes string to Loading...
                setTitle("Loading...");
                setProgress(progress * 100); //Make the bar disappear after URL is loaded

                // Return the app name after finish loading
                if (progress == 100)
                    setTitle(name);
            }
        });

        myWebView.setWebViewClient(new WebViewClient());
        myWebView.clearCache(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.loadUrl(url);

    }
}

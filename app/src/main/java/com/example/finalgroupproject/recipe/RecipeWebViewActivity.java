package com.example.finalgroupproject.recipe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.finalgroupproject.R;

public class RecipeWebViewActivity extends AppCompatActivity {
    /**
     * Web page view: After the user clicked the button of go to web page,
     * the source web page will be shown.
     */
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_web_view);

        String url=getIntent().getStringExtra(MainRecipeActivity.TITLE);

        //reference: https://stackoverflow.com/questions/6850017/how-to-set-webview-client
        //tell the WebviewClient that you've overloaded the URL loading
        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return true;
            }
        });
        webView.loadUrl(url);
    }
}//RecipeWebViewActivity

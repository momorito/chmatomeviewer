package jp.tanikinaapps.chviewer;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class WebActivity extends AppCompatActivity  {
    private WebView webView;
    public static final String DATA = "jp.tanikinaapps.chviewer.DATA";
    Toolbar toolbar;
    ProgressBar pBar;
    FavoriteOpenHelper fHelper;
    SQLiteDatabase fDb;
    SharedPreferences data;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        toolbar = findViewById(R.id.toolbarImage);
        setSupportActionBar(toolbar);
        // Backボタンを有効にする
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        pBar = findViewById(R.id.LoadingPage);

        data = getSharedPreferences("settingData",MODE_PRIVATE);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplication(),ImageActivity.class);
                intent.putExtra("jp.tanikinaapps.chviewer.DATA",webView.getUrl());
                startActivity(intent);
            }
        });
        if(data.getBoolean("useImageSearch",true)){
            //
        } else{
            fab.setVisibility(View.INVISIBLE);
        }

        //adMobの設定
        new SetAd((AdView)findViewById(R.id.adView),this,getResources().getString(R.string.ad_code)).AdSetting();

        webViewSettings();
        swipeRefresh();

        Intent intent = getIntent();
        if(intent.getStringExtra(DATA) != null){
            pBar.setVisibility(pBar.VISIBLE);
            customLoadUrl(intent.getStringExtra(DATA));
        }



    }


    private void webViewSettings(){
        data = getSharedPreferences("settingData",MODE_PRIVATE);
        Boolean useExpansion = data.getBoolean("useImageExpansion",false);
        Boolean removeJavascript = data.getBoolean("removeJavascript",false);

        webView = findViewById(R.id.webView);
        if(removeJavascript){
            webView.getSettings().setJavaScriptEnabled(false);
        } else{
            webView.getSettings().setJavaScriptEnabled(true);
        }

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);

        if(useExpansion){
            webView.getSettings().setBuiltInZoomControls(true);
        }

        webView.setWebViewClient(new WebViewClient(){
            TextView pageTitle = findViewById(R.id.pageTitle);

            @Override
            public void onPageStarted(WebView webView, String url, Bitmap favicon){
                toolbar.setTitle("");
                pageTitle.setText("読込中…");
                super.onPageStarted(webView,url,favicon);
                if(swipeRefreshLayout.isRefreshing()){
                    //
                } else {
                    pBar.setVisibility(pBar.VISIBLE);
                }
            }

            @Override
            public void onPageFinished(WebView webView,String url){
                super.onPageFinished(webView,url);
                pageTitle.setText(webView.getTitle());
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    pBar.setVisibility(pBar.INVISIBLE);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView,String url){
                boolean useOtherApp = data.getBoolean("useOtherApp",true);

                if(url.startsWith("mailto:")){
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                if(useOtherApp){
                    if(url.contains("imgur.com") || url.contains("instagram.com") || url.contains("twitter.com") || url.contains("youtube.com") || url.contains("nicovideo.jp")){
                        Intent i = new Intent();
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        return true;
                    }
                }
                webView.loadUrl(url);
                return true;
            }

        });
        webView.setWebChromeClient(new WebChromeClient(){


        });


        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
    }

    private void customLoadUrl(String url){
        pBar.setVisibility(pBar.VISIBLE);
        webView.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent();
        switch(item.getItemId()){
            case R.id.action_reboot:
                webView.reload();
                break;
            case R.id.action_share:
                String shareText = webView.getTitle() + " " + webView.getUrl() + " #ChMatomeViewer";
                i.setAction(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT,shareText);
                startActivity(i);
                break;
            case R.id.open_other_app:
                i.setAction(Intent.ACTION_VIEW);
                i.setData(Uri.parse(webView.getUrl()));
                startActivity(i);
                break;
            case R.id.action_setting:
                i = new Intent(WebActivity.this,SettingActivity.class);
                startActivity(i);
                break;
            case R.id.action_favorite:
                if(fHelper == null){
                    fHelper = new FavoriteOpenHelper(getApplicationContext());
                }
                if(fDb == null){
                    fDb = fHelper.getWritableDatabase();
                }

                insertFavoriteData(fDb,
                        webView.getTitle(),
                        webView.getUrl(),
                        null,
                        null);
                Toast.makeText(getApplicationContext(),"お気に入りに登録しました",Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }
    private void insertFavoriteData(SQLiteDatabase fDb,String title,String url,String date,String blogName){
        ContentValues values = new ContentValues();
        values.put("articleTitle",title);
        values.put("articleAddress",url);
        values.put("articleUpDate",date);
        values.put("blogName",blogName);

        fDb.insert("favoritedb",null,values);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_BACK && !webView.canGoBack()){
            finish();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    public void swipeRefresh(){
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        data = getSharedPreferences("settingData",MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        webView.onResume();
        if(data.getBoolean("checkChange",true)){
            webView.clearCache(true);
            webViewSettings();
            webView.loadUrl(webView.getUrl());
            editor.putBoolean("checkChange",false);
            editor.apply();
        }

    }
    @Override
    public void onPause(){
        super.onPause();
        webViewSettings();
    }
    @Override
    public void onDestroy(){
        webView.clearCache(true);
        //クッキー削除する記述
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        webView.destroy();
        super.onDestroy();
    }


}

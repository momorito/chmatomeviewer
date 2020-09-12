package jp.tanikinaapps.chviewer;


import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.psdev.licensesdialog.LicensesDialogFragment;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

public class MainActivity extends AppCompatActivity {
    private ArticleOpenHelper helper;
    private SQLiteDatabase db;
    private ListView listView;
    List<Map<String, String>> allData,checkData;
    AsyncGetArticle[] asyncGetArticles;
    private int readCount = 0;
    private ProgressBar progressBar;
    private static String version;

    private FavoriteOpenHelper fHelper;
    private SQLiteDatabase fDb;

    private NgWordOpenHelper nHelper;
    private SQLiteDatabase nDb;

    SharedPreferences settingData;
    SwipeRefreshLayout swipeRefreshLayout;

    int blogCount,readBlogCount;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String useColor;
        super.onCreate(savedInstanceState);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        setTheme(R.style.AppTheme_NoActionBar);

        settingData = this.getSharedPreferences("settingData",MODE_PRIVATE);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbarImage);
        setSupportActionBar(toolbar);



        try{
            String packageName = getPackageName();
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName,0);
            version = packageInfo.versionName;
        } catch(Exception e){
            e.printStackTrace();
        }

        if(helper == null){
            helper = new ArticleOpenHelper(getApplicationContext());
        }
        if(db == null){
            db = helper.getWritableDatabase();
            db = helper.getReadableDatabase();
        }

        //adMobの設定
        new SetAd((AdView)findViewById(R.id.adView),this,getResources().getString(R.string.ad_code)).AdSetting();
        swipeRefresh();

        doExcute();


        snackbar = Snackbar.make((View)findViewById(R.id.cl),R.string.loading,Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("設定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(i);
            }
        });

        snackbar.show();

    }
    
    private void doExcute(){
        TypedArray typedArray = getResources().obtainTypedArray(R.array.listBlogs);
        blogCount = typedArray.length();
        String[] blogUrl = new String[blogCount];
        ArrayList<String> arrayList = new ArrayList<>();


        //取り除くブログの処理
        final String blog = "blog";
        Boolean[] readBlogs = new Boolean[blogCount];

        for(int i =0;i<blogCount;i++){
            readBlogs[i] = settingData.getBoolean(blog + (i),true);
        }

        for(int i =0;i<blogCount;i++){
            int resourceId = typedArray.getResourceId(i,0);
            String[] array = getResources().getStringArray(resourceId);
            if(readBlogs[i]){
                arrayList.add(array[1]);
            }
        }
        readBlogCount = arrayList.size();

        //blog選択の処理
        for(int i =0;i<blogCount;i++){
            int resourceId = typedArray.getResourceId(i,0);
            String[] array = getResources().getStringArray(resourceId);
            blogUrl[i] = array[1];
        }

        asyncGetArticles = new AsyncGetArticle[arrayList.size()];
        for(int i = 0;i<arrayList.size();i++){
            asyncGetArticles[i] = new AsyncGetArticle();
            asyncGetArticles[i].setListener(createListener());
            asyncGetArticles[i].executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, arrayList.get(i));
        }
        toastSome("記事を読込中…");
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(blogCount);
        progressBar.setVisibility((ProgressBar.VISIBLE));
    }

    private AsyncGetArticle.Listener createListener(){
        return new AsyncGetArticle.Listener() {
            @Override
            public void onSuccess(List<Map<String, String>> data) {

                if (readCount == 0){
                    deleteData();
                }
                readCount++;
                progressBar.setProgress(readCount);
                insertData(db,data);

                if(readCount == readBlogCount){
                    readData();
                    progressBar.setVisibility(progressBar.INVISIBLE);
                    readCount = 0;
                    progressBar.setProgress(readCount);
                    swipeRefreshLayout.setRefreshing(false);
                    snackbar.dismiss();
                }
            }
        };
    }

    private void insertData(SQLiteDatabase db,List<Map<String,String>> data){
        ContentValues values = new ContentValues();
        for(int i = 0;i<data.size();i++){
            values.put("articleTitle",data.get(i).get("pageTitleString"));
            values.put("articleAddress",data.get(i).get("pageUrlString"));
            values.put("articleUpDate",data.get(i).get("pageDateString"));
            values.put("blogName",data.get(i).get("blogTitleString"));

            db.insert("articledb",null,values);
        }
    }

    private void insertFavoriteData(SQLiteDatabase fDb,String title,String url,String date,String blogName){
        ContentValues values = new ContentValues();
            values.put("articleTitle",title);
            values.put("articleAddress",url);
            values.put("articleUpDate",date);
            values.put("blogName",blogName);

            fDb.insert("favoritedb",null,values);

    }

    private void readData(){
        allData = new ArrayList<Map<String,String>>();
        if(helper == null){
            helper = new ArticleOpenHelper(getApplicationContext());
        }
        if(db == null){
            db = helper.getReadableDatabase();
        }
        Cursor cursor = db.query("articledb",new String[]{ "articleTitle","articleAddress","articleUpDate","blogName"},null,null,null,null,"articleUpDate DESC");
        cursor.moveToFirst();


        for(int i = 0;i<cursor.getCount();i++){
            Map<String,String> item = new HashMap<String,String>();
            item.put("pageTitle",cursor.getString(0));
            item.put("pageUrl",cursor.getString(1));
            item.put("pageDate",cursor.getString(2));
            item.put("blogName",cursor.getString(3));
            allData.add(item);
            cursor.moveToNext();
        }
        cursor.close();

        setAllData(allData);


        if(nHelper == null){
            nHelper = new NgWordOpenHelper(getApplicationContext());
        }
        if(nDb == null){
            nDb = nHelper.getReadableDatabase();
        }

        Cursor ngCursor = nDb.query("ngworddb",new String[]{ "ngword"},null,null,null,null,null);
        ngCursor.moveToFirst();

        checkData = new ArrayList<>();

        for(int i = 0;i<ngCursor.getCount();i++){
            Map<String,String> item = new HashMap<String,String>();
            item.put("ngword",ngCursor.getString(0));
            checkData.add(item);
            ngCursor.moveToNext();
        }
        ngCursor.close();


        for(int n = 0;n<checkData.size();n++){
            for(int i = 0;i<allData.size();i++){
                if(allData.get(i).get("pageTitle").contains(checkData.get(n).get("ngword"))){
                    allData.remove(i);
                }
            }
        }



        listView = findViewById(R.id.contentsList);
        BaseAdapter adapter = new SetAdapter.ListAdapter(getApplicationContext(),R.layout.list_contents,allData);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplication(),WebActivity.class);
                intent.putExtra("jp.tanikinaapps.chviewer.DATA",allData.get(position).get("pageUrl"));
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(fHelper == null){
                    fHelper = new FavoriteOpenHelper(getApplicationContext());
                }
                if(fDb == null){
                    fDb = fHelper.getWritableDatabase();
                }

                insertFavoriteData(fDb,
                        getAllData().get(position).get("pageTitle"),
                        getAllData().get(position).get("pageUrl"),
                        getAllData().get(position).get("pageDate"),
                        getAllData().get(position).get("blogName"));
                toastSome("お気に入りに登録しました");

                return true;
            }
        });

    }

    private void deleteData(){
        if(helper == null){
            helper = new ArticleOpenHelper(getApplicationContext());
        }
        if(db == null){
            db = helper.getWritableDatabase();
        }
        db.delete("articledb",null,null);
    }

    private void setAllData(List<Map<String, String>> allData){
        this.allData = allData;
    }
    private List<Map<String, String>> getAllData(){
        return allData;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_reboot:
                readCount = 0;
                deleteData();
                readData();
                doExcute();
                break;
            case R.id.action_settings:
                Intent i = new Intent(MainActivity.this,SettingActivity.class);
                startActivity(i);
                break;
            case R.id.action_search:
                FragmentManager searchFragmentManager = getSupportFragmentManager();
                DialogFragment searchDialogFragment = new SetDialog.SearchDialogFragment();
                searchDialogFragment.show(searchFragmentManager,"search");
                break;
            case R.id.action_favorite:
                Intent ii = new Intent(MainActivity.this,FavoriteActivity.class);
                startActivity(ii);
                break;
            case R.id.action_about:
                try {
                    String packageName = getPackageName();
                    PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
                    version = packageInfo.versionName;
                } catch(Exception e){
                    e.printStackTrace();
                }

                FragmentManager aboutFragmentManager = getSupportFragmentManager();
                DialogFragment aboutDialogFragment = new SetDialog.aboutDialogFragment(version);
                aboutDialogFragment.show(aboutFragmentManager,"about");
                break;
            case R.id.action_license:
                final Notices notices = new Notices();
                notices.addNotice(new Notice("jsoup Java HTML Parser", "https://jsoup.org/", "Copyright  2009– Jonathan Hedley", new MITLicense()));
                notices.addNotice(new Notice("Picasso", "https://square.github.io/picasso/", "Copyright 2013 Square, Inc.", new ApacheSoftwareLicense20()));
                notices.addNotice(new Notice("OkHttp", "https://square.github.io/okhttp/", "Copyright 2019 Square, Inc.", new ApacheSoftwareLicense20()));


                final LicensesDialogFragment fragment = new LicensesDialogFragment.Builder(getApplicationContext())
                        .setNotices(notices)
                        .setShowFullLicenseText(false)
                        .setIncludeOwnLicense(true)
                        .build();

                fragment.show(getSupportFragmentManager(), null);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void toastSome(String text){
        Toast toast = Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT);
        toast.show();
    }

    public void swipeRefresh(){
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                deleteData();
                readData();
                doExcute();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(this)
                    .setTitle("確認")
                    .setMessage("終了しますか？")
                    .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("いいえ", null)
                    .show();
            return true;
        } else{
            return super.onKeyDown(keyCode,event);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        settingData = getSharedPreferences("settingData",MODE_PRIVATE);
        if(settingData.getBoolean("checkChange",false)){
            deleteData();
            readData();
            doExcute();
            SharedPreferences.Editor editor = settingData.edit();
            editor.putBoolean("checkChange",false);
            editor.apply();
            
        }
    }
}

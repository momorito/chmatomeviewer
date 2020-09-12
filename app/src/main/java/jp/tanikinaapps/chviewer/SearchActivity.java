package jp.tanikinaapps.chviewer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private ArticleOpenHelper helper;
    private SQLiteDatabase db;
    private String[] searchWords;
    List<Map<String, String>> searchData;
    Toolbar toolbar;
    ListView listView;
    private FavoriteOpenHelper fHelper;
    private SQLiteDatabase fDb;
    SharedPreferences colorData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        new SetAd((AdView)findViewById(R.id.adView),this,getResources().getString(R.string.ad_code)).AdSetting();

        colorData = getSharedPreferences("settingData",MODE_PRIVATE);

        Intent intent = getIntent();
        if(intent.getStringArrayExtra("jp.tanikinaapps.chviewer.DATA") != null){
            searchWords = new String[2];
            searchWords = intent.getStringArrayExtra("jp.tanikinaapps.chviewer.DATA");

            getSupportActionBar().setTitle("「" + searchWords[1] + "」の検索結果");
        }


        if(helper == null){
            helper = new ArticleOpenHelper(getApplicationContext());
        }
        if(db == null){
            db = helper.getReadableDatabase();
        }

        if(searchWords[0].equals("article")){
            Cursor cursor = db.query("articledb",new String[]{ "articleTitle","articleAddress","articleUpDate","blogName"},"articleTitle like ?",new String[]{"%" + searchWords[1] +"%"},null,null,"articleUpDate DESC");
            cursor.moveToFirst();
            searchData = new ArrayList<Map<String,String>>();
            for(int i = 0;i<cursor.getCount();i++){

                Map<String,String> item = new HashMap<String,String>();
                item.put("pageTitle",cursor.getString(0));
                item.put("pageUrl",cursor.getString(1));
                item.put("pageDate",cursor.getString(2));
                item.put("blogName",cursor.getString(3));
                searchData.add(item);
                cursor.moveToNext();
            }

            cursor.close();
        } else {
            Cursor cursor = db.query("articledb",new String[]{ "articleTitle","articleAddress","articleUpDate","blogName"},"blogName like ?",new String[]{"%" + searchWords[1] +"%"},null,null,"articleUpDate DESC");
            cursor.moveToFirst();
            searchData = new ArrayList<Map<String,String>>();
            for(int i = 0;i<cursor.getCount();i++){

                Map<String,String> item = new HashMap<String,String>();
                item.put("pageTitle",cursor.getString(0));
                item.put("pageUrl",cursor.getString(1));
                item.put("pageDate",cursor.getString(2));
                item.put("blogName",cursor.getString(3));
                searchData.add(item);
                cursor.moveToNext();
            }

            cursor.close();
        }







        listView = findViewById(R.id.listSearch);
        BaseAdapter adapter = new SetAdapter.ListAdapter(getApplicationContext(),R.layout.list_contents,searchData);
        listView.setAdapter(adapter);
        Toast.makeText(this,"検索結果：" + searchData.size() + "件",Toast.LENGTH_SHORT).show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplication(),WebActivity.class);
                intent.putExtra("jp.tanikinaapps.chviewer.DATA",searchData.get(position).get("pageUrl"));
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
                        searchData.get(position).get("pageTitle"),
                        searchData.get(position).get("pageUrl"),
                        searchData.get(position).get("pageDate"),
                        searchData.get(position).get("blogName"));
                Toast.makeText(getApplicationContext(),"お気に入りに登録しました",Toast.LENGTH_SHORT).show();

                return true;
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorite, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_favorite_help:
                Toast.makeText(getApplicationContext(),"長押しでお気に入りに登録できます",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

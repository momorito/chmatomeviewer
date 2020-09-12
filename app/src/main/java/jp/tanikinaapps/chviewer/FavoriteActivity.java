package jp.tanikinaapps.chviewer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.Map;

import de.psdev.licensesdialog.LicensesDialogFragment;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

public class FavoriteActivity extends AppCompatActivity {
    public static final String DATA = "jp.tanikinaapps.chviewer.DATA";
    Toolbar toolbar;
    ArrayList<Map<String,String>> favoriteData;
    FavoriteOpenHelper fHelper;
    SQLiteDatabase fDb;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        new SetAd((AdView)findViewById(R.id.adView),this,getResources().getString(R.string.ad_code)).AdSetting();
        toolbar = findViewById(R.id.toolbarFavorite);

        setSupportActionBar(toolbar);
        // Backボタンを有効にする
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        readData();
    }

    public void  readData(){
        favoriteData = new ArrayList<Map<String,String>>();
        if(fHelper == null){
            fHelper = new FavoriteOpenHelper(getApplicationContext());
        }
        if(fDb == null){
            fDb = fHelper.getReadableDatabase();
        }
        Cursor cursor = fDb.query("favoritedb",new String[]{ "_id","articleTitle","articleAddress","articleUpDate","blogName"},null,null,null,null,null);
        cursor.moveToFirst();

        for(int i = 0;i<cursor.getCount();i++){
            Map<String,String> item = new HashMap<String,String>();
            item.put("pageId",cursor.getString(0));
            item.put("pageTitle",cursor.getString(1));
            item.put("pageUrl",cursor.getString(2));
            item.put("pageDate",cursor.getString(3));
            item.put("blogName",cursor.getString(4));
            favoriteData.add(item);
            cursor.moveToNext();
        }
        cursor.close();

        listView = findViewById(R.id.listFavorite);
        BaseAdapter adapter = new SetAdapter.ListAdapter(getApplicationContext(),R.layout.list_contents,favoriteData);
        listView.setAdapter(adapter);

        Toast.makeText(getApplicationContext(),"お気に入り登録：" + cursor.getCount() + "件",Toast.LENGTH_SHORT).show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplication(),WebActivity.class);
                intent.putExtra(DATA,favoriteData.get(position).get("pageUrl"));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String deleteId = favoriteData.get(position).get("pageId");
                new AlertDialog.Builder(FavoriteActivity.this)
                        .setTitle(R.string.deleteFavorite)
                        .setMessage("「" + favoriteData.get(position).get("pageTitle") + "」" + " を削除しますか？")
                        .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fDb.delete("favoritedb","_id = \"" + deleteId + "\"",null);
                                readData();
                            }
                        })
                        .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_favorite_help:
                Toast.makeText(getApplicationContext(),"長押しでお気に入りから削除できます",Toast.LENGTH_LONG).show();
                break;
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favorite, menu);
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        readData();
    }
}

package jp.tanikinaapps.chviewer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListView;

import com.google.android.gms.ads.AdView;

public class ImageActivity extends AppCompatActivity {
    public static final String DATA = "jp.tanikinaapps.chviewer.DATA";
    Toolbar toolbar;
    AsyncGetImage asyncGetImage;
    private GridView gridImage;
    Context context;
    String[] str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        toolbar = findViewById(R.id.toolbarImage);
        setSupportActionBar(toolbar);
        // Backボタンを有効にする
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setTitle("R.string.image_activity_title");

        new SetAd((AdView)findViewById(R.id.adView),this,getResources().getString(R.string.ad_code)).AdSetting();

        context = getApplicationContext();
        asyncGetImage = new AsyncGetImage();
        Intent i = getIntent();
        if(i.getStringExtra(DATA) != null){
            asyncGetImage.setListener(createListener());
            asyncGetImage.execute(i.getStringExtra(DATA));
        }
    }

    private AsyncGetImage.Listener createListener(){
        return new AsyncGetImage.Listener() {
            @Override
            public void onSuccess(String[] str) {
                setStr(str);
                gridImage = findViewById(R.id.gridView);
                SetAdapter.ImageAdapter imageAdapter = new SetAdapter.ImageAdapter(getApplicationContext(),R.layout.list_image,str);
                gridImage.setAdapter(imageAdapter);

                gridImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        FragmentManager searchFragmentManager = getSupportFragmentManager();
                        DialogFragment imageDialogFragment = new SetDialog.ImageDialogFragment(getStr(position));
                        imageDialogFragment.show(searchFragmentManager,"image");
                        Log.d("進捗",getStr(position));
                    }
                });

            }
        };
    }

    public void setStr(String[] str){
        this.str = str;
    }
    public String getStr(int position){
        return str[position];
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

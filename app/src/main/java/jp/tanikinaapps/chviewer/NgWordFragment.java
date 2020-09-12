package jp.tanikinaapps.chviewer;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

import org.jsoup.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NgWordFragment extends Fragment {
    private NgWordOpenHelper helper;
    private SQLiteDatabase db;
    private List<Map<String, String>> ngWordList;
    private EditText editText;
    private SharedPreferences data;

    public NgWordFragment() {
        // Required empty public constructor
    }

    public static NgWordFragment newInstance() {
        NgWordFragment fragment = new NgWordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ng_word, container, false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){

        super.onViewCreated(view,savedInstanceState);
        data = getContext().getSharedPreferences("settingData", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = data.edit();
        new SetAd((AdView)view.findViewById(R.id.adView),getContext(),getResources().getString(R.string.ad_code)).AdSetting();
        readWords();

        Button buttonRegist = view.findViewById(R.id.buttonRegist);
        buttonRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                editText = getView().findViewById(R.id.editText);
                if(editText.getText().toString().length() == 0){
                    Toast.makeText(getContext(),"入力しなければ登録できません",Toast.LENGTH_SHORT).show();
                } else{
                    writeWords();
                    readWords();
                    Toast.makeText(getContext(),"登録しました",Toast.LENGTH_SHORT).show();
                    editor.putBoolean("checkChange",true);
                    editor.apply();

                }
            }
        });



    }

    private void readWords(){


        if(helper == null){
            helper = new NgWordOpenHelper(getContext());
        }
        if(db == null){
            db = helper.getReadableDatabase();
        }
        ngWordList = new ArrayList<Map<String,String>>();
        ArrayList<String> ngWords = new ArrayList<>();

        Cursor cursor = db.query("ngworddb",new String[]{ "_id","ngword"},null,null,null,null,null);
        cursor.moveToFirst();

            for(int i = 0;i<cursor.getCount();i++){
                Map<String,String> item = new HashMap<String,String>();
                item.put("wordId",cursor.getString(0));
                item.put("ngword",cursor.getString(1));
                ngWordList.add(item);
                cursor.moveToNext();
            }
            cursor.close();
            setNgWordList(ngWordList);



        /*
        if(ngWordList.size() != 0){

            ngWords = new String[ngWordList.size()];
        } else{
            ngWords = new String[1];
            ngWords.
        }

         */

            for(int i = 0;i<ngWordList.size();i++){
                ngWords.add(ngWordList.get(i).get("ngword"));
            }

            final ListView listView = getView().findViewById(R.id.ngWordList);
            final BaseAdapter adapter = new SetAdapter.NgWordAdapter(getContext(),R.layout.setting_list,ngWords);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final String deleteId = ngWordList.get(position).get("wordId");
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.deleteFavorite)
                            .setMessage("「" + ngWordList.get(position).get("ngword") + "」" + " を削除しますか？")
                            .setPositiveButton("はい", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db.delete("ngworddb","_id = \"" + deleteId + "\"",null);
                                    data = getContext().getSharedPreferences("settingData", Context.MODE_PRIVATE);
                                    final SharedPreferences.Editor editor = data.edit();
                                    editor.putBoolean("checkChange",true);
                                    editor.apply();
                                    readWords();
                                }
                            })
                            .setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .show();
                }
            });

    }

    private void writeWords(){
        String word;
        editText = getView().findViewById(R.id.editText);
        word = editText.getText().toString();

        if(helper == null){
            helper = new NgWordOpenHelper(getContext());
        }
        if(db == null){
            db = helper.getWritableDatabase();
        }

        ContentValues values = new ContentValues();
        values.put("ngword",word);
        db.insert("ngworddb",null,values);
        editText.setText("");

    }
    private void setNgWordList(List<Map<String, String>> ngWordList){
        this.ngWordList = ngWordList;
    }
}

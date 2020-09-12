package jp.tanikinaapps.chviewer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.android.gms.ads.AdView;


public class GeneralSettingFragment extends Fragment {
    ListView listGeneral;
    SharedPreferences data;
    Boolean checkChange;

    public GeneralSettingFragment() {
    }

    public static GeneralSettingFragment newInstance(String param1, String param2) {
        GeneralSettingFragment fragment = new GeneralSettingFragment();
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

        return inflater.inflate(R.layout.fragment_general_setting, container, false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        data = getContext().getSharedPreferences("settingData", Context.MODE_PRIVATE);

        new SetAd((AdView)view.findViewById(R.id.adView),getContext(),getResources().getString(R.string.ad_code)).AdSetting();
        hiddenKeyboard(getView());

        BaseAdapter adapter = new SetAdapter.GeneralSettingAdapter(getContext(),R.layout.setting_list);
        listGeneral = view.findViewById(R.id.listGeneral);
        listGeneral.setAdapter(adapter);
        listGeneral.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch(position){
                    case 0:
                    case 1:
                    case 2:
                        break;
                    case 3:
                        final String[] items = {"灰（グレー）","黒（ブラック）","白（ホワイト）","桃（ピンク）","青（ブルー）"};
                        new AlertDialog.Builder(getContext())
                                .setTitle("色を選択")
                                .setItems(items, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String useColor = null;
                                        switch(which){
                                            case 0:
                                                useColor = "gray";
                                                break;
                                            case 1:
                                                useColor = "black";
                                                break;
                                            case 2:
                                                useColor = "white";
                                                break;
                                            case 3:
                                                useColor = "pink";
                                                break;
                                            case 4:
                                                useColor = "blue";
                                                break;
                                        }
                                        checkChange = true;

                                        SharedPreferences.Editor editor = data.edit();
                                        editor.putString("color",useColor);
                                        editor.putBoolean("checkChange",checkChange);
                                        editor.apply();
                                    }
                                })
                                .show();
                        break;
                }
            }
        });


    }

    private void hiddenKeyboard(View v){
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }
}

package jp.tanikinaapps.chviewer;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;

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


public class BlogSelectFragment extends Fragment {

    public BlogSelectFragment() {
        // Required empty public constructor
    }

    public static BlogSelectFragment newInstance() {
        BlogSelectFragment fragment = new BlogSelectFragment();
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
        return inflater.inflate(R.layout.fragment_blog_select, container, false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        hiddenKeyboard(getView());

        new SetAd((AdView)view.findViewById(R.id.adView),getContext(),getResources().getString(R.string.ad_code)).AdSetting();

        TypedArray typedArray = getResources().obtainTypedArray(R.array.listBlogs);
        final String[] blogName = new String[typedArray.length()];
        String[] url = new String[typedArray.length()];
        for(int i =0;i<typedArray.length();i++){
            blogName[i] =  getResources().getStringArray(typedArray.getResourceId(i,0))[0];
            url[i] =  getResources().getStringArray(typedArray.getResourceId(i,0))[1];
        }
        ListView blogList = getView().findViewById(R.id.blogList);
        BaseAdapter adapter = new SetAdapter.BlogListAdapter(getContext(),R.layout.setting_list,blogName,url);
        blogList.setAdapter(adapter);



    }

    private void hiddenKeyboard(View v){
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }
}

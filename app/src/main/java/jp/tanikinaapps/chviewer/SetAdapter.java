package jp.tanikinaapps.chviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SetAdapter {

    public static class ListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int layoutID;
        private List<Map<String,String>> allData;
        private String colorData;

        class ViewHolder{
            TextView title,date,blogName;
        }

        ListAdapter(Context context, int itemLayoutId, List<Map<String,String>> allData){
            inflater = LayoutInflater.from(context);
            layoutID = itemLayoutId;

            this.allData = allData;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if(convertView == null){
                convertView = inflater.inflate(layoutID,null);
                holder = new ViewHolder();
                holder.title = convertView.findViewById(R.id.contentsTitle);
                holder.date = convertView.findViewById(R.id.contentsDate);
                holder.blogName = convertView.findViewById(R.id.contentsBlogName);
                convertView.setTag(holder);
            } else{
                holder = (ViewHolder) convertView.getTag();
            }

            holder.title.setText(allData.get(position).get("pageTitle"));
            holder.date.setText(allData.get(position).get("pageDate"));
            holder.blogName.setText(allData.get(position).get("blogName"));

            return convertView;
        }

        @Override
        public int getCount() {
            return allData.size();
        }
        @Override
        public Object getItem(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    public static class ImageAdapter extends BaseAdapter {
        Context context;
        private LayoutInflater inflater;
        private int layoutId;
        String[] imageUrl;

        private int ScreenWHalf = 0;

        static class ViewHolder{
            ImageView imageView;
        }

        ImageAdapter(Context context,int layoutId,String[] imageUrl){
            super();
            this.context =context;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.layoutId = layoutId;
            this.imageUrl = imageUrl;

            WindowManager wm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            if(wm != null){
                Display disp = wm.getDefaultDisplay();
                Point size = new Point();
                disp.getSize(size);

                int screenWidth = size.x;
                ScreenWHalf = screenWidth / 2;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if(convertView == null){
                view = inflater.inflate(layoutId,parent,false);
            } else {
                view = convertView;
            }
            ImageView img = view.findViewById(R.id.imageView);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.with(context).load(imageUrl[position]).resize(ScreenWHalf,ScreenWHalf).into(img);

            return view;
        }

        @Override
        public int getCount() {
            return imageUrl.length;
        }
        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }


    }


    public static class BlogListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int layoutID;
        private String[] data,url;
        SharedPreferences selectData;

        class ViewHolder{
            TextView title,explanation;
            Switch settingSwitch;
            ListView list;
        }

        BlogListAdapter(Context context, int itemLayoutId, String[] data,String[] url){
            inflater = LayoutInflater.from(context);
            layoutID = itemLayoutId;

            this.data = data;
            this.url = url;

            selectData =context.getSharedPreferences("settingData",Context.MODE_PRIVATE);



        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            final SharedPreferences.Editor editor = selectData.edit();
            final String blog = "blog";

            if(convertView == null){
                convertView = inflater.inflate(layoutID,null);
                holder = new ViewHolder();
                holder.title = convertView.findViewById(R.id.title);
                holder.explanation = convertView.findViewById(R.id.ecplanation);
                holder.settingSwitch = convertView.findViewById(R.id.settingSwitch);
                convertView.setTag(holder);
            } else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText(data[position]);
            holder.title.setTextColor(Color.WHITE);
            holder.explanation.setText(url[position]);
            holder.explanation.setTextSize(12.0f);
            holder.explanation.setTextColor(Color.WHITE);

            holder.settingSwitch.setOnCheckedChangeListener(null);
            holder.settingSwitch.setChecked(selectData.getBoolean(blog + position,true));


            holder.settingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        editor.putBoolean(blog + position,true);
                    } else{
                        editor.putBoolean(blog + position,false);
                    }
                    editor.putBoolean("checkChange",true);
                    editor.apply();
                }
            });


            return convertView;
        }

        @Override
        public int getCount() {
            return data.length;
        }
        @Override
        public Object getItem(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    public static class GeneralSettingAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int layoutID;
        SharedPreferences data;

        class ViewHolder{
            TextView title,explanation;
            Switch settingSwitch;
        }

        GeneralSettingAdapter(Context context, int itemLayoutId){
            inflater = LayoutInflater.from(context);
            layoutID = itemLayoutId;
            data = context.getSharedPreferences("settingData", Context.MODE_PRIVATE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if(convertView == null){
                convertView = inflater.inflate(layoutID,null);
                holder = new ViewHolder();
                holder.title = convertView.findViewById(R.id.title);
                holder.explanation = convertView.findViewById(R.id.ecplanation);
                holder.settingSwitch = convertView.findViewById(R.id.settingSwitch);
                convertView.setTag(holder);
            } else{
                holder = (ViewHolder) convertView.getTag();
            }

            switch(position){
                case 0:
                    Boolean useImageExpansion;
                    useImageExpansion = data.getBoolean("useImageExpansion",false);

                    holder.title.setText(R.string.general_1_title);
                    holder.explanation.setText(R.string.general_1_explain);
                    holder.title.setTextColor(Color.WHITE);
                    holder.explanation.setTextColor(Color.WHITE);

                    holder.settingSwitch.setChecked(useImageExpansion);
                    holder.settingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            SharedPreferences.Editor editor = data.edit();
                            editor.putBoolean("useImageExpansion",isChecked);
                            editor.putBoolean("checkChange",true);
                            editor.apply();
                        }
                    });
                    break;
                case 1:
                    Boolean useOtherApp;
                    useOtherApp = data.getBoolean("useOtherApp",true);
                    holder.title.setText(R.string.general_2_title);
                    holder.explanation.setText(R.string.general_2_explain);
                    holder.title.setTextColor(Color.WHITE);
                    holder.explanation.setTextColor(Color.WHITE);

                    holder.settingSwitch.setChecked(useOtherApp);
                    holder.settingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            SharedPreferences.Editor editor = data.edit();
                            editor.putBoolean("useOtherApp",isChecked);
                            editor.putBoolean("checkChange",true);
                            editor.apply();
                        }
                    });
                    break;
                case 2:
                    Boolean useImageSearch;
                    useImageSearch = data.getBoolean("useImageSearch",true);
                    holder.title.setText(R.string.general_3_title);
                    holder.explanation.setText(R.string.general_3_explain);
                    holder.title.setTextColor(Color.WHITE);
                    holder.explanation.setTextColor(Color.WHITE);

                    holder.settingSwitch.setChecked(useImageSearch);
                    holder.settingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            SharedPreferences.Editor editor = data.edit();
                            editor.putBoolean("useImageSearch",isChecked);
                            editor.putBoolean("checkChange",true);
                            editor.apply();
                        }
                    });
                    break;
                case 3:
                    Boolean removeJavascript;
                    removeJavascript = data.getBoolean("removeJavascript",false);
                    holder.title.setText(R.string.general_5_title);
                    holder.explanation.setText(R.string.general_5_explain);
                    holder.title.setTextColor(Color.WHITE);
                    holder.explanation.setTextColor(Color.WHITE);

                    holder.settingSwitch.setChecked(removeJavascript);
                    holder.settingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            SharedPreferences.Editor editor = data.edit();
                            editor.putBoolean("removeJavascript",isChecked);
                            editor.putBoolean("checkChange",true);
                            editor.apply();
                        }
                    });
                    /*
                case 3:
                    holder.title.setText(R.string.general_4_title);
                    holder.explanation.setText(R.string.general_4_explain);
                    holder.title.setTextColor(Color.WHITE);
                    holder.explanation.setTextColor(Color.WHITE);
                    holder.settingSwitch.setVisibility(View.INVISIBLE);
                    break;

                     */
            }
            return convertView;
        }

        @Override
        public int getCount() {
            return 4;
        }
        @Override
        public Object getItem(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    public static class NgWordAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int layoutID;
        ArrayList<String> ngWords;

        class ViewHolder{
            TextView title,explanation;
            Switch settingSwitch;
        }

        NgWordAdapter(Context context, int itemLayoutId,ArrayList<String> ngWords){
            inflater = LayoutInflater.from(context);
            layoutID = itemLayoutId;
            this.ngWords = ngWords;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if(convertView == null){
                convertView = inflater.inflate(layoutID,null);
                holder = new ViewHolder();
                holder.title = convertView.findViewById(R.id.title);
                holder.explanation = convertView.findViewById(R.id.ecplanation);
                holder.settingSwitch = convertView.findViewById(R.id.settingSwitch);
                convertView.setTag(holder);
            } else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setTextColor(Color.WHITE);
            holder.title.setText(ngWords.get(position));
            holder.explanation.setTextSize(0.1f);
            holder.explanation.setVisibility(View.INVISIBLE);
            holder.settingSwitch.setVisibility(View.INVISIBLE);
            return convertView;
        }

        @Override
        public int getCount() {
            return ngWords.size();
        }
        @Override
        public Object getItem(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
    }

}

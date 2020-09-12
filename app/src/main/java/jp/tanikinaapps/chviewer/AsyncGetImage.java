package jp.tanikinaapps.chviewer;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AsyncGetImage extends AsyncTask<String,String,String[]>{

    private Listener listener;

    @Override
    protected String[] doInBackground(String... url) {
        ArrayList arrayList = new ArrayList<String>();
        try {
            Document document = Jsoup.connect(url[0]).get();
            Elements jpg = document.select("img, src");

            for(int i =0;i<jpg.size();i++){
                if(jpg.get(i).toString().contains(".jpg")){
                    arrayList.add(jpg.get(i).absUrl("src"));
                }

                if(jpg.get(i).toString().contains(".jpeg")){
                    arrayList.add(jpg.get(i).absUrl("src"));

                }

                if(jpg.get(i).toString().contains(".png")){
                    arrayList.add(jpg.get(i).absUrl("src"));
                }

                if(jpg.get(i).toString().contains(".gif")){
                    arrayList.add(jpg.get(i).absUrl("src"));
                }

            }

            String[] imageUrl = new String[arrayList.size()];
            for(int i = 0;i<arrayList.size();i++){
                imageUrl[i] = arrayList.get(i).toString();
            }
            return imageUrl;

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Error","failed to read Url");
            return null;
        }
    }

    @Override
    protected void onPostExecute(String[] imageUrl){

        if(listener != null){
            listener.onSuccess(imageUrl);
        }
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onSuccess(String[] str);
    }
}

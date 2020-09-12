package jp.tanikinaapps.chviewer;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AsyncGetArticle extends AsyncTask<String,String, List<Map<String, String>>> {
    private Listener listener;
    private ArrayList<String> pageTitle,pageUrl,pageDate;
    private List<Map<String, String>> data;


    @Override
    protected List<Map<String, String>> doInBackground(String... str) {
        data = new ArrayList<Map<String,String>>();
        for(int i=0;i<str.length;i++){
            inputUrl(str[i]);
        }
        return data;
    }

    private void inputUrl(String blogUrl){
        pageTitle = new ArrayList<String>();
        pageUrl = new ArrayList<String>();
        pageDate = new ArrayList<String>();



            try{
                XmlPullParser xmlPullParser = Xml.newPullParser();
                xmlPullParser.setInput(okHttp(blogUrl),"UTF-8");

                int eventType;
                while((eventType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT){
                    if(eventType == XmlPullParser.START_TAG && "title".equals(xmlPullParser.getName())){
                        pageTitle.add(xmlPullParser.nextText());
                    }
                }

                xmlPullParser.setInput(okHttp(blogUrl),"UTF-8");
                int eventType2;
                while((eventType2 = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT){
                    if(eventType2 == XmlPullParser.START_TAG && "link".equals(xmlPullParser.getName())){
                        pageUrl.add(xmlPullParser.nextText());
                    }
                }

                xmlPullParser.setInput(okHttp(blogUrl),"UTF-8");
                int eventType3;
                while((eventType3 = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT){
                    if(eventType3 == XmlPullParser.START_TAG && "date".equals(xmlPullParser.getName())){
                        pageDate.add(xmlPullParser.nextText());
                    }
                }

                String[] blogTitleString = new String[pageTitle.size() - 1];
                String[] pageTitleString = new String[pageTitle.size() - 1];
                String[] pageUrlString = new String[pageUrl.size() - 1];
                String[] pageDateString = new String[pageDate.size()];


                for(int m =0;m<pageTitle.size() -1 ;m++){
                    blogTitleString[m] = pageTitle.get(0);
                    pageTitleString[m] = pageTitle.get(m+1);
                    pageUrlString[m] = pageUrl.get(m+2);
                    pageDateString[m] = pageDate.get(m).substring(0,4) + "年" + pageDate.get(m).substring(5,7) + "月" + pageDate.get(m).substring(8,10) + "日 " + pageDate.get(m).substring(11,16);

                }

                for(int m=0; m<pageTitleString.length ; m++){
                    Map<String, String> item = new HashMap<String, String>();
                    item.put("blogTitleString",blogTitleString[m]);
                    item.put("pageTitleString", pageTitleString[m]);
                    item.put("pageUrlString", pageUrlString[m]);
                    item.put("pageDateString",pageDateString[m]);
                    data.add(item);
                }

            } catch(Exception e){
            }

    }


    private InputStream okHttp(String blogUrl){
        Request request = new Request.Builder().url(blogUrl).get().build();
        Response response;
        try{
            response = new OkHttpClient().newCall(request).execute();
        } catch(Exception e){
            return  null;
        }
        return response.body().byteStream();
    }

    @Override
    protected void onPostExecute(List<Map<String, String>> data){
        if(listener != null){
            listener.onSuccess(data);
        }
    }
    void setListener(Listener listener){
        this.listener = listener;
    }
    interface Listener{
        void onSuccess(List<Map<String, String>> data);
    }
}


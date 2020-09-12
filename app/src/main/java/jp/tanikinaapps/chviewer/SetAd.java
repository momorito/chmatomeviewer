package jp.tanikinaapps.chviewer;

import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class SetAd {
    AdView adView;
    Context context;
    String adCode;


    public SetAd(AdView adView, Context context,String adCode){
        this.adView = adView;
        this.context = context;
        this.adCode = adCode;
    }

    public void AdSetting(){
        MobileAds.initialize(context,adCode);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
}

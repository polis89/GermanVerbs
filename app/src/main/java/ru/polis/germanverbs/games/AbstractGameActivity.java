package ru.polis.germanverbs.games;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.Random;

import ru.polis.germanverbs.PracticeFragment;
import ru.polis.germanverbs.R;
import ru.polis.germanverbs.objects.Result;
import ru.polis.germanverbs.objects.Verb;

/**
 * Created by Dmitrii Polianskii.
 */
public abstract class AbstractGameActivity extends AppCompatActivity {
    protected Verb[] verbs; //Все глаголы для изучения
    protected Result[] results; //Результаты изучения глагола
    protected int presentVerbNum; //номер текущего глагола
    protected LinearLayout layoutWithAdView;
    protected InterstitialAd mInterstitialAd;

    abstract protected void startResultActivity();

    protected static boolean isOnline(Context context)    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    protected void addToolbar()    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    protected void loadAd(){
        mInterstitialAd = new InterstitialAd(this);
        AdView adView = (AdView) layoutWithAdView.findViewById(R.id.adView);
        if(isOnline(this)){
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
            AdRequest ar = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(ar);
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    startResultActivity();
                }
            });
        }else{
            layoutWithAdView.removeView(adView);
        }
    }

    protected void getDataFromIntent(){
        Parcelable[] parcelableArrayExtra = getIntent().getParcelableArrayExtra(PracticeFragment.RANDOM_VERB_INTENT_EXTRA);
        verbs = new Verb[parcelableArrayExtra.length];
        results = new Result[parcelableArrayExtra.length];
        for (int i = 0; i < parcelableArrayExtra.length; i++) {
            verbs[i] = (Verb) parcelableArrayExtra[i];
            results[i] = new Result();
        }
    }

    protected void mixVerbs(){
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < verbs.length; i++) {
            int rndInt = random.nextInt(verbs.length);
            Verb temp = verbs[i];
            verbs[i] = verbs[rndInt];
            verbs[rndInt] = temp;
        }
    }

    protected void onStopLesson(){
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        } else{
            startResultActivity();
        }
    }
}

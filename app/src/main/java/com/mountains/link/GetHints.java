package com.mountains.link;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.mountains.link.utils.DataHandler;
import com.mountains.link.utils.PrefClass;
import com.tapjoy.TapjoyConnect;
import com.tapjoy.TapjoySpendPointsNotifier;

import java.util.Hashtable;

public class GetHints extends Activity implements OnClickListener {

    private final static String TAG = "DOTS";

    private Context context = this;
    private Button bfree, bshare, bvideo;
    private ProgressBar progressBarVideoHints;
    private TextView textViewVideoHints;

    private RewardedAd rewardedAd;
    private InterstitialAd mInterstitialAd;

    private int sharedHints = 5;
    private boolean isShared = false;
    private int earnedTapHints = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.hints_choice);

        sharedHints = getResources().getInteger(R.integer.shareHints);

        bfree = findViewById(R.id.bfreehints);
        bshare = findViewById(R.id.bshareHints);
        bvideo = findViewById(R.id.bvideoHints);
        progressBarVideoHints = findViewById(R.id.progress);
        textViewVideoHints = findViewById(R.id.text_hints);

        bshare.setTypeface(DataHandler.getTypeface(context));
        bvideo.setTypeface(DataHandler.getTypeface(context));
        bfree.setTypeface(DataHandler.getTypeface(context));

        bshare.setOnClickListener(this);
        bvideo.setOnClickListener(this);
        bfree.setOnClickListener(this);

        ((TextView) findViewById(R.id.tvfreehints)).setTypeface(DataHandler.getTypeface(context));
        ((TextView) findViewById(R.id.tvsharehints)).setTypeface(DataHandler.getTypeface(context));
        ((TextView) findViewById(R.id.tvvideohints)).setTypeface(DataHandler.getTypeface(context));

        if (DataHandler.tapjoy_ads) {
            initialiseInterstitialAd();
        }

        if (DataHandler.video_ads) {
            initialiseRewardedAd();
        }

        if (!DataHandler.video_ads) {
            bvideo.setVisibility(View.GONE);
            findViewById(R.id.tvvideohints).setVisibility(View.GONE);
        }

        if (!DataHandler.tapjoy_ads) {
            bfree.setVisibility(View.GONE);
            findViewById(R.id.tvfreehints).setVisibility(View.GONE);
        }
    }

    private void initialiseInterstitialAd() {
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_id));
        mInterstitialAd.setAdListener(mAdListener);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void initialiseRewardedAd() {
        progressBarVideoHints.setVisibility(View.VISIBLE);
        textViewVideoHints.setVisibility(View.GONE);
        rewardedAd = new RewardedAd(this,
                getString(R.string.rewarded_id));
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
                progressBarVideoHints.setVisibility(View.GONE);
                textViewVideoHints.setVisibility(View.VISIBLE);
                textViewVideoHints.setText(rewardedAd.getRewardItem().getAmount() + "\n" + getString(R.string.hints));
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);
    }

    private AdListener mAdListener = new AdListener() {
        boolean isOnAdClickedCalled = false;
        @Override
        public void onAdLoaded() {
            // Code to be executed when an ad finishes loading.
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            // Code to be executed when an ad request fails.
        }

        @Override
        public void onAdOpened() {
            // Code to be executed when the ad is displayed.
            isOnAdClickedCalled = false;
        }

        @Override
        public void onAdClicked() {
            // Code to be executed when the user clicks on an ad.
            final int hints = getResources().getInteger(R.integer.videoHints);
            addHint(hints);
            Log.d("my_test3", "Click ad");
            isOnAdClickedCalled = true;
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, hints + " " + getString(R.string.hints_added), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onAdLeftApplication() {
            // Code to be executed when the user has left the app.
        }

        @Override
        public void onAdClosed() {
            // Code to be executed when the interstitial ad is closed.
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            if (!isOnAdClickedCalled)
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(context, R.string.click_to_ad_receive_hints, Toast.LENGTH_SHORT).show();
                    }
                });
        }
    };

    private RewardedAdCallback mAdCallback = new RewardedAdCallback() {
        boolean isOnUserEarnedRewardCalled = false;

        @Override
        public void onRewardedAdOpened() {
            // Ad opened.
            isOnUserEarnedRewardCalled = false;
        }

        @Override
        public void onRewardedAdClosed() {
            // Ad closed.
            initialiseRewardedAd();
            if (!isOnUserEarnedRewardCalled)
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(context, getString(R.string.please_watch_video_to_get_hints), Toast.LENGTH_SHORT).show();
                    }
                });
        }

        @Override
        public void onUserEarnedReward(@NonNull RewardItem reward) {
            // User earned reward.
            final int hints = reward.getAmount();
            addHint(hints);
            isOnUserEarnedRewardCalled = true;
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, hints + " " + getString(R.string.hints_added), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onRewardedAdFailedToShow(AdError adError) {
            // Ad failed to display.
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, R.string.you_cannot_play_rewarded, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private void addHint(int value) {
        int hint = PrefClass.getHint();
        hint = hint + value;
        PrefClass p1 = new PrefClass(context);
        PrefClass.saveHint(hint);
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.bshareHints:
                shareIt();
                break;
            case R.id.bfreehints:
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Toast.makeText(context, R.string.interstitial_wasnt_loaded_yet, Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.bvideoHints:
                showRewardedAd();
                break;
        }
    }

    private void showRewardedAd() {
        if (rewardedAd.isLoaded()) {
            Activity activityContext = this;
            rewardedAd.show(activityContext, mAdCallback);
        } else {
            Toast.makeText(context, R.string.rewarded_ad_wasnt_loaded_yet, Toast.LENGTH_LONG).show();
        }
    }

    private boolean hasDayPassed(long lastSharedTime) {
        boolean passed = false;
        if (lastSharedTime == 0)
            passed = true;
        else if (lastSharedTime + 24 * 60 * 60 * 100 < System.currentTimeMillis()) {
            passed = true;
        }
        return passed;
    }

    private void shareIt() {
        String appPackageName = getPackageName();
        isShared = true;
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=" + appPackageName);
        i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.shareSubject));
        startActivity(Intent.createChooser(i, "Share"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isShared && hasDayPassed(PrefClass.getLastSharedTime())) {
            PrefClass.setLastSharedTime(System.currentTimeMillis());
            Toast.makeText(context,
                    getString(R.string.thanks_for_sharing) + " " +
                            sharedHints + " " + getString(R.string.hints_added),
                    Toast.LENGTH_SHORT).show();
            addHint(sharedHints);
            isShared = false;
            this.finish();
        } else if (isShared && !hasDayPassed(PrefClass.getLastSharedTime())) {
            Toast.makeText(context, R.string.try_sharing_tomorrow, Toast.LENGTH_LONG).show();
            isShared = false;
            this.finish();
        }
    }
}

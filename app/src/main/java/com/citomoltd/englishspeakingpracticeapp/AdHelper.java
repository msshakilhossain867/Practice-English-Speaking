package com.citomoltd.englishspeakingpracticeapp;

import android.app.Activity;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdHelper {

    private static final String TAG = "AdHelper";

    // --- ক্লাসের একদম উপরে Ad Unit ID সমূহ ---
  /* private static final String INTERSTITIAL_AD_ID = "ca-app-pub-5488667449529842/8715740569";
    private static final String BANNER_AD_ID = "ca-app-pub-5488667449529842/2137055623";*/

     //Interstitial Test Ad ID
    private static final String INTERSTITIAL_AD_ID = "ca-app-pub-3940256099942544/1033173712";

     //Banner Test Ad ID
    private static final String BANNER_AD_ID = "ca-app-pub-3940256099942544/6300978111";

    // --- কনফিগারেশন ---
    private static final int SHOW_AD_EVERY_N_CLICKS = 3;

    // ১ম ক্লিকেই অ্যাড দেখাবে, তারপর প্রতি ৩ ক্লিকে দেখাবে
    private static int clickCount = SHOW_AD_EVERY_N_CLICKS - 1;

    private static InterstitialAd mInterstitialAd;
    private static boolean isAdLoading = false;
    private static AdView mAdView;

    public interface AdListener {
        void onAdClosed();
    }

    // MainActivity-এর onCreate-এ একবার এই মেথড কল করে রাখবেন
    public static void init(Activity activity) {
        loadNewAd(activity);
    }

    // বাটন ক্লিকে এই মেথডটি কল করবেন
    public static void showAdOnButtonClick(Activity activity, AdListener listener) {
        clickCount++;

        if (clickCount >= SHOW_AD_EVERY_N_CLICKS) {
            if (mInterstitialAd != null) {
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        Log.d(TAG, "Ad dismissed.");
                        clickCount = 0; // অ্যাড দেখার পর কাউন্টার ০ থেকে শুরু হবে
                        mInterstitialAd = null;
                        loadNewAd(activity); // পরের ৩ নম্বর ক্লিকের জন্য ব্যাকগ্রাউন্ডে প্রিলোড
                        if (listener != null) listener.onAdClosed();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        Log.d(TAG, "Ad failed to show: " + adError.getMessage());
                        clickCount = 0;
                        mInterstitialAd = null;
                        loadNewAd(activity);
                        if (listener != null) listener.onAdClosed();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.d(TAG, "Ad showed successfully.");
                    }
                });

                mInterstitialAd.show(activity);
            } else {
                Log.d(TAG, "Ad not ready yet. Retrying on next click.");
                clickCount = SHOW_AD_EVERY_N_CLICKS - 1; // অ্যাড না থাকলে পরের ক্লিকে যেন আবার চেষ্টা করে
                loadNewAd(activity);
                if (listener != null) listener.onAdClosed();
            }
        } else {
            // ২য় ও ৩য় ক্লিকে অ্যাড শো হবে না, তবে লোড না থাকলে লোড করে নেওয়া হবে
            if (mInterstitialAd == null && !isAdLoading) {
                loadNewAd(activity);
            }
            if (listener != null) listener.onAdClosed();
        }
    }

    private static void loadNewAd(Activity activity) {
        if (isAdLoading || mInterstitialAd != null) {
            return;
        }

        isAdLoading = true;
        AdRequest adRequest = new AdRequest.Builder().build();

        // উপরে ডিফাইন করা INTERSTITIAL_AD_ID ব্যবহার করা হচ্ছে
        InterstitialAd.load(activity.getApplicationContext(), INTERSTITIAL_AD_ID, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(InterstitialAd interstitialAd) {
                        Log.d(TAG, "Interstitial ad loaded.");
                        mInterstitialAd = interstitialAd;
                        isAdLoading = false;
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.d(TAG, "Failed to load ad: " + loadAdError.getMessage());
                        mInterstitialAd = null;
                        isAdLoading = false;
                    }
                });
    }

    // Banner Ad লোড করার মেথড
    public static void loadBannerAd(Activity activity, FrameLayout adContainer) {
        if (adContainer == null) return;

        mAdView = new AdView(activity);
        mAdView.setAdSize(AdSize.BANNER);

        // উপরে ডিফাইন করা BANNER_AD_ID ব্যবহার করা হচ্ছে
        mAdView.setAdUnitId(BANNER_AD_ID);

        adContainer.removeAllViews();
        adContainer.addView(mAdView);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public static void clearBannerAd() {
        if (mAdView != null) {
            mAdView.destroy();
            mAdView = null;
        }
    }
}
package com.pluk.fiveballs.ads;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.pluk.fiveballs.BuildConfig;

public class AdManager {

    public static void addAd(Activity activity, ViewGroup root, AdListener listener) {

        // Create a new ad view.
        AdView adView = new AdView(activity);
        adView.setAdSize(getAdSize(activity, root));
        adView.setAdUnitId(BuildConfig.ADMOB_APPID);

        // Replace ad container with new ad view.
        root.removeAllViews();
        root.addView(adView);

        adView.setAdListener(listener);

        // Start loading the ad in the background.
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private static AdSize getAdSize(Activity activity, ViewGroup root) {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = root.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

}

package com.pluk.fiveballs.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.pluk.fiveballs.view.FiveMore;

public class Navigation {

    public static void goToAndroidMarket(Activity activity) {
        String PLAYSTORE_PACKAGE_NAME = "com.pluk.fiveballs";
        goPlaystore(activity, PLAYSTORE_PACKAGE_NAME);
    }

    public static void goToNoAds(Activity activity) {
        String PLAYSTORE_PACKAGE_NAME = "com.pluk.fiveballs.noads";
        goPlaystore(activity, PLAYSTORE_PACKAGE_NAME);
    }

    private static void goPlaystore(Activity activity, String PLAYSTORE_PACKAGE_NAME) {
        try {
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PLAYSTORE_PACKAGE_NAME));
            activity.startActivity(goToMarket);

        } catch (android.content.ActivityNotFoundException e) {
            Intent goWeb = new Intent(Intent.ACTION_VIEW);
            goWeb.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + PLAYSTORE_PACKAGE_NAME));
            activity.startActivity(goWeb);
        }
    }

    public static void goToPrivacyPolices(Activity activity) {
        Intent goWeb = new Intent(Intent.ACTION_VIEW);
        goWeb.setData(Uri.parse("https://plukapps.github.io/privacypolicy/fiveballs"));
        activity.startActivity(goWeb);
    }
}

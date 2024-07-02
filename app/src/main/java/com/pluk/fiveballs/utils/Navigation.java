package com.pluk.fiveballs.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class Navigation {

    public static void goToAndroidMarket(Activity activity) {
        String PLAYSTORE_PACKAGE_NAME = "com.pluk.fiveballs";

        try {
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PLAYSTORE_PACKAGE_NAME));
            activity.startActivity(goToMarket);

        } catch (android.content.ActivityNotFoundException e) {
            Intent goWeb = new Intent(Intent.ACTION_VIEW);
            goWeb.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + PLAYSTORE_PACKAGE_NAME));
            activity.startActivity(goWeb);
        }
    }

}

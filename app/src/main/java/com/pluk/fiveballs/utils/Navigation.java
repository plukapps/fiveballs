package com.pluk.fiveballs.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class Navigation {

    public static void goToAndroidMarket(Activity activity) {
        String PLAYSTORE_PACKAGE_NAME = "com.pluk.fiveballs";
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PLAYSTORE_PACKAGE_NAME));
        activity.startActivity(goToMarket);
    }

}

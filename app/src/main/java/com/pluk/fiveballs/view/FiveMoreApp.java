package com.pluk.fiveballs.view;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.pluk.fiveballs.BuildConfig;
import com.pluk.fiveballs.R;

import java.util.Arrays;

public class FiveMoreApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        configFirebase();
        configAds();
        configRemoteConfig();
    }

    private void configFirebase() {
        FirebaseApp.initializeApp(this);
    }

    private void configAds() {
        new Thread(
                () -> {

                    if (BuildConfig.BUILD_TYPE != "release") {
                        RequestConfiguration.Builder builder = new RequestConfiguration.Builder().setTestDeviceIds(
                                Arrays.asList("EF77E807927DB66D409E2C24C35840A0", "1D97A3BAA347D78AE599EF8CB336A007")
                        );
                        MobileAds.setRequestConfiguration(builder.build());
                    }

                    // Initialize the Google Mobile Ads SDK on a background thread.
                    MobileAds.initialize(this, initializationStatus -> {});
                })
                .start();
    }

    private void configRemoteConfig() {
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(60)
                .build();
        remoteConfig.setConfigSettingsAsync(configSettings);

        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);

//        remoteConfig.addOnConfigUpdateListener(new ConfigUpdateListener() {
//            @Override
//            public void onUpdate(@NonNull ConfigUpdate configUpdate) {
//                Log.d("FiveMoreApp", "Updated keys: " + configUpdate.getUpdatedKeys());
//                remoteConfig.activate();
//            }
//
//            @Override
//            public void onError(@NonNull FirebaseRemoteConfigException error) {
//                Log.d("FiveMoreApp", "Updated fail:" + error);
//            }
//        });
    }
}

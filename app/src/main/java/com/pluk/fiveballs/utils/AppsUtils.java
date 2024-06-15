package com.pluk.fiveballs.utils;
import com.pluk.fiveballs.R;
import com.pluk.fiveballs.model.Consts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.Toast;


public class AppsUtils {
	public static final String TAG = "FiveBalls"; 
	
	public static void showToast(Context context, int resId) {
		Toast toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
		toast.show();	
	}
	
	public static void showToast(Context context, String msg) {
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public static void goToBrowser(Activity activity, String url) {
    	if (url != null && !url.equals("")) {
	    	Uri uri = Uri.parse(url);
			activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));
    	} else {
    		//showToast(activity, R.string.banners_link_broken);
    	}
	}
	
	public static void playAudio(Context context, MediaPlayer mp, SoundUtils.SoundType soundType) {
		if (isAudioEnabled(context)){
			SoundUtils.play(context, mp, soundType);
		}
	}
	
	private static boolean isAudioEnabled(Context context){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    	boolean audioPref = prefs.getBoolean(Consts.preferences.SOUND_ENABLED_PREFERENCE_KEY, true);
    	return audioPref;
    }

	public static void goToWebSite(Activity activity) {
		Uri uri = Uri.parse(activity.getString(R.string.fb_website_url));
		activity.startActivity(new Intent( Intent.ACTION_VIEW, uri ) );
	}
	
	public static void share(Activity activity){
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.fb_main_share_msg));
		/*
		 * Este es el q toma el Share en Facebook
		 */
		shareIntent.putExtra(Intent.EXTRA_TEXT, "http://fiveballs.androiduy.com/share.html");
		activity.startActivity(Intent.createChooser(shareIntent, "http://fiveballs.androiduy.com/share.html I have been playing fiveballs"));
	}
	

}

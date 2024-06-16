package com.pluk.fiveballs.view;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pluk.fiveballs.R;
import com.pluk.fiveballs.model.Consts;
import com.pluk.fiveballs.utils.SoundUtils;
import com.pluk.fiveballs.widgets.RankingDialog;

/**
 * Activity que muestra una pantalla de bienvenida. 
 * 
 * @author santilod
 *
 */
public class FiveMore extends Activity implements OnClickListener {

	private static final String TAG = "SplashScreenActivity";

	private static final int DIALOG_SHOW_SCORES = 1;
	
	private MediaPlayer mp = null;

	private int mStarSize;

	private int mStarRadio;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play);
		
		// Play button
//		Button vPlayButton = (Button) findViewById(R.id.start_button_play);
	
		random = new Random(Calendar.getInstance().getTimeInMillis());
		
		
		mStarSize = getResources().getDimensionPixelOffset(R.dimen.aqua_play_stars_size);
		mStarRadio = mStarSize / 2;
		
		boolean showStars = getResources().getBoolean(R.bool.show_stars_in_home);
		if (showStars) {
			createStar(random.nextInt(1700));
			createStar(random.nextInt(1700));
			createStar(random.nextInt(1700));
			createStar(random.nextInt(1700));	
		}
	}

	private void createStar(int delay) {
//		int delay = 0;
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						showStar(random.nextInt(480), random.nextInt(800));
					}
				});	
			}
		}, delay);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		mp = MediaPlayer.create(this.getApplicationContext(), R.raw.bubble2);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(mp != null){
    		mp.release();
    	}
	}

	// Usar overridePendingTransition(R.anim.fade, R.anim.hold);
	public void setActivityAnimation(Activity activity, int in, int out) { 
		Log.i(TAG, "setActivityAnimation");
	    try {
	        Method method = Activity.class.getMethod("overridePendingTransition", new Class[]{int.class, int.class});
	        method.invoke(activity, in, out);
	    } catch (Exception e) {
	    	Log.w(TAG, e);
	    	getWindow().setWindowAnimations(in);
	    }
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.start_button_play:
				goToGameActivity();
				playAudio(SoundUtils.SoundType.CLICK);
				break;
			case R.id.start_settings_btn:
				goToSettingsActicity();
				playAudio(SoundUtils.SoundType.CLICK);
				break;
			case R.id.start_scores_btn:
				showDialog(DIALOG_SHOW_SCORES);
				break;
			case R.id.start_rate_app:
				goToAndroidMarket();
				playAudio(SoundUtils.SoundType.CLICK);
				break;
			default:
				break;
		}
	}
	
	private void goToSettingsActicity() {
		Intent intent = new Intent(this, OptionsActivity.class);
    	startActivity(intent);
    	setActivityAnimation(FiveMore.this, R.anim.fade, R.anim.hold);
	}

	private void goToGameActivity() {
		Intent intent = new Intent(FiveMore.this, GameActivity.class);
		startActivity(intent);
		setActivityAnimation(FiveMore.this, R.anim.fade, R.anim.hold);
	}
	
	private void goToAndroidMarket() {
		Intent goToMarket = null;
		goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.pluk.fiveballs.view"));
		startActivity(goToMarket);
	}
	
	public void playAudio(SoundUtils.SoundType soundType) {
		if (isAudioEnabled()){
			SoundUtils.play(this, mp, soundType);
		}
	}
	
	public boolean isAudioEnabled(){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	boolean audioPref = prefs.getBoolean(Consts.preferences.SOUND_ENABLED_PREFERENCE_KEY, true);
    	return audioPref;
    }

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
//			showStar(random.nextInt(480), random.nextInt(800), random.nextInt(10000));
		}
	}

	private void showStar(float x, float y) {
		
		int durationMillis = 1700;
		
		ImageView star = new ImageView(this);
		
		switch (random.nextInt(4)) {
			case 0:
				star.setImageResource(R.drawable.i_star_white_80);
				break;
			case 1:
				star.setImageResource(R.drawable.i_star_green_80);
				break;
			case 2:
				star.setImageResource(R.drawable.i_star_orange_80);
				break;
			case 3:
				star.setImageResource(R.drawable.i_star_violet_80);
				break;
		}
		
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.start_panel);
		layout.addView(star, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		TranslateAnimation tr = new TranslateAnimation(
					Animation.ABSOLUTE, x, 
					Animation.ABSOLUTE, x, 
					Animation.ABSOLUTE, y,
					Animation.ABSOLUTE, y);
		tr.setDuration(100);
		tr.setFillAfter(true);
		
		AlphaAnimation aaa = new AlphaAnimation(0f, 1.0f);
		aaa.setDuration(200);
		
		RotateAnimation ra = new RotateAnimation(0f, 400f, x+mStarRadio, y+mStarRadio);
		ra.setStartOffset(100);
		ra.setFillAfter(true);
		ra.setDuration(durationMillis);
		
		float scaleInit = 0.2f;
		float scaleEnd = random.nextFloat() * 0.8f;
		
		scaleInit = 0.3f;
		scaleEnd = 0.8f;
		
		ScaleAnimation sa = new ScaleAnimation(scaleInit, scaleEnd, scaleInit, scaleEnd, x+mStarRadio, y+mStarRadio);
		sa.setFillAfter(true);
		sa.setStartOffset(100);
		sa.setDuration(durationMillis);
		
//		AlphaAnimation aa = new AlphaAnimation(1f, 0.0f);
//		aa.setStartOffset(durationMillis-300);
//		aa.setDuration(300);
		
		AnimationSet set = new AnimationSet(true);
		set.setFillAfter(true);
		set.addAnimation(tr);
//		set.addAnimation(aaa);
		set.addAnimation(ra);
		set.addAnimation(sa);
//		set.addAnimation(aa);
		set.setAnimationListener(new StarAnimationListener(star));
		
		star.setAnimation(set);
	}
	
	private class StarAnimationListener implements AnimationListener {
		
		private final View view;
		private final ViewGroup parent;

		public StarAnimationListener(View view) {
			this.view = view;
			this.parent = (ViewGroup) this.view.getParent();
		}

		public void onAnimationEnd(Animation arg0) {
			this.view.setVisibility(View.GONE);
			this.parent.post(new Runnable() {
	            public void run() {
	                FiveMore.this.runOnUiThread(new Runnable() {
	                    public void run() {
	                        parent.removeView(view);
	                        createStar(random.nextInt(1700));
	                    }
	                });
	            }
			});
		}
		
		public void onAnimationRepeat(Animation arg0) {
		}
		public void onAnimationStart(Animation arg0) {
		}
	}
	
	private RankingDialog.RankingMode rankingMode = RankingDialog.RankingMode.LOCAL;
	private int position = 0;
	private int currentPage = 0;

	private Random random;
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case DIALOG_SHOW_SCORES:
				RankingDialog dialog = new RankingDialog(this, R.style.AquaTheme_Dialog, mp);
				dialog.setOnDismissListener(new OnDismissListener() {

					public void onDismiss(DialogInterface d) {
						RankingDialog dialog = (RankingDialog) d;
						rankingMode = dialog.getRankingMode();;
						position = dialog.getCurrentRanking();
						currentPage = dialog.getCurrentPage();
					}
				});
				return dialog;
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
			case DIALOG_SHOW_SCORES:
				RankingDialog rankingDialog = (RankingDialog) dialog;
				switch (rankingMode) {
					case LOCAL: rankingDialog.setLocalScores();	break;
					case GLOBAL: rankingDialog.setGlobalScores(position, currentPage); break;
					case WEEKLY: rankingDialog.setWeeklyScores(); break;
				}
				break;
		}
	}


    

}
package com.pluk.fiveballs.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.pluk.fiveballs.BuildConfig;
import com.pluk.fiveballs.R;
import com.pluk.fiveballs.ads.AdManager;
import com.pluk.fiveballs.exceptions.EmptyCellExeption;
import com.pluk.fiveballs.handlers.FactoryGame;
import com.pluk.fiveballs.handlers.IGameManager;
import com.pluk.fiveballs.model.BallColor;
import com.pluk.fiveballs.model.Consts;
import com.pluk.fiveballs.model.Coord;
import com.pluk.fiveballs.model.ImageType;
import com.pluk.fiveballs.persistence.PuntajeDB;
import com.pluk.fiveballs.utils.AppsUtils;
import com.pluk.fiveballs.utils.Navigation;
import com.pluk.fiveballs.utils.SoundUtils;
import com.pluk.fiveballs.utils.SoundUtils.SoundType;
import com.pluk.fiveballs.widgets.AboutDialog;
import com.pluk.fiveballs.widgets.HelpGameDialog;
import com.pluk.fiveballs.widgets.HighScoreDialog;
import com.pluk.fiveballs.widgets.NewGameDialog;
import com.pluk.fiveballs.widgets.RankingDialog;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.OptionalInt;

public class GameActivity extends Activity implements ViewSwitcher.ViewFactory, OnClickListener{
	
	private static final String TAG = "GameActivity";
	
	private static IGameManager aGame;
	private MediaPlayer mp = null;
	private PuntajeDB persistencia;
	public static ImageType imageType;
	private boolean showAnimationsLayouts;
	
	//Opciones del menú
	private static int NEW_GAME;
	private static int OPTIONS;
	private static int HELP;
	private static int ABOUT;
	
	// Preferences keys
	public static final String PREFERENCE_SOUND = "sound";
	public static final String PREFEREBCE_IMAGES = "imageTypes";

	private ImageView vControlRestart;
	private ImageView vControlRanking;
	private ImageView vControlSound;
	private ImageView vControlRateApp;
	private RelativeLayout mGameBoardView;
	private TextView mScoreSwitcher;

	RankingDialog.RankingMode rankingMode = RankingDialog.RankingMode.LOCAL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG, "onCreate Fivemore");
    	
        super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// Se carga las preferencias de la imagenes
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String type = prefs.getString(PREFEREBCE_IMAGES, ImageType.BALLS.toString());
		
		if (ImageType.BALLS.toString().equals(type)) {
			imageType = ImageType.BALLS;
		} else if (ImageType.BALLS.toString().equals(type)) {
			imageType = ImageType.SHAPES;
		} else {
			imageType = ImageType.STARS;
		}
		
		aGame = FactoryGame.newGame();
        setPersistencia(new PuntajeDB(this.getApplicationContext()));
        
        mGameBoardView = (RelativeLayout) findViewById(R.id.gameBoard);
		vControlRestart = (ImageView) findViewById(R.id.vControlRestart);
        vControlRanking = (ImageView) findViewById(R.id.vControlRanking);
		vControlSound = (ImageView) findViewById(R.id.vControlSound);
		vControlRateApp = (ImageView) findViewById(R.id.vControlRateApp);
    	mScoreSwitcher = (TextView) findViewById(R.id.scoreValue);

		setSoundIcon(isAudioEnabled());

    	vControlRestart.setOnClickListener(this);
    	vControlRanking.setOnClickListener(this);
		vControlSound.setOnClickListener(this);
		vControlRateApp.setOnClickListener(this);

//    	vControlShare.setOnClickListener(shareActionListener());

    	// Score 
		updateScore(0);

//        mAdView = (AdView)this.findViewById(R.id.adView);
//        if (mAdView != null) {
//	        AdRequest adRequest = new AdRequest();
////	        adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
////	        adRequest.addTestDevice("C2EEE67F6E791D37BAF4946F80533D8B");
//	        mAdView.loadAd(adRequest);
//        }

		loadTop1LocalScore();
		loadBanner();

        try {
			loadAd();
			startGame();

		} catch (Exception e) {
			Log.e(TAG, "startGame: no funciono", e);
		}

    }

	private InterstitialAd mInterstitialAd;

	private void loadAd() {
		boolean flag = FirebaseRemoteConfig.getInstance().getBoolean("ads_show_fullscreen_restart");
		if (!flag) {
			return;
		}

		Log.i(TAG, "loadFullScreeAds");


		AdRequest adRequest = new AdRequest.Builder().build();

		InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest,
			new InterstitialAdLoadCallback() {
				@Override
				public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
					// The mInterstitialAd reference will be null until
					// an ad is loaded.
					mInterstitialAd = interstitialAd;
					setInterstitialCallback();
					Log.i(TAG, "onAdLoaded");
				}

				@Override
				public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
					// Handle the error
					Log.d(TAG, loadAdError.toString());
					mInterstitialAd = null;
				}
			});


	}

	private void setInterstitialCallback() {
		mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
			@Override
			public void onAdClicked() {
				// Called when a click is recorded for an ad.
				Log.d(TAG, "Ad was clicked.");
			}

			@Override
			public void onAdDismissedFullScreenContent() {
				// Called when ad is dismissed.
				// Set the ad reference to null so you don't show the ad a second time.
				Log.d(TAG, "Ad dismissed fullscreen content.");
				mInterstitialAd = null;
				restartGameImpl();
			}

			@Override
			public void onAdFailedToShowFullScreenContent(AdError adError) {
				// Called when ad fails to show.
				Log.e(TAG, "Ad failed to show fullscreen content.");
				mInterstitialAd = null;
			}

			@Override
			public void onAdImpression() {
				// Called when an impression is recorded for an ad.
				Log.d(TAG, "Ad recorded an impression.");
			}

			@Override
			public void onAdShowedFullScreenContent() {
				// Called when ad is shown.
				Log.d(TAG, "Ad showed fullscreen content.");
			}
		});
	}

	public void loadBanner() {

		boolean flag = FirebaseRemoteConfig.getInstance().getBoolean("ads_show_banner_game");

		if (!flag) {
			getAdContainer().setVisibility(View.GONE);
		} else {
			getAdContainer().setVisibility(View.VISIBLE);

			AdManager.addAd(this, getAdContainer(), new AdListener() {
				@Override
				public void onAdClicked() {
					// Code to be executed when the user clicks on an ad.
					Log.i("Santi", "onAdClicked()");
				}

				@Override
				public void onAdClosed() {
					// Code to be executed when the user is about to return
					// to the app after tapping on an ad.
					Log.i("Santi", "onAdClosed()");
				}

				@Override
				public void onAdFailedToLoad(LoadAdError adError) {
					// Code to be executed when an ad request fails.
					Log.i("Santi", "onAdFailedToLoad()" + adError);
				}

				@Override
				public void onAdImpression() {
					// Code to be executed when an impression is recorded
					// for an ad.
					Log.i("Santi", "onAdImpression()");
				}

				@Override
				public void onAdLoaded() {
					// Code to be executed when an ad finishes loading.
					Log.i("Santi", "onAdLoaded()");
				}

				@Override
				public void onAdOpened() {
					// Code to be executed when an ad opens an overlay that
					// covers the screen.
					Log.i("Santi", "onAdOpened()");
				}
			});
		}
	}

	private ViewGroup getAdContainer() {
		return findViewById(R.id.adContainer);
	}

	private void loadTop1LocalScore() {

		TextView vTop1LocalScore = (TextView) findViewById(R.id.bestScoreValue);

		PuntajeDB persistencia = new PuntajeDB(getApplicationContext());
		List<PuntajeDB.Row> lscores = persistencia.fetchAllRows();

		OptionalInt maxScore = lscores.stream().mapToInt(s -> s.score).max();
		if (maxScore.isPresent()) {
			vTop1LocalScore.setText("" + maxScore.getAsInt());
		} else {
			vTop1LocalScore.setText("0");
		}
	}
    
	@Override
	protected void onStart() {
    	Log.i(TAG, "onStart Fivemore");
		super.onStart();
	}
    
    @Override
	protected void onResume() {
    	Log.i(TAG, "onResume Fivemore");
    	
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this); 
    	String type = prefs.getString(PREFEREBCE_IMAGES, ImageType.BALLS.toString());
		if (ImageType.BALLS.toString().equals(type) && imageType != ImageType.BALLS) {
			imageType = ImageType.BALLS;
			changeImageTypes();	
		} else if (ImageType.SHAPES.toString().equals(type) && imageType != ImageType.SHAPES) {
			imageType = ImageType.SHAPES;
			changeImageTypes();
		} else if (ImageType.STARS.toString().equals(type) && imageType != ImageType.STARS) {
			imageType = ImageType.STARS;
			changeImageTypes();
		}
		super.onResume();
		showAnimationsLayouts = true;
	}
    
    @Override
	public void onWindowFocusChanged(boolean hasFocus) {
    	Log.i(TAG, "onWindowFocusChanged hasFocus = " + hasFocus);
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus && showAnimationsLayouts) {
			
			Animation gameBoardAnimation = AnimationUtils.loadAnimation(this, R.anim.to_up);
			Animation gameButtonAnimation = AnimationUtils.loadAnimation(this, R.anim.to_right);
			Animation rankingButtonAnimation = AnimationUtils.loadAnimation(this, R.anim.to_down);
			Animation shareButtonAnimation = AnimationUtils.loadAnimation(this, R.anim.to_left);

			mGameBoardView.startAnimation(gameBoardAnimation);
//			mGameButtonView.startAnimation(gameButtonAnimation);
//			mRankingButtonView.startAnimation(rankingButtonAnimation);
//			mShareButtonView.startAnimation(shareButtonAnimation);

			showAnimationsLayouts = false;
		}
    }

	@Override
	protected void onRestart() {
		Log.i(TAG, "onRestart Fivemore");
		super.onRestart();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause Fivemore");
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		Log.i(TAG, "onStop Fivemore");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy Fivemore");
		
		super.onDestroy();
    	
    	//Libero el MediaPlayer para liberar los recursos
    	if(mp != null){
    		mp.release();
    	}
    	//Cierro la base de datos abierta
    	if ( persistencia != null)
    		persistencia.close();
    }

	@Nullable
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		return super.onCreateDialog(id, args);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
			case R.id.vControlRestart:
				showDialog(DIALOG_RESTART);
				playAudio(SoundType.CLICK);
				break;
			case R.id.vControlRanking:
				showDialog(DIALOG_SHOW_SCORES);
				playAudio(SoundType.CLICK);
				break;
			case R.id.vControlSound:
				toogleAudio();
				playAudio(SoundType.CLICK);
				break;
			case R.id.vControlRateApp:
				Navigation.goToAndroidMarket(this);
				playAudio(SoundUtils.SoundType.CLICK);
				break;
//			case R.id.share:
//				vControlShare.setOnClickListener(shareActionListener());
//				playAudio(SoundUtils.SoundType.CLICK);
//				break;

			default:
				break;
		}
	}

	public boolean isAudioEnabled(){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	boolean audioPref = prefs.getBoolean(Consts.preferences.SOUND_ENABLED_PREFERENCE_KEY, true);
    	return audioPref;
    }

	public boolean toogleAudio(){
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		boolean audioPref = prefs.getBoolean(Consts.preferences.SOUND_ENABLED_PREFERENCE_KEY, true);

		boolean newValue = !audioPref;
		prefs.edit().putBoolean(Consts.preferences.SOUND_ENABLED_PREFERENCE_KEY, newValue).commit();

		setSoundIcon(newValue);

		return newValue;
	}

	private void setSoundIcon(boolean enabled) {
		if (enabled) {
			vControlSound.setImageResource(R.drawable.round_volume_up_black_48);
		} else {
			vControlSound.setImageResource(R.drawable.round_volume_off_black_48);
		}
	}


	private OnClickListener shareActionListener() {
    	return new OnClickListener() {
			public void onClick(View arg0) {
				Navigation.goToAndroidMarket(GameActivity.this);
				playAudio(SoundType.CLICK);
			}
    	};
	}
    
    //muestra las opciones editables del juego
    private void goToOptions() {
    	Intent settingsActivity = new Intent(getBaseContext(), OptionsActivity.class);
    	startActivity(settingsActivity);
    }
    
    public void shareScoreIntent(String name, int score){
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		
		shareIntent.putExtra(Intent.EXTRA_SUBJECT, "I am playing FiveBalls on my android phone.");
		/*
		 * Este es el q toma el Share en Facebook
		 */
		shareIntent.putExtra(Intent.EXTRA_TEXT, "http://fiveballs.androiduy.com/share?name=" + URLEncoder.encode(name) + "&score=" + String.valueOf(score));
		startActivity(Intent.createChooser(shareIntent, "http://fiveballs.androiduy.com/share.html I have been playing fiveballs"));
	}
	
    
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu, menu);
	    
	    //id del item en el menu
	    NEW_GAME = menu.getItem(0).getItemId();
	    OPTIONS = menu.getItem(1).getItemId();
	    HELP = menu.getItem(2).getItemId();
	    ABOUT = menu.getItem(3).getItemId();
	    
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		playAudio(SoundType.CLICK);
		if (item.getItemId() == NEW_GAME) {
			showDialog(DIALOG_RESTART);
		} else if (item.getItemId() == OPTIONS) {
			goToOptions();
		} else if (item.getItemId() == HELP) {
			showDialog(DIALOG_HELP);

		} else if (item.getItemId() == ABOUT) {
			showDialog(DIALOG_ABOUT);
			
		} else {
			return super.onContextItemSelected(item);
		}
		
		return true;
	}

	public void startGame() throws Exception {
    	
    	// Busco las bolas iniciales y sus colores.
		LinkedList<BallColor> initBalls = aGame.getNextColorBalls();
		HashMap<Coord, BallColor> newBalls = aGame.addBalls(initBalls);	
		mp = MediaPlayer.create(this.getApplicationContext(), R.raw.bubble2);
		
		// Se agregan los eventos a cada celda del tablero
    	for (int i = 0; i < Consts.game.gridsize; i++) {
			for (int j = 0; j < Consts.game.gridsize; j++) {

				CellView cellView = new CellView(aGame, this, mp);
				
				Coord currentCoord = new Coord(i,j);
				
				for (Entry<Coord, BallColor> entry : newBalls.entrySet()) {
					Coord coord = entry.getKey();
					if (coord.equals(currentCoord)) {
						cellView.addBall(coord, entry.getValue());
						break;
					}
				}
				
				ImageView ballView = cellView.findByCoord(currentCoord);
				ballView.setOnClickListener(cellView);
			}
		}
		
		float fromXScale = 0.3f;
		float toXScale = 1.0f;
		float fromYScale = 0.3f;
		float toYScale = 1.0f;
	    float pivotX = 0.5f;
	    float pivotY = 0.5f;
	    Animation animation = new ScaleAnimation(fromXScale, toXScale, fromYScale, toYScale, 
	    		Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
		animation.setDuration(Consts.animation.scaleDelayMillis);
		animation.setFillAfter(false);
		
		// Se actualiza las bolas siguientes
		LinkedList<BallColor> nextBalls = aGame.getNextColorBalls();;
		
		ImageView nextball1 = (ImageView) findViewById(R.id.NextBall1);
		ImageView nextball2 = (ImageView) findViewById(R.id.NextBall2);
		ImageView nextball3 = (ImageView) findViewById(R.id.NextBall3);
		nextball1.setImageResource(BallColor.getImageResource(nextBalls.get(0), imageType));
		nextball2.setImageResource(BallColor.getImageResource(nextBalls.get(1), imageType));
		nextball3.setImageResource(BallColor.getImageResource(nextBalls.get(2), imageType));

		ImageView noball = (ImageView) findViewById(R.id.ballTmp);
		noball.setVisibility(View.GONE);

		nextball1.startAnimation(animation);
		nextball2.startAnimation(animation);
		nextball3.startAnimation(animation);
	}
	
	public void restartGame() {
		if (mInterstitialAd != null) {
			mInterstitialAd.show(GameActivity.this);
		} else {
			restartGameImpl();
			Log.d("TAG", "The interstitial ad wasn't ready yet.");
		}
	}

	private void restartGameImpl() {
		try {

			boolean hasSelected = aGame.hasSelected();
			if (hasSelected) {
				Coord selected = aGame.getSelected();
				ImageView imageView = (ImageView) findByCoord(selected.getX(), selected.getY());
				imageView.clearAnimation();
			}

			// Se reinicia el score
			updateScore(Integer.valueOf(0));

			for (int i = 0; i < Consts.game.gridsize; i++) {
				for (int j = 0; j < Consts.game.gridsize; j++) {

					// Id del frame
					ImageView imageView = findByCoord(i, j);
					imageView.setBackgroundResource(R.drawable.noball);
					imageView.setOnClickListener(null);
				}
			}
			aGame = FactoryGame.restart();

			loadAd();
			startGame();

		} catch (Exception e) {
			Log.e(TAG, "No deberia pasar nunca.", e);
		}
	}

	public ImageView findByCoord(int i, int j) {
		int id = getResources().getIdentifier("ball"+i+j, "id" , BuildConfig.APPLICATION_ID);
		return (ImageView) findViewById(id);
	}
  
    
	public void setPersistencia(PuntajeDB persistencia) {
		this.persistencia = persistencia;
	}

	public PuntajeDB getPersistencia() {
		return persistencia;
	}

	// ================================================================
	// 			Dialogs
	// ================================================================
	
	public static final int DIALOG_RESTART = 1;
	public static final int DIALOG_SHOW_SCORES = 2;
	public static final int DIALOG_NEW_SCORE = 5;
	public static final int DIALOG_HELP = 3;
	public static final int DIALOG_ABOUT = 6;
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");

//	private AdView mAdView;

	private int currentPosition = 0;
	private int currentPage = 0;
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {

			case DIALOG_RESTART:
				NewGameDialog newGameDialog = new NewGameDialog(this, R.style.AquaTheme_Dialog, mp);
				newGameDialog.setOnNewGameListener(new NewGameDialog.OnNewGameListener() {
					public void onNewGame() {
						restartGame();
					}
				});
				return newGameDialog;
				
			case DIALOG_SHOW_SCORES:
				RankingDialog rankingDialog = new RankingDialog(this, R.style.AquaTheme_Dialog, mp);
				rankingDialog.setOnDismissListener(new OnDismissListener() {
					public void onDismiss(DialogInterface d) {
						RankingDialog dialog = (RankingDialog) d;
						rankingMode = dialog.getRankingMode();;
						currentPosition = dialog.getCurrentRanking();
						currentPage = dialog.getCurrentPage();
					}
				});
				return rankingDialog;
			
			case DIALOG_HELP: 
				HelpGameDialog helpDialog = new HelpGameDialog(this, R.style.AquaTheme_Dialog, mp);
				return helpDialog;
				
			case DIALOG_ABOUT: 
				AboutDialog aboutDialog = new AboutDialog(this, R.style.AquaTheme_Dialog, mp);
				aboutDialog.setAboutDialogListener(new AboutDialog.AboutDialogListener() {
					public void onShare() {
						AppsUtils.share(GameActivity.this);
					}
					public void onGoToWeb() {
						AppsUtils.goToWebSite(GameActivity.this);
					}
				});
				return aboutDialog;
				
			case DIALOG_NEW_SCORE:
				HighScoreDialog highScoreDialog = new HighScoreDialog(this, R.style.AquaTheme_Dialog, mp, persistencia);
				highScoreDialog.setOnSumbitScoreListener(new HighScoreDialog.OnSumbitHighScoreListener() {
					public void showGlobalsScores(int position, String name, int page) {
						currentPosition = position;
						currentPage = page;
						rankingMode = RankingDialog.RankingMode.GLOBAL;
						showDialog(DIALOG_SHOW_SCORES);
					}
					public void shareScore(String name, int score, int position, int page) {
						shareScoreIntent(name, score);
					}
					public void onSumbitLocalScore() {
						rankingMode = RankingDialog.RankingMode.LOCAL;
						showDialog(DIALOG_SHOW_SCORES);
					}
				});
				return highScoreDialog;
		}
		return super.onCreateDialog(id);
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		
		StringBuilder stringBuilder;
		
		switch (id) {
			case DIALOG_SHOW_SCORES:
				RankingDialog rankingDialog = (RankingDialog) dialog;
				switch (rankingMode) {
					case LOCAL: rankingDialog.setLocalScores();	break;
					case GLOBAL: rankingDialog.setGlobalScores(); break;
					case WEEKLY: rankingDialog.setWeeklyScores(); break;
				}
				break;
			case DIALOG_NEW_SCORE:
				Log.i(TAG, "prepate newScoreDialog");
				
				HighScoreDialog highScoreDialog = (HighScoreDialog) dialog;
				highScoreDialog.setScore(aGame.getScore());
				
				// Preparo el texto para el contenido del dialogo
		    	String finishString = getString(R.string.fb_upload_score_gameover);
				String scoreString = getString(R.string.fb_upload_score_game);
				stringBuilder = new StringBuilder();
				stringBuilder.append(finishString);
				stringBuilder.append("\n");
				stringBuilder.append(scoreString);
				stringBuilder.append("  ");
				stringBuilder.append(aGame.getScore());
		
				highScoreDialog.setHighScoreMsg(stringBuilder.toString());
				break;
		}
	}

	public void changeImageTypes() {
    	for (int i = 0; i < Consts.game.gridsize; i++) {
			for (int j = 0; j < Consts.game.gridsize; j++) {
				Coord coord = new Coord(i,j);
				if (aGame.hasBall(coord)) {
					try {
						BallColor ballColor = aGame.getBallColor(coord);
						ImageView imageView = (ImageView) findByCoord(i, j);
						int resourceAnimation;
						if (GameActivity.imageType.equals(ImageType.BALLS)) {
							resourceAnimation = BallColor.getResourceAnimation(ballColor, GameActivity.imageType);
						} else { // ImageType.SHAPES or ImageType.STARS 
							resourceAnimation = BallColor.getImageResource(ballColor, GameActivity.imageType);
						}
						imageView.clearAnimation();
						imageView.setBackgroundResource(resourceAnimation);
					} catch (EmptyCellExeption e) {
						Log.e(TAG, "No deberia pasar", e);
						e.printStackTrace();
					}
				}
			}
		}	
    	
    	LinkedList<BallColor> currentNextBalls = aGame.getCurrentNextBalls();
    	
    	ImageView nextball1 = (ImageView) findViewById(R.id.NextBall1);
		ImageView nextball2 = (ImageView) findViewById(R.id.NextBall2);
		ImageView nextball3 = (ImageView) findViewById(R.id.NextBall3);
		nextball1.setImageResource(BallColor.getImageResource(currentNextBalls.get(0), imageType));
		nextball2.setImageResource(BallColor.getImageResource(currentNextBalls.get(1), imageType));
		nextball3.setImageResource(BallColor.getImageResource(currentNextBalls.get(2), imageType));
		
	}

	public View makeView() {
		 TextView t = new TextView(this/*, attributes*/);
		 t.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24.f);
		 Typeface font = ResourcesCompat.getFont(this, R.font.digital_7);
		 t.setTypeface(font);
		 return t;
	}
	
	public void updateScore(int score) {
        String scoreStr = String.format("%d", Integer.valueOf(score));
		mScoreSwitcher.setText(scoreStr);
	}
	
	public void playAudio(SoundType soundType) {
		if (isAudioEnabled()){
			SoundUtils.play(this, mp, soundType);
		}
	}
}
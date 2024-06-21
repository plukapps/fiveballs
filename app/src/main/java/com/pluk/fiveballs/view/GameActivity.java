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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.core.content.res.ResourcesCompat;

import com.pluk.fiveballs.R;
import com.pluk.fiveballs.exceptions.EmptyCellExeption;
import com.pluk.fiveballs.handlers.FactoryGame;
import com.pluk.fiveballs.handlers.IGameManager;
import com.pluk.fiveballs.model.BallColor;
import com.pluk.fiveballs.model.Consts;
import com.pluk.fiveballs.model.Coord;
import com.pluk.fiveballs.model.ImageType;
import com.pluk.fiveballs.persistence.PuntajeDB;
import com.pluk.fiveballs.utils.AppsUtils;
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
import java.util.Map.Entry;

public class GameActivity extends Activity implements ViewSwitcher.ViewFactory {
	
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
	private ImageView vControlBack;
	private ImageView vControlShare;
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
        vControlShare = (ImageView) findViewById(R.id.vControlShare);
        vControlBack = (ImageView) findViewById(R.id.vControlBack);
    	mScoreSwitcher = (TextView) findViewById(R.id.scoreValue);

        // New Game button 
    	vControlRestart.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(DIALOG_RESTART);
				playAudio(SoundType.CLICK);
			}
		});

    	// Ranking button
    	vControlRanking.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(DIALOG_SHOW_SCORES);
				playAudio(SoundType.CLICK);
			}
		});
    	
    	// Share button
    	vControlShare.setOnClickListener(shareActionListener());

		//
		vControlBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.i("santi", aGame.getPanelGrid());
			}
		});

    	// Score 
		updateScore(0);

//        mAdView = (AdView)this.findViewById(R.id.adView);
//        if (mAdView != null) {
//	        AdRequest adRequest = new AdRequest();
////	        adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
////	        adRequest.addTestDevice("C2EEE67F6E791D37BAF4946F80533D8B");
//	        mAdView.loadAd(adRequest);
//        }
        
        try { 	
			startGame();
		} catch (Exception e) {
			Log.e(TAG, "onStart: Fallo", e);
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
	
	public boolean isAudioEnabled(){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	boolean audioPref = prefs.getBoolean(Consts.preferences.SOUND_ENABLED_PREFERENCE_KEY, true);
    	return audioPref;
    }
    
    private OnClickListener shareActionListener() {
    	return new OnClickListener() {
			public void onClick(View arg0) {
				AppsUtils.share(GameActivity.this);
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
			
			startGame();
		} catch (Exception e) {
			Log.e(TAG, "No deberia pasar nunca.", e);
		}
	}
	
	public ImageView findByCoord(int i, int j) {
		int id = getResources().getIdentifier("ball"+i+j, "id" , "com.pluk.fiveballs");
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
					case GLOBAL: rankingDialog.setGlobalScores(currentPosition, currentPage); break;
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
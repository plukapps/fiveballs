package com.pluk.fiveballs.widgets;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.pluk.fiveballs.model.Consts;
import com.pluk.fiveballs.persistence.GetRankingTask;
import com.pluk.fiveballs.persistence.GetRankingTask.GetRankingCallBack;
import com.pluk.fiveballs.persistence.PuntajeDB;
import com.pluk.fiveballs.persistence.PuntajeDB.Row;
import com.pluk.fiveballs.persistence.ScoreData;
import com.pluk.fiveballs.utils.AppsUtils;
import com.pluk.fiveballs.utils.SoundUtils;
import com.pluk.fiveballs.utils.SoundUtils.SoundType;
import com.pluk.fiveballs.R;
import com.pluk.fiveballs.view.TypefaceUtils;

public class RankingDialog extends Dialog implements View.OnClickListener, GetRankingCallBack {
	
	public static final String TAG = "RankingDialog";
	
	private Context context;
	
	private static final int pageSize = 10;
	private static int currentRanking = 0;
	private static String name = "";
	
	final int rankRankginColor = getContext().getResources().getColor(R.color.rankRanking);
	final int rankNickColor = getContext().getResources().getColor(R.color.rankNick);
	final int rankScoreColor = getContext().getResources().getColor(R.color.rankScore);
	final int rankDateColor = getContext().getResources().getColor(R.color.rankDate);
	final int rankRankginFocusColor = getContext().getResources().getColor(R.color.rankFocusRanking);
	final int rankNickFocusColor = getContext().getResources().getColor(R.color.rankFocusNick);
	final int rankScoreFocusColor = getContext().getResources().getColor(R.color.rankFocusScore);
	final int rankDateFocusColor = getContext().getResources().getColor(R.color.rankFocusDate);
	
	private View layout;
	private LayoutInflater mInflater;
	private ProgressBar vProgress;
	
	private int currentPage = 0;
	
	public enum RankingMode {
		LOCAL, GLOBAL, WEEKLY, COUNTRY
	}
	
	private RankingMode rankingMode = RankingMode.LOCAL;
	private Button vLocalScoresButton;
	private Button vGlobalScoresButton;
	private Button vWeekScoresButton;
	private final MediaPlayer mp;
	private Button vPrevPageButton;
	private Button vCloseButton;
	private Button vNextPageButton;

	private boolean showWeekly = false;

	private String mCountryCode = null;

	public RankingDialog(Context context, int theme, MediaPlayer mp) {
		super(context, theme);

		this.context = context;
		this.mp = mp;
		
		
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = mInflater.inflate(R.layout.ranking_dialog, null);
        
		vLocalScoresButton = (Button) layout.findViewById(R.id.rank_dialog_local_scores);
		vGlobalScoresButton = (Button) layout.findViewById(R.id.rank_dialog_global_scores);
		vWeekScoresButton = (Button) layout.findViewById(R.id.rank_dialog_week_scores);
		vPrevPageButton = (Button) layout.findViewById(R.id.rank_dialog_prev_page);
		vCloseButton = (Button) layout.findViewById(R.id.rank_dialog_close);
		vNextPageButton = (Button) layout.findViewById(R.id.rank_dialog_next_page);

		vLocalScoresButton.setOnClickListener(this);
		vGlobalScoresButton.setOnClickListener(this);
		vWeekScoresButton.setOnClickListener(this);
		vPrevPageButton.setOnClickListener(this);
		vCloseButton.setOnClickListener(this);
		vNextPageButton.setOnClickListener(this);
		
		vCloseButton.setVisibility(View.VISIBLE);
		
//		Typeface font = TypefaceUtils.circulat(context);
		
		TableLayout scoreTable = (TableLayout) layout.findViewById(R.id.scoreTable);
    	for (int i = 1; i <= 10; i++) {
			TableRow row = null;
			switch (i) {
				case 1: row = (TableRow) scoreTable.findViewById(R.id.scoreRow1); break;
				case 2: row = (TableRow) scoreTable.findViewById(R.id.scoreRow2); break;
				case 3: row = (TableRow) scoreTable.findViewById(R.id.scoreRow3); break;
				case 4: row = (TableRow) scoreTable.findViewById(R.id.scoreRow4); break;
				case 5: row = (TableRow) scoreTable.findViewById(R.id.scoreRow5); break;
				case 6: row = (TableRow) scoreTable.findViewById(R.id.scoreRow6); break;
				case 7: row = (TableRow) scoreTable.findViewById(R.id.scoreRow7); break;
				case 8: row = (TableRow) scoreTable.findViewById(R.id.scoreRow8); break;
				case 9: row = (TableRow) scoreTable.findViewById(R.id.scoreRow9); break;
				case 10:row = (TableRow) scoreTable.findViewById(R.id.scoreRow10); break;
			}
			
			// Setea el tipo de fuente para el score
			TextView scoreView = (TextView) row.getChildAt(3);
//			scoreView.setTypeface(font);
    	}
    	
    	vProgress = (ProgressBar) layout.findViewById(R.id.rank_dialog_progress);
    	vProgress.setVisibility(View.GONE);
    	
    	setContentView(layout);
	}
	
	public void setCurrentPosition(int position){
		this.setCurrentRanking(position);
	}
	
	public void setCurrentRanking(int currentRanking) {
		RankingDialog.currentRanking = currentRanking;
	}

	public int getCurrentRanking() {
		return currentRanking;
	}
	
	public RankingMode getRankingMode() {
		return rankingMode;
	}

	public static void setName(String name) {
		RankingDialog.name = name;
	}

	public static String getName() {
		return name;
	}
	
	public void setData(List<ScoreData> data) {
		
		TableLayout scoreTable = (TableLayout) findViewById(R.id.scoreTable);
		TextView textView = (TextView) findViewById(R.id.dialogContentEmpty);
		int size = data.size();
		if (size == 0) {
			scoreTable.setVisibility(View.INVISIBLE);
			textView.setVisibility(View.VISIBLE);
		} else {
			scoreTable.setVisibility(View.VISIBLE);
			textView.setVisibility(View.INVISIBLE);
			
			for (int i = 1; i <= 10; i++) {
				TableRow row = null;
				
				switch (i) {
					case 1: row = (TableRow) scoreTable.findViewById(R.id.scoreRow1); break;
					case 2: row = (TableRow) scoreTable.findViewById(R.id.scoreRow2); break;
					case 3: row = (TableRow) scoreTable.findViewById(R.id.scoreRow3); break;
					case 4: row = (TableRow) scoreTable.findViewById(R.id.scoreRow4); break;
					case 5: row = (TableRow) scoreTable.findViewById(R.id.scoreRow5); break;
					case 6: row = (TableRow) scoreTable.findViewById(R.id.scoreRow6); break;
					case 7: row = (TableRow) scoreTable.findViewById(R.id.scoreRow7); break;
					case 8: row = (TableRow) scoreTable.findViewById(R.id.scoreRow8); break;
					case 9: row = (TableRow) scoreTable.findViewById(R.id.scoreRow9); break;
					case 10:row = (TableRow) scoreTable.findViewById(R.id.scoreRow10); break;
				}
				
				if (size < i) {
					row.setVisibility(View.INVISIBLE);
					row.setOnClickListener(null);
				} else {
					ScoreData scoreData = data.get(i-1);
					
					if (RankingMode.GLOBAL == rankingMode) {
						row.setTag(scoreData.getCountryCode());
						row.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								String countryCode = (String) v.getTag();
								onRowClick(countryCode);
							}
						});	
					} else {
						row.setOnClickListener(null);
					}
					
					row.setVisibility(View.VISIBLE);
					
					String rank  = String.valueOf(scoreData.getRank());
					Drawable image = scoreData.getImage();
					String nick = scoreData.getName();
					String score = String.valueOf(scoreData.getScore());
					String date = scoreData.getDateString();
					
					TextView rankView = (TextView) row.getChildAt(0);
					LinearLayout flagContainer = (LinearLayout)row.getChildAt(1);
					ImageView flagView = (ImageView) flagContainer.getChildAt(0); 
					TextView nickView = (TextView) row.getChildAt(2);
					TextView scoreView = (TextView) row.getChildAt(3);
					TextView dateView = (TextView) row.getChildAt(4);
					
					// Setea el rank
					rankView.setText(rank);
					
					// Setea la bandera
//					if (image != null) {
//						flagView.setImageDrawable(image);
//						flagView.setVisibility(View.VISIBLE);
//						flagContainer.setVisibility(View.VISIBLE);
//						row.setTag(scoreData.getCountryCode());
//					} else {
//						flagView.setVisibility(View.INVISIBLE);
//						flagContainer.setVisibility(View.INVISIBLE);
//					}
					
					// Setea el nick
					nickView.setText(nick);
					
					// Setea el score
					scoreView.setText(score);
					
					// Setea la fecha
//					if (date == null) {
//						dateView.setVisibility(View.VISIBLE);
//						dateView.setText("--/--/----");
//					} else {
//						dateView.setVisibility(View.VISIBLE);
//						dateView.setText(date);
//					}
					
					/*
					 * Chequeo que sea la posicion actual
					 */
					int ranking = Integer.parseInt(rank);
					if(ranking != 0 && ranking == currentRanking ){
						rankView.setTextColor(rankRankginFocusColor);
						nickView.setTextColor(rankNickFocusColor);
						scoreView.setTextColor(rankScoreFocusColor);
						dateView.setTextColor(rankDateFocusColor);
					} else {
						rankView.setTextColor(rankRankginColor);
						nickView.setTextColor(rankNickColor);
						scoreView.setTextColor(rankScoreColor);
						dateView.setTextColor(rankDateColor);
					}
				}
			}
		}
	}
	
	public void setLocalScores() {
		setRankingMode(RankingMode.LOCAL);
		vPrevPageButton.setVisibility(View.INVISIBLE);
		vNextPageButton.setVisibility(View.INVISIBLE);
		loadLocalScores();
	}
	
	public void setGlobalScores() {
		setGlobalScores(0, 0);
	}

	public void setGlobalScores(int position, int currentPage) {
		currentRanking = position;
		setRankingMode(RankingMode.GLOBAL);
		vPrevPageButton.setVisibility(View.VISIBLE);
		vNextPageButton.setVisibility(View.VISIBLE);
		this.currentPage = currentPage;
		showWeekly = false;
		mCountryCode = null;
		getRankingFromServer(currentPage, showWeekly, mCountryCode);
	}
	
	public void setWeeklyScores() {
		setRankingMode(RankingMode.WEEKLY);
		vPrevPageButton.setVisibility(View.VISIBLE);
		vNextPageButton.setVisibility(View.VISIBLE);
		showWeekly  = true;
		currentPage = 0;
		currentRanking = 0;
		mCountryCode = null;
		getRankingFromServer(currentPage, showWeekly, mCountryCode);
	}

	public void onClick(View v) {

		switch (v.getId()) {
			
			case R.id.rank_dialog_local_scores:
				if (RankingMode.LOCAL == rankingMode) {
					return;
				}
				playAudio(SoundType.CLICK);
				setRankingMode(RankingMode.LOCAL);
				vPrevPageButton.setVisibility(View.INVISIBLE);
				vNextPageButton.setVisibility(View.INVISIBLE);
				loadLocalScores();
				currentRanking = 0;
				break;
				
			case R.id.rank_dialog_global_scores:
				if (RankingMode.GLOBAL == rankingMode) {
					return;
				}
				playAudio(SoundType.CLICK);
				setRankingMode(RankingMode.GLOBAL);
				vPrevPageButton.setVisibility(View.VISIBLE);
				vNextPageButton.setVisibility(View.VISIBLE);
				currentPage = 0;
				showWeekly = false;
				mCountryCode = null;
				currentRanking = 0;
				getRankingFromServer(currentPage, showWeekly, mCountryCode);
				break;
				
			case R.id.rank_dialog_week_scores:
				if (RankingMode.WEEKLY == rankingMode) {
					return; 
				}
				playAudio(SoundType.CLICK);
				setRankingMode(RankingMode.WEEKLY);
				vPrevPageButton.setVisibility(View.VISIBLE);
				vNextPageButton.setVisibility(View.VISIBLE);
				showWeekly  = true;
				currentPage = 0;
				mCountryCode = null;
				currentRanking = 0;
				getRankingFromServer(currentPage, showWeekly, mCountryCode);
				break;
			
			case R.id.rank_dialog_prev_page:
				playAudio(SoundType.CLICK);
				if(currentPage == 0){
					AppsUtils.showToast(context, R.string.fb_ranking_first_page_msg);
					return;
				}
				currentPage--;
				getRankingFromServer(currentPage, showWeekly, mCountryCode);
				break;
				
			case R.id.rank_dialog_next_page:
				playAudio(SoundType.CLICK);
				currentPage++;
				getRankingFromServer(currentPage, showWeekly, mCountryCode);
				break;
				
			case R.id.rank_dialog_close:
				playAudio(SoundType.CLICK);
				dismiss();
				break;
				
			default:
				break;
		}
	}
	
	public int getCurrentPage() {
		return currentPage;
	}

	private void onRowClick(String countryCode) {
		if (RankingMode.GLOBAL == rankingMode) {
			mCountryCode  = countryCode;
			setRankingMode(RankingMode.COUNTRY);
			currentPage = 0;
			currentRanking = 0;
			showWeekly = false;
			getRankingFromServer(currentPage, showWeekly, mCountryCode);
		}
	}

	public void setRankingMode(RankingMode mode) {
		rankingMode = mode;
		vLocalScoresButton.setSelected(mode == RankingMode.LOCAL);
		vGlobalScoresButton.setSelected(mode == RankingMode.GLOBAL);
		vWeekScoresButton.setSelected(mode == RankingMode.WEEKLY);
	}

	private void loadLocalScores() {
		PuntajeDB persistencia = new PuntajeDB(context.getApplicationContext());
		List<Row> lscores = persistencia.fetchAllRows();
		persistencia.close();
		ArrayList<ScoreData> localScores = new ArrayList<ScoreData>();
		int i = 0;
		for (Row row : lscores) {
			i++;
			ScoreData sd = new ScoreData(i, row.name, row.score);
			localScores.add(sd);
		}
		setData(localScores);
	}
	
	public void playAudio(SoundType soundType) {
		if (isAudioEnabled()){
			SoundUtils.play(context, mp, soundType);
		}
	}
	
	public boolean isAudioEnabled(){
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    	boolean audioPref = prefs.getBoolean(Consts.preferences.SOUND_ENABLED_PREFERENCE_KEY, true);
    	return audioPref;
    }
	
	private void getRankingFromServer(int page, boolean weekly, String countryCode) {
		vPrevPageButton.setVisibility(View.VISIBLE);
		vNextPageButton.setVisibility(View.VISIBLE);
		TableLayout scoreTable = (TableLayout) findViewById(R.id.scoreTable);
		TextView emptyMsg = (TextView) findViewById(R.id.dialogContentEmpty);
		scoreTable.setVisibility(View.INVISIBLE);
		emptyMsg.setVisibility(View.INVISIBLE);
		GetRankingTask task = new GetRankingTask(context, page, countryCode, weekly, vProgress);
		task.setCallback(this);
		task.execute();
	}

	public void onSuccess(List<ScoreData> scores) {
		if (scores == null || scores.isEmpty()) {
			AppsUtils.showToast(context, R.string.fb_ranking_empty);
			return;
		} 
		int j = 1 + currentPage * pageSize;
		for (ScoreData sd : scores) {
			sd.setRank(j);
			j++;
		}
		setData(scores);	
		
		TableLayout scoreTable = (TableLayout) findViewById(R.id.scoreTable);
		scoreTable.setVisibility(View.VISIBLE);
		
		if (scores.size() < 10) {
			vNextPageButton.setVisibility(View.INVISIBLE);
		}
	}
}

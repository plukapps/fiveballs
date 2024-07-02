package com.pluk.fiveballs.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pluk.fiveballs.model.Consts;
import com.pluk.fiveballs.persistence.APIClient;
import com.pluk.fiveballs.persistence.PuntajeDB;
import com.pluk.fiveballs.persistence.PuntajeDB.Row;
import com.pluk.fiveballs.persistence.ScoreData;
import com.pluk.fiveballs.persistence.ScoreService;
import com.pluk.fiveballs.utils.AppsUtils;
import com.pluk.fiveballs.utils.SecurityManager;
import com.pluk.fiveballs.utils.SoundUtils;
import com.pluk.fiveballs.utils.SoundUtils.SoundType;
import com.pluk.fiveballs.R;

import retrofit2.Call;
import retrofit2.Callback;

public class RankingDialog extends Dialog implements View.OnClickListener {
	
	public static final String TAG = "RankingDialog";
	
	private Context context;
	
	private static final int pageSize = 10;
	private static int currentRanking = 0;
	private static String name = "";
	private static String RANK_MAX_VAL = "999999000000000000";
	private static String RANK_MIN_VAL = "000000000000000000";

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
	private String pageValueNext = RANK_MAX_VAL;
	private String pageValuePrev = RANK_MIN_VAL;

	public enum RankingMode {
		LOCAL, GLOBAL, WEEKLY, COUNTRY
	}

	public enum PageDir {
		PAGEUP, PAGEDOWN;

		@NonNull
		@Override
		public String toString() {
			switch(this) {
				case PAGEUP: return "pageup";
				case PAGEDOWN:
				default:
					return "pagedown";
			}
		}
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
//		LinearLayout scoreTable = (LinearLayout) layout.findViewById(R.id.scoreTable);
//    	for (int i = 1; i <= 10; i++) {
//			RelativeLayout row = (RelativeLayout) scoreTable.getChildAt(i-1);
//
//			// Setea el tipo de fuente para el score
//			TextView scoreView = (TextView) row.getChildAt(3);
////			scoreView.setTypeface(font);
//    	}

		TextView emptyMsg = layout.findViewById(R.id.dialogContentEmpty);
		emptyMsg.setVisibility(View.GONE);

    	vProgress = layout.findViewById(R.id.rank_dialog_progress);
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
		
		LinearLayout scoreTable = (LinearLayout) findViewById(R.id.scoreTable);
		TextView textView = (TextView) findViewById(R.id.dialogContentEmpty);
		int size = data.size();
		if (size == 0) {
			scoreTable.setVisibility(View.INVISIBLE);
			textView.setVisibility(View.VISIBLE);
		} else {
			scoreTable.setVisibility(View.VISIBLE);
			textView.setVisibility(View.INVISIBLE);
			
			for (int i = 1; i <= 10; i++) {
				RelativeLayout row = null;

				row = ((RelativeLayout) scoreTable.getChildAt(i-1));

				RelativeLayout innerRow = (RelativeLayout) row.getChildAt(0);

				if (size < i) {
					row.setVisibility(View.INVISIBLE);
					row.setOnClickListener(null);
				} else {
					ScoreData scoreData = data.get(i-1);
					
					if (RankingMode.GLOBAL == rankingMode) {
//						row.setTag(scoreData.getCountryCode());
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
//					Drawable image = scoreData.getImage();
					String nick = scoreData.getName();
					String score = String.valueOf(scoreData.getScore());
					String date = scoreData.getDateString();
					
					TextView rankView = (TextView) innerRow.getChildAt(0);
					LinearLayout flagContainer = (LinearLayout)innerRow.getChildAt(1);
					ImageView flagView = (ImageView) flagContainer.getChildAt(0);
					TextView nickView = (TextView) innerRow.getChildAt(2);
					TextView scoreView = (TextView) innerRow.getChildAt(3);
					TextView dateView = (TextView) innerRow.getChildAt(4);
					
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
		currentPage = 0;
		currentRanking = 0;
		pageValueNext = RANK_MAX_VAL;
		pageValuePrev = RANK_MIN_VAL;
		setGlobalScores(0, 0);
	}

	private void setGlobalScores(int position, int currentPage) {
		setRankingMode(RankingMode.GLOBAL);
		showWeekly = false;
		vPrevPageButton.setVisibility(View.VISIBLE);
		vNextPageButton.setVisibility(View.VISIBLE);
		mCountryCode = null;

		currentRanking = position;
		this.currentPage = currentPage;
		getRankingFromServer(pageValueNext,PageDir.PAGEDOWN, showWeekly, mCountryCode);
	}
	
	public void setWeeklyScores() {
		setRankingMode(RankingMode.WEEKLY);
		showWeekly  = true;
		vPrevPageButton.setVisibility(View.VISIBLE);
		vNextPageButton.setVisibility(View.VISIBLE);
		mCountryCode = null;

		currentPage = 0;
		currentRanking = 0;
		pageValueNext = RANK_MAX_VAL;
		pageValuePrev = RANK_MIN_VAL;

		getRankingFromServer(pageValueNext,PageDir.PAGEDOWN, showWeekly, mCountryCode);
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
				pageValueNext = RANK_MAX_VAL;
				pageValuePrev = RANK_MIN_VAL;
				showWeekly = false;
				mCountryCode = null;
				currentRanking = 0;
				getRankingFromServer(pageValueNext, PageDir.PAGEDOWN, showWeekly, mCountryCode);
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
				pageValueNext = RANK_MAX_VAL;
				pageValuePrev = RANK_MIN_VAL;
				mCountryCode = null;
				currentRanking = 0;
				getRankingFromServer(pageValueNext, PageDir.PAGEDOWN, showWeekly, mCountryCode);
				break;
			
			case R.id.rank_dialog_prev_page:
				playAudio(SoundType.CLICK);
				if (currentPage == 0) {
					AppsUtils.showToast(context, R.string.fb_ranking_first_page_msg);
					return;
				} else {
					currentPage--;
				}
				getRankingFromServer(pageValuePrev, PageDir.PAGEUP, showWeekly, mCountryCode);
				break;
				
			case R.id.rank_dialog_next_page:
				playAudio(SoundType.CLICK);
				currentPage++;
				getRankingFromServer(pageValueNext, PageDir.PAGEDOWN, showWeekly, mCountryCode);
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
//		if (RankingMode.GLOBAL == rankingMode) {
//			mCountryCode  = countryCode;
//			setRankingMode(RankingMode.COUNTRY);
//			currentPage = 0;
//			currentRanking = 0;
//			showWeekly = false;
//			getRankingFromServer(currentPage, showWeekly, mCountryCode);
//		}
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
	
	private void getRankingFromServer(String value, PageDir pagedir, boolean weekly, String countryCode) {
		vPrevPageButton.setVisibility(View.VISIBLE);
		vNextPageButton.setVisibility(View.VISIBLE);
		LinearLayout scoreTable = (LinearLayout) findViewById(R.id.scoreTable);
		TextView emptyMsg = (TextView) findViewById(R.id.dialogContentEmpty);
		scoreTable.setVisibility(View.INVISIBLE);
		emptyMsg.setVisibility(View.INVISIBLE);

		ScoreService service = APIClient.getClient().create(ScoreService.class);
		String valueStr = String.valueOf(value);
		String dir = pagedir.toString();
		Call<List<ScoreData>> call = !weekly ? service.topScores(valueStr, dir) : service.weeklyScores(valueStr, dir);

		vProgress.setVisibility(View.VISIBLE);
		call.enqueue(new Callback<List<ScoreData>>() {
			@Override
			public void onResponse(Call<List<ScoreData>> call, retrofit2.Response<List<ScoreData>> response) {
				vProgress.setVisibility(View.GONE);
				if (response.isSuccessful()) {
					onSuccess(response.body(), weekly);
				} else {
					onFailure();
				}
			}

			@Override
			public void onFailure(Call<List<ScoreData>> call, Throwable t) {
				vProgress.setVisibility(View.VISIBLE);
				onFailure();
			}

			private void onFailure() {
			}

		});

	}

	public void onSuccess(List<ScoreData> scores, boolean weekly) {
		if (scores == null || scores.isEmpty()) {
			AppsUtils.showToast(context, R.string.fb_ranking_empty);
			return;
		}

		Collections.sort(scores, (o1, o2) -> o2.filter.compareTo(o1.filter));

		List<ScoreData> pageList = null;

		ScoreData first = scores.get(0);
		ScoreData last = scores.get(scores.size() - 1);

		if (scores.size() > pageSize) {
			pageList = scores.subList(0, scores.size() - 1);
		} else {
			pageList = scores;
		}

		int j = 1 + (currentPage) * pageSize;
		for (int i = 0; i < pageList.size(); i++) {
			ScoreData sd = pageList.get(i);
			sd.setRank(j);
			j++;
		}

		pageValuePrev = first.filter;
		pageValueNext = last.filter;


		setData(pageList);
		
		LinearLayout scoreTable = (LinearLayout) findViewById(R.id.scoreTable);
		scoreTable.setVisibility(View.VISIBLE);
		
		if (scores.size() <= pageSize) {
			vNextPageButton.setVisibility(View.INVISIBLE);
		}
	}
}

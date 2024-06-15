package com.pluk.fiveballs.persistence;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.pluk.fiveballs.R;

/**
 * AsyncTask<Param, Progress, Result>
 * 
 * @author marcel
 *
 */
public class GetRankingTask extends AsyncTask<Void, Void, List<ScoreData>> {
	
	public interface GetRankingCallBack {
		public void onSuccess(List<ScoreData> scores);
	}
	
	private static final String TAG = "GetTopScoresTask";
	
    private Context context;
	private boolean hayResultados = false;
	private int pageNumber;
	private String countryCode;

	private final ProgressBar progress;
	private GetRankingCallBack listener;

	private final boolean weekly;
	
	public GetRankingTask(Context context, int page, String countryCode, boolean weekly, ProgressBar progress) {
		this.context = context;
		this.pageNumber = page;
		this.countryCode = countryCode;
		this.weekly = weekly;
		this.progress = progress;
	}

	@Override
	protected void onPreExecute() {
		progress.setVisibility(View.VISIBLE);
    }

	@Override
    protected List<ScoreData> doInBackground(Void... unused) {
    	/*
		 * Obtengo los resultados de la web
		 */
		List<ScoreData> resultados;
		try {
			resultados = getGlobalTopScores();
			hayResultados  = true;
			
			// Se descarga las imagenes 
			for (ScoreData scoreData : resultados) {
				String countryCode = scoreData.getCountryCode();
				int identifier = 0;
				if (countryCode != null) {
					countryCode = countryCode.toLowerCase();
					identifier = context.getResources().getIdentifier(countryCode, "drawable", "com.pluk.fiveballs");
				}
				if (identifier == 0) { // Not found
					Log.i(TAG, "Not found flag to " + countryCode + " flag");
					identifier = context.getResources().getIdentifier("noflag", "drawable", "com.pluk.fiveballs");
				}
				Drawable drawable = context.getResources().getDrawable(identifier);
				scoreData.setImage(drawable);
			}
			
			return resultados;
			
		} catch (IOException e) {
			hayResultados = false;
		}
		return null;
    }
	
	private List<ScoreData> getGlobalTopScores() throws IOException {
//		ScoreSubmitter ss = new ScoreSubmitter();
//		if (countryCode == null) {
//			return ss.getTopScores(pageNumber, weekly);
//		} else {
//			return ss.getTopScores(pageNumber, countryCode, weekly);
//		}

		return new ParsedScoreDataSet().getScores();
	}

	@Override
	protected void onPostExecute(List<ScoreData> l) {
		progress.setVisibility(View.GONE);
        if(hayResultados) {
        	
        	if (listener != null) {
        		listener.onSuccess(l);
        	}
//        	if (countryCode == null) {
//        		activity.showDialog(GameActivity.DIALOG_GLOBAL_SCORES);
//        	} else {
//        		activity.showDialog(GameActivity.DIALOG_GLOBAL_SCORES_BY_COUNTRY);
//        	}
        } else {
        	Toast tostada = Toast.makeText(context, context.getString(R.string.fb_ranking_get_scores_error), Toast.LENGTH_SHORT);
			tostada.setGravity(Gravity.BOTTOM, 5, 2);
			tostada.show();
        }
        
    }

	public GetRankingCallBack getListener() {
		return listener;
	}

	public void setCallback(GetRankingCallBack listener) {
		this.listener = listener;
	}
	 
  }
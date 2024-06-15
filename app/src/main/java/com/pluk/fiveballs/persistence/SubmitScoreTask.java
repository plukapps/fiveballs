package com.pluk.fiveballs.persistence;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.pluk.fiveballs.utils.AppsUtils;
import com.pluk.fiveballs.R;

/**
 * AsyncTask<Param, Progress, Result>
 * 
 * @author marcel
 *
 */
public class SubmitScoreTask  extends AsyncTask<Void, Void, String> {
	
    private static final String TAG = SubmitScoreTask.class.getSimpleName();
	private ProgressDialog dialog;
	private ScoreData score;
	private String name;
	private long scoreId;
	private boolean share = false;
	private Context context;
	private final PuntajeDB db;
	
	public interface SubmitScoreCallback {
		void shareScore(String name, int score, int position, int page);
		void showGlobalsScores(int position, String name, int page);
	}
	
	private SubmitScoreCallback submitScoreCallback;
	
	public void setSubmitScoreCallback(SubmitScoreCallback submitScoreCallback) {
		this.submitScoreCallback = submitScoreCallback;
	}

	public SubmitScoreTask(Context context, ScoreData s, long scoreId, boolean share, PuntajeDB db) {
		this.context = context;
		this.score = s;
		this.db = db;
		this.name = s.getName();
		this.scoreId = scoreId;
		this.share = share;
	}

	@Override
	protected void onPreExecute() {
		dialog = new ProgressDialog(context);
		dialog.setMessage(context.getString(R.string.fb_common_loading_message));
		dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();
    }

	@Override
    protected String doInBackground(Void... unused) {
//		ScoreSubmitter ss = new ScoreSubmitter();
//		SubmitData res = ss.submitScore(score.getName() , String.valueOf(score.getScore()));
//
//		if(res != null) {
//			Log.i(TAG, "Submiting score online ... El resultado es: " + res.getRes() + " idExterno = " + res.getRemoteScoreId() );
//			Log.i(TAG, "Updating local db with external Id");
//
//			db.updateSubmitId(scoreId, res.getRemoteScoreId());
//
//			return res.getRes();
//		}
//		else {
			return null;
//		}
		
    }
    
	
	@Override
	protected void onPostExecute(String res) {
        dialog.dismiss();
        
        /*
		 * Según el resultado del submit, le mando un dialogo de error o no
		 */
		if(res != null){
			int position = Integer.parseInt(res);
			int page;
			if(position == 0){
				page = 0;
			}
			else page = (position - 1 )/ 10;
			Log.i(TAG, "La pagina calculada es " + page);
			
			if (submitScoreCallback != null) {
				if(share){
					submitScoreCallback.shareScore(getName(), score.getScore(), position, page);
				} else {
					submitScoreCallback.showGlobalsScores(position, getName(), page);
				}
				
			}
		}
		else {
			AppsUtils.showToast(context, R.string.fb_upload_score_error);
		}
    }

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}
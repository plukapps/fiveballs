package com.pluk.fiveballs.widgets;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pluk.fiveballs.persistence.APIClient;
import com.pluk.fiveballs.persistence.PuntajeDB;
import com.pluk.fiveballs.persistence.ScoreData;
import com.pluk.fiveballs.persistence.ScoreService;
import com.pluk.fiveballs.persistence.SubmitScoreTask;
import com.pluk.fiveballs.persistence.SubmitScoreTask.SubmitScoreCallback;
import com.pluk.fiveballs.utils.AppsUtils;
import com.pluk.fiveballs.utils.InputUtils;
import com.pluk.fiveballs.utils.SoundUtils.SoundType;
import com.pluk.fiveballs.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class HighScoreDialog extends Dialog implements View.OnClickListener, SubmitScoreCallback {
	
	public static final String TAG = "HighScoreDialog";
	
	private Context context;
	private final MediaPlayer mp;
	
	private LayoutInflater mInflater;

	private View layout;
	private Button vOkButton;
//	private Button vUploadButton;
//	private Button vShareButton;
	private EditText vNickname;
	private TextView vHighScoreMsg;

	private int mScore;

	private final PuntajeDB db;
	
	public interface OnSumbitHighScoreListener {
		public void onSumbitLocalScore();
		public void shareScore(String name, int score, int position, int page);
		public void showGlobalsScores(int position, String name, int page);
	}
	
	private OnSumbitHighScoreListener onSumbitScoreListener;
	
	public void setOnSumbitScoreListener(OnSumbitHighScoreListener onSumbitScoreListener) {
		this.onSumbitScoreListener = onSumbitScoreListener;
	}

	public HighScoreDialog(Context context, int theme, MediaPlayer mp, PuntajeDB db) {
		super(context, theme);

		this.context = context;
		this.mp = mp;
		this.db = db;
		
    	mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = mInflater.inflate(R.layout.high_score_dialog, null);

        vOkButton = (Button) layout.findViewById(R.id.high_score_btn_ok);
		vOkButton.setOnClickListener(this);
		
//		vUploadButton = (Button) layout.findViewById(R.id.high_score_btn_submit);
//		vUploadButton.setOnClickListener(this);
//
//		vShareButton = (Button) layout.findViewById(R.id.high_score_btn_share);
//		vShareButton.setOnClickListener(this);
		
		vNickname = (EditText) layout.findViewById(R.id.high_score_nick);
		vHighScoreMsg = (TextView) layout.findViewById(R.id.high_score_msg);
		
		setContentView(layout);
	}
	
	public void setNickName(String nickname) {
		vNickname.setText(nickname);
	}
	
	public void setHighScoreMsg(String msg) {
		vHighScoreMsg.setText(msg);
	}
	
	public void setScore(int score) {
		mScore = score;
	}
	
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.high_score_btn_ok:
				AppsUtils.playAudio(context, mp, SoundType.CLICK);
				
				if (db.isTopScore(mScore)) {
					//Valido que el nombre ingresado no sea vacio
					if (InputUtils.isEmpty(vNickname)) {
						AppsUtils.showToast(context, R.string.fb_upload_score_empty_name);
					} else {
						String name = InputUtils.getText(vNickname);
						String score = String.valueOf(mScore);
						saveLocal(name, score);
						saveGlobal(name, mScore);
						if (onSumbitScoreListener != null) {
							onSumbitScoreListener.onSumbitLocalScore();
						}
						dismiss();
					}
				} else {
					dismiss();
				}
				break;
				
//			case R.id.high_score_btn_submit:
//				AppsUtils.playAudio(context, mp, SoundType.CLICK);
//				if (db.isTopScore(mScore)) {
//
//					if (InputUtils.isEmpty(vNickname)) {
//						AppsUtils.showToast(context, R.string.fb_upload_score_empty_name);
//					} else {
//						dismiss();
//
//						String nick = InputUtils.getText(vNickname);
//						nick = nick.replaceAll("\n", " ").trim();
//
//						// Actualizo los scores locales y me quedo con el id del registro local
//						long idScoreEntry = db.createRow(nick, String.valueOf(mScore));
//						// Inicio una tarea en el background para hacer el submit los resultados desde la web
//
//
//					}
//				}

//				break;
//			case R.id.high_score_btn_share:
//				AppsUtils.playAudio(context, mp, SoundType.CLICK);
//				if (db.isTopScore(mScore)) {
//
//					if (InputUtils.isEmpty(vNickname)) {
//						AppsUtils.showToast(context, R.string.fb_upload_score_empty_name);
//					} else {
//						dismiss();
//
//						String nick = InputUtils.getText(vNickname);
//						nick = nick.replaceAll("\n", " ").trim();
//
//						// Actualizo los scores locales
//						long idScoreEntry = db.createRow(nick, String.valueOf(mScore));
//
//						// Inicio una tarea en el background para hacer el submit los resultados desde la web
//						SubmitScoreTask task = new SubmitScoreTask(context, new ScoreData(nick, mScore, null), idScoreEntry, true, db);
//						task.setSubmitScoreCallback(this);
//						task.execute();
//					}
//				}
				
//				break;
			default:
				break;
		}
	}

	private void saveGlobal(String nick, int score) {
		ScoreData scoreData = new ScoreData(nick, mScore, null);
//		SubmitScoreTask task = new SubmitScoreTask(context, scoreData, idScoreEntry, false, db);
//		task.setSubmitScoreCallback(this);
//		task.execute();

		ScoreService service = APIClient.getClient().create(ScoreService.class);
		Call<String> call = service.newScore(nick, score);
		call.enqueue(new Callback<String>() {
			@Override
			public void onResponse(Call<String> call, retrofit2.Response<String> response) {
				if (response.isSuccessful()) {
//					showGlobalsScores(0, nick, 0);
				} else {
					onFailure();
				}
			}

			@Override
			public void onFailure(Call<String> call, Throwable t) {
				onFailure();
			}

			private void onFailure() {
			}

		});

	}

	private void saveLocal(String name, String score) {
		db.createRow(name, score);
	}

	public void shareScore(String name, int score, int position, int page) {
		if (onSumbitScoreListener != null) {
			onSumbitScoreListener.shareScore(name, score, position, page);
		}
	}

	public void showGlobalsScores(int position, String name, int page) {
		if (onSumbitScoreListener != null) {
			onSumbitScoreListener.showGlobalsScores(position, name, page);
		}
	}
	
}

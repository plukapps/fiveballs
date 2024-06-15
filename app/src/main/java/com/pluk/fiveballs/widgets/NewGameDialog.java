package com.pluk.fiveballs.widgets;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.pluk.fiveballs.utils.AppsUtils;
import com.pluk.fiveballs.utils.SoundUtils.SoundType;
import com.pluk.fiveballs.R;

public class NewGameDialog extends Dialog implements View.OnClickListener {
	
	public static final String TAG = "NewGameDialog";
	
	private Context context;
	private final MediaPlayer mp;
	
	private LayoutInflater mInflater;

	private View layout;
	private Button vCancelButton;
	private Button vOkButton;
	
	public interface OnNewGameListener {
		public void onNewGame();
	}
	
	private OnNewGameListener onNewGameListener;

	public void setOnNewGameListener(OnNewGameListener onNewGameListener) {
		this.onNewGameListener = onNewGameListener;
	}

	public NewGameDialog(Context context, int theme, MediaPlayer mp) {
		super(context, theme);

		this.context = context;
		this.mp = mp;
		
    	mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = mInflater.inflate(R.layout.new_game_dialog, null);
        
		vCancelButton = (Button) layout.findViewById(R.id.new_game_btn_cancel);
		vOkButton = (Button) layout.findViewById(R.id.new_game_btn_ok);

		vCancelButton.setOnClickListener(this);
		vOkButton.setOnClickListener(this);
		
    	setContentView(layout);
	}
	
	public void onClick(View v) {

		switch (v.getId()) {
		
			case R.id.new_game_btn_cancel:
				AppsUtils.playAudio(context, mp, SoundType.CLICK);
				dismiss();
				break;
			case R.id.new_game_btn_ok:
				AppsUtils.playAudio(context, mp, SoundType.CLICK);
				dismiss();
				if (onNewGameListener != null) {
					onNewGameListener.onNewGame();
				}
				break;
			default:
				break;
		}
	}
	
}

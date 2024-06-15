package com.pluk.fiveballs.widgets;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pluk.fiveballs.utils.AppsUtils;
import com.pluk.fiveballs.utils.SoundUtils.SoundType;
import com.pluk.fiveballs.R;

public class HelpGameDialog extends Dialog implements View.OnClickListener {
	
	public static final String TAG = "HelpGameDialog";
	
	private Context context;
	private final MediaPlayer mp;
	
	private LayoutInflater mInflater;

	private View layout;
	private Button vOkButton;

	private TextView vMsg;

	private TextView vTitle;
	
	public HelpGameDialog(Context context, int theme, MediaPlayer mp) {
		super(context, theme);

		this.context = context;
		this.mp = mp;
		
    	mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = mInflater.inflate(R.layout.help_dialog, null);
        
        vTitle = (TextView) layout.findViewById(R.id.help_game_dialog_title);
        vTitle.setText(R.string.fb_help);
        
		vOkButton = (Button) layout.findViewById(R.id.help_game_btn_ok);
		vOkButton.setOnClickListener(this);
		
		vMsg = (TextView) layout.findViewById(R.id.help_game_dialog_msg);
		vMsg.setText(R.string.fb_help_paragraph1);
		vMsg.append("\n");
		vMsg.append(context.getString(R.string.fb_help_paragraph2));
		vMsg.append("\n");
		vMsg.append(context.getString(R.string.fb_help_paragraph3));
		vMsg.append("\n");
		vMsg.append(context.getString(R.string.fb_help_paragraph4));

		setContentView(layout);
	}
	
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.help_game_btn_ok:
				AppsUtils.playAudio(context, mp, SoundType.CLICK);
				dismiss();
				break;
			default:
				break;
		}
	}
	
}

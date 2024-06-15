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

public class AboutDialog extends Dialog implements View.OnClickListener {
	
	public static final String TAG = "AboutDialog";
	
	private Context context;
	private final MediaPlayer mp;
	
	private LayoutInflater mInflater;

	private View layout;
	private Button vShareButton;
	private Button vWebButton;
	private Button vOkButton;
	
	public interface AboutDialogListener {
		void onGoToWeb();
		void onShare();
	}
	
	private AboutDialogListener aboutDialogListener;
	
	public AboutDialog(Context context, int theme, MediaPlayer mp) {
		super(context, theme);

		this.context = context;
		this.mp = mp;
		
    	mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = mInflater.inflate(R.layout.about_dialog, null);
        
        vShareButton = (Button) layout.findViewById(R.id.about_btn_share);
        vWebButton = (Button) layout.findViewById(R.id.about_btn_web);
		vOkButton = (Button) layout.findViewById(R.id.about_btn_ok);
		
		vShareButton.setOnClickListener(this);
		vWebButton.setOnClickListener(this);
		vOkButton.setOnClickListener(this);
		
		setContentView(layout);
	}
	
	public void onClick(View v) {
		AppsUtils.playAudio(context, mp, SoundType.CLICK);
		switch (v.getId()) {
			case R.id.about_btn_share:
				if (aboutDialogListener != null) {
					aboutDialogListener.onShare();
				}
				break;
			case R.id.about_btn_web:
				if (aboutDialogListener != null) {
					aboutDialogListener.onGoToWeb();
				}
				break;
			case R.id.about_btn_ok:
				break;
			default:
				break;
		}
		dismiss();
	}

	public void setAboutDialogListener(AboutDialogListener aboutDialogListener) {
		this.aboutDialogListener = aboutDialogListener;
	}
	
	
	
}

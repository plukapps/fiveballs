package com.pluk.fiveballs.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.pluk.fiveballs.R;

public class SoundUtils {
	
	public enum SoundType {
		CLICK, BUBBLES, BUBBLE_SHORT;
	}

	private static final String TAG = "FiveBalls";
	
	public static void play(Context context, MediaPlayer mp, SoundType sound) {
		int audioId = R.raw.click;
		switch (sound) {
			case CLICK:
				audioId = R.raw.click;
				break;
			case BUBBLES: 
				audioId = R.raw.bubbles2;
				break;
			case BUBBLE_SHORT: 
				audioId = R.raw.bubble2;
				break;
		}
		
		try {
			mp = MediaPlayer.create(context, audioId);
			if (mp != null) {
				mp.start();
			} else {
				Log.i(TAG, "The audioPlayer reference was lost, disabling audio");
			}
   		} catch (IllegalStateException e) {
			Log.e(TAG, "Error en el start del audioPlayer. Illegal State");
			e.printStackTrace();
   		} catch (Exception e) {
   			Log.e(TAG, "An error ocurred when play aduio");
			e.printStackTrace();
   		}
			
	}

}

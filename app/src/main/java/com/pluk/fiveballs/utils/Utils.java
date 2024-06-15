package com.pluk.fiveballs.utils;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.pluk.fiveballs.R;
import com.pluk.fiveballs.view.GameActivity;

public class Utils {
	
	/**
	 * Desplegar un mensaje genérico
	 * @param activity
	 * @param message
	 */
	public static void displayMessage(final GameActivity activity, String title, String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(title != null ? title : "");
		builder.setIcon(activity.getResources().getDrawable(R.drawable.iconcolors));
		builder.setMessage(message);
		builder.setPositiveButton(R.string.fb_common_button_accept, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				activity.playAudio(SoundUtils.SoundType.CLICK);
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**
	 * Despliega un mensaje de error y cierra la aplicación
	 * @param activity
	 * @param message
	 */
	public static void displayError(final GameActivity activity, String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Se ha producido un error");
		builder.setIcon(activity.getResources().getDrawable(R.drawable.iconcolors));
		builder.setMessage(message);
		builder.setPositiveButton(R.string.fb_common_button_accept, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				activity.playAudio(SoundUtils.SoundType.CLICK);
				activity.finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
}

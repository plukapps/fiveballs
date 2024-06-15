package com.pluk.fiveballs.utils;

import android.widget.EditText;
import android.widget.Spinner;

public class InputUtils {

	/**
	 * Devuelve true si el input tiene algun texto
	 * @param editText
	 * @return
	 */
	public static boolean isNotEmpty(EditText editText){
		if (editText != null) {
			String text = editText.getText().toString();
			if (text != null && text.length() > 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Devuelve true si el input tiene algun texto
	 * @param editText
	 * @return
	 */
	public static boolean isEmpty(EditText editText){
		if (editText != null) {
			String text = getText(editText);
			if (text != null && text.length() > 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Devuleve el text que contiene en editext. 
	 * @param editText
	 * @return
	 */
	public static String getText(EditText editText) {
		if (editText == null) {
			return null;
		}
		String text = editText.getText().toString();
		if (text == null) {
			return null;
		}
		text = text.trim();
		if (text.length() == 0) {
			return null;
		}
		return text;
	}
	
	/**
	 * Setea el text en el editext.
	 * @param editText
	 * @param text
	 */
	public static void setText(EditText editText, String text) {
		if (editText == null) {
			return;
		}
		text = text.trim();
		editText.setText(text);
	}
	
	/**
	 * Limpia los datos del editText
	 * @param editText
	 */
	public static void clear(EditText editText) {
		if (editText != null) {
			editText.setText("");
		}
	}

	public static long getSelectedIndex(Spinner spinner) {
		return spinner.getSelectedItemId();
	}

	public static boolean isNumber(EditText editText) {
		try {
			Double.valueOf(getText(editText));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static Double getNumber(EditText editText) {
		try {
			return Double.valueOf(getText(editText));
		} catch (Exception e) {
			return null;
		}
	}

}

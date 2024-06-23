package com.pluk.fiveballs.persistence;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.SerializedName;


public class ScoreData {
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
	
	private String name;
	private int score;
	private Date fecha;
	@SerializedName("country")
	private String countryCode;
//	private String countryName;
	private Drawable image;
	private int rank;
	
	public ScoreData(String name, int score, Date fecha) {
		this.name = name;
		this.score = score;
		this.fecha = fecha;
	}
	
//	public ScoreData(String name, int score, Date fecha, String countryCode, String countryName) {
//		this.name = name;
//		this.score = score;
//		this.fecha = fecha;
//		this.countryCode = countryCode;
//		this.countryName = countryName;
//	}
	
	public ScoreData(int rank, String name, int score) {
		this.rank = rank;
		this.name = name;
		this.score = score;
	}
	
//	public ScoreData(int rank, Drawable image, String name, int score, Date fecha) {
////		this.rank = rank;
////		this.image = image;
//		this.name = name;
//		this.score = score;
//		this.fecha = fecha;
//	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Date getFecha() {
		return fecha;
	}
	public String getDateString() {
		if (fecha != null) {
			return sdf.format(fecha); 
		}
		return null;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public Drawable getImage() {
		return image;
	}
	public void setImage(Drawable image) {
		this.image = image;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
//	public String getCountryName() {
//		return countryName;
//	}
	
	
}

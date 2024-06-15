package com.pluk.fiveballs.persistence;

import java.util.LinkedList;
import java.util.List;

public class ParsedScoreDataSet {
	private String extractedString = null;
	private int extractedInt = 0;
	
	private List<ScoreData> scores = new LinkedList<ScoreData>();

	public String getExtractedString() {
		return extractedString;
	}
	public void setExtractedString(String extractedString) {
		this.extractedString = extractedString;
	}

	public int getExtractedInt() {
		return extractedInt;
	}
	public void setExtractedInt(int extractedInt) {
		this.extractedInt = extractedInt;
	}
	
	public String toString(){
		return "ExtractedString = " + this.extractedString
				+ "nExtractedInt = " + this.extractedInt;
	}
	public void setScores(List<ScoreData> scores) {
		this.scores = scores;
	}
	public List<ScoreData> getScores() {
		return scores;
	}

}
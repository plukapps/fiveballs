package com.pluk.fiveballs.persistence;

public class SubmitData {
	
	private String position;
	private long remoteScoreId;
	public String getRes() {
		return position;
	}
	public void setRes(String res) {
		this.position = res;
	}
	public long getRemoteScoreId() {
		return remoteScoreId;
	}
	public void setRemoteScoreId(long remoteScoreId) {
		this.remoteScoreId = remoteScoreId;
	}
	public SubmitData(String res, long remoteScoreId) {
		super();
		this.position = res;
		this.remoteScoreId = remoteScoreId;
	}
	
	
}

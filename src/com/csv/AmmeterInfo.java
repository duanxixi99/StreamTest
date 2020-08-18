package com.csv;

public class AmmeterInfo {
	private String ammeterID;// 电表ID
	private Long ammeterReading;// 电表值
	private String readTime;// 时间

	public String getAmmeterID() {
		return ammeterID;
	}

	public void setAmmeterID(String ammeterID) {
		this.ammeterID = ammeterID;
	}

	public Long getAmmeterReading() {
		return ammeterReading;
	}

	public void setAmmeterReading(Long ammeterReading) {
		this.ammeterReading = ammeterReading;
	}

	public String getReadTime() {
		return readTime;
	}

	public void setReadTime(String readTime) {
		this.readTime = readTime;
	}

}

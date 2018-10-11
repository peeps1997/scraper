package com.peoplesoft.scraper.model;

public class WeeklyInsight {
	public String sunday;
	public String monday;
	public String tuesday;
	public String wednesday;
	public String thursday;
	public String friday;
	public String saturday;
	public String sundayCode;
	public String mondayCode;
	public String tuesdayCode;
	public String wednesdayCode;
	public String thursdayCode;
	public String fridayCode;
	public String saturdayCode;

	public WeeklyInsight() {

	}

	public WeeklyInsight(String sunday, String monday, String tuesday, String wednesday, String thursday, String friday,
			String saturday, String sundayCode, String mondayCode, String tuesdayCode, String wednesdayCode,
			String thursdayCode, String fridayCode, String saturdayCode) {
		super();
		this.sunday = sunday;
		this.monday = monday;
		this.tuesday = tuesday;
		this.wednesday = wednesday;
		this.thursday = thursday;
		this.friday = friday;
		this.saturday = saturday;
		this.sundayCode = sundayCode;
		this.mondayCode = mondayCode;
		this.tuesdayCode = tuesdayCode;
		this.wednesdayCode = wednesdayCode;
		this.thursdayCode = thursdayCode;
		this.fridayCode = fridayCode;
		this.saturdayCode = saturdayCode;
	}

}

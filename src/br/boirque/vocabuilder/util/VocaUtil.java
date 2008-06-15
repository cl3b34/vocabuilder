package br.boirque.vocabuilder.util;

import java.util.Calendar;
import java.util.Date;

public class VocaUtil {

	/**
	 * Default constructor
	 */
	public VocaUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public String getStudyTimeAsString(long studyTime) {
		// 1s = 1000 ms
		studyTime = studyTime / 1000; // study time in seconds
		if (studyTime < 60) {
			return studyTime + " sec";
		} else {
			long seconds = studyTime % 60; //remaining seconds
			studyTime = studyTime / 60; // study time in minutes
			if (studyTime < 60) {
				return studyTime + "min " + seconds + "sec";
			} else {
				long minutes = studyTime % 60; //remaining minutes
				studyTime = studyTime / 60; // study time in hours
				if (studyTime < 24) {
					return studyTime + "hr " + formatWithLeftZero(minutes) + "min";
				} else {
					long hours = studyTime % 24; //remaining hours
					studyTime = studyTime / 24; // study time in days
					return studyTime + " day " + hours + "hr " + formatWithLeftZero(minutes) + "min";
				}
			}
		}
	}
	
	/*
	 * Format minutes from 0 to 9 with a left zero 
	 * ( 01, 02, 03, etc)
	 */
	private String formatWithLeftZero(long unitOfTime){
		if(unitOfTime < 10) {
			return "0" + unitOfTime;
		}
		return unitOfTime + "";
	}

	/**
	 * Format a long into a string representing a date. ex:
	 * 13/7/1974 08:15 
	 */
	Calendar calendar = Calendar.getInstance();
	public String getLastTimeViewedAsString(long lastTimeViewed) {
		calendar.setTime(new Date(lastTimeViewed));
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int date = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		String lastTimeViewedFormatted = date + "/" + month + "/" + year 
			+ " " + hour + ":" + formatWithLeftZero(minute);
		return lastTimeViewedFormatted;
	}

}

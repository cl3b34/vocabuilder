package br.boirque.vocabuilder.util;

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
			studyTime = studyTime / 60; // study time in minutes
			if (studyTime < 60) {
				return studyTime + " min";
			} else {
				studyTime = studyTime / 60; // study time in hours
				if (studyTime < 24) {
					return studyTime + " hr";
				} else {
					studyTime = studyTime / 24; // study time in days
					return studyTime + " day";
				}
			}
		}
	}


}

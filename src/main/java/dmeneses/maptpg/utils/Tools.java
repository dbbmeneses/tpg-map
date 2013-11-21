package dmeneses.maptpg.utils;

import java.util.Date;
import java.util.Random;

public class Tools {
	final static private double METERS_PER_MINUTE=55.0;
	
	public static TimeDiff getWalkTime(double meters) {
		double minutes = meters / METERS_PER_MINUTE;
		return new TimeDiff((long) (minutes*60.0*1000.0));
	}
	
	public static long getWalkTimeMs(double meters) {
		double minutes = meters / METERS_PER_MINUTE;
		return (long) (minutes*60.0*1000.0);
	}
	
	public static String getRandomHexString(int len) {
        final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
        Random rand = new Random();
        StringBuilder strBuilder = new StringBuilder(len);

        for(int i = 0; i < len; i++) {
                strBuilder.append(letters.charAt(rand.nextInt(letters.length())));
        }

        return strBuilder.toString();
	}
	
	/**
	 * A little expensive, use with care
	 * @param d
	 * @param t
	 * @return
	 */
	public static Date addDateTimeDiff(Date d, TimeDiff t) {
		return new Date(d.getTime() + t.getMs());
	}
}

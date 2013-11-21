package dmeneses.maptpg.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeDiff implements Comparable<TimeDiff> {
	private long ms;
	private long[] values = new long[TimeField.values().length];
	private double[] totalValues = new double[TimeField.values().length];

	@Override
	public String toString() {
		boolean first = false;
		StringBuilder sb = new StringBuilder();
		TimeField []fields = { TimeField.DAY, TimeField.HOUR, TimeField.MINUTE, 
				TimeField.SECOND, TimeField.MILLISECOND};
		
		for(TimeField tf : fields) {
			if(!first && this.get(tf) == 0) {
				continue;
			}

			first = true;

			sb.append(this.get(tf));
			switch(tf) {
			case DAY:
				sb.append("d");
				break;
			case HOUR:
				sb.append("h");
				break;
			case MINUTE:
				sb.append("m");
				break;
			case SECOND:
				sb.append("s");
				break;
			case MILLISECOND:
				sb.append("ms");
				break;
			}
		}
		return sb.toString();
	}

	public long get(TimeField field) {
		return values[field.ordinal()];
	}

	public double getTotal(TimeField field) {
		return totalValues[field.ordinal()];
	}

	public TimeDiff(long ms) {
		setValues(ms);
	}

	public static TimeDiff MAX() {
		return new TimeDiff(Long.MAX_VALUE);
	}

	public static TimeDiff MIN() {
		return new TimeDiff(0);
	}

	public long getMs() {
		return ms;
	}

	public static TimeDiff add(TimeDiff t1, TimeDiff t2) {
		return new TimeDiff(t1.getMs() + t2.getMs());
	}

	private void setValues(long ms) {
		this.ms = ms;
		final int ONE_DAY = 1000 * 60 * 60 * 24;
		final int ONE_HOUR = ONE_DAY / 24;
		final int ONE_MINUTE = ONE_HOUR / 60;
		final int ONE_SECOND = ONE_MINUTE / 60;

		totalValues[TimeField.DAY.ordinal()] = ms / ((double) ONE_DAY);
		totalValues[TimeField.HOUR.ordinal()] = ms / ((double) ONE_HOUR);
		totalValues[TimeField.MINUTE.ordinal()] = ms / ((double) ONE_MINUTE);
		totalValues[TimeField.SECOND.ordinal()] = ms / ((double) ONE_SECOND);
		totalValues[TimeField.MILLISECOND.ordinal()] = ms;

		long d = ms / ONE_DAY;
		ms %= ONE_DAY;

		long h = ms / ONE_HOUR;
		ms %= ONE_HOUR;

		long m = ms / ONE_MINUTE;
		ms %= ONE_MINUTE;

		long s = ms / ONE_SECOND;
		ms = ms % ONE_SECOND;

		values[TimeField.DAY.ordinal()] = d;
		values[TimeField.HOUR.ordinal()] = h;
		values[TimeField.MINUTE.ordinal()] = m;
		values[TimeField.SECOND.ordinal()] = s;
		values[TimeField.MILLISECOND.ordinal()] = ms;
	}

	public TimeDiff(Date start, Date end) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		cal.setTime(start);

		long t1 = cal.getTimeInMillis();
		cal.setTime(end);

		long diff = Math.abs(cal.getTimeInMillis() - t1);
		setValues(diff);

	}

	public static void printDiffs(long[] diffs) {
		System.out.printf("Days:         %3d\n", diffs[0]);
		System.out.printf("Hours:        %3d\n", diffs[1]);
		System.out.printf("Minutes:      %3d\n", diffs[2]);
		System.out.printf("Seconds:      %3d\n", diffs[3]);
		System.out.printf("Milliseconds: %3d\n", diffs[4]);
	}

	public static enum TimeField {
		DAY,
		HOUR,
		MINUTE,
		SECOND,
		MILLISECOND;
	}

	@Override
	public int compareTo(TimeDiff other) {
		long diff = this.getMs() - other.getMs();
		int dif = 0;

		if(diff < Integer.MIN_VALUE) {
			dif = Integer.MIN_VALUE;
		}
		else if(diff > Integer.MAX_VALUE) {
			dif = Integer.MAX_VALUE;
		}
		else {
			dif = (int) diff;
		}

		return dif;
	}
}
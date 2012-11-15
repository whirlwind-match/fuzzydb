package org.fuzzydb.util.web;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.util.Assert;

import org.fuzzydb.core.query.Result;
import org.fuzzydb.dto.attributes.Score;
import org.fuzzydb.util.DateTimeUtils;

public abstract class TagLibFunctions {

	/**
	 * Returns a string of the form rgb(100,200,0) to be used
	 * to indicate the percentage.  100% is green, 0% is red.
	 * HSV model:
	 *   Hue 0 -> 120 (red -> green)
	 *   Sat -> fixed @ 85
	 *   Brightness -> fixed @ 90
	 *
	 *   @param value 0 - 1.0f
	 */
	public static String toCssRGBColor(float value) {
	    return toCssRGBColor(value, 0.85f);
	}

	/**
	 * Returns a string of the form rgb(100,200,0) to be used
	 * to indicate the percentage.  100% is green, 0% is red.
	 * HSV model:
	 *   Hue 0 -> 120 (red -> green)
	 *   Sat -> fixed @ 85
	 *   Brightness -> fixed @ 90
	 *
	 *   @param value 0 - 1.0f
	 *   @param saturation 0 - 1.0f - lower for paler
	 */
	public static String toCssRGBColor(float value, float saturation) {
	    int intVal = Math.round(value * 100);
	    Color color = new Color(Color.HSBtoRGB(intVal * 1.2f / 360f, saturation, 0.90f));
	    return "rgb(" + color.getRed() + "," + color.getGreen() + ","+ color.getBlue() +")";
	}

	/**
	 * Returns a string of the form rgb(100,200,0) to be used
	 * to indicate the percentage.  100% is green, 0% is red.
	 * HSV model:
	 *   Hue 0 -> 120 (red -> green)
	 *   Sat -> fixed @ 85
	 *   Brightness -> fixed @ 90
	 *
	 *   @param value 0 - 1.0f
	 */
	public static String toCssRGBColor(Score score) {
		return toCssRGBColor(score.total());
	}

	public static String toCssRGBColor(Score score, float sat) {
		return toCssRGBColor(score.total(), sat);
	}

	public static int toPercent(Score score) {
		return Math.round(score.total() * 100f);
	}

	public static <T> List<T> toList(Iterator<T> items) {
		Assert.notNull(items);

		List<T> list = new LinkedList<T>();
		for (Iterator<T> iterator = items; iterator.hasNext();) {
			T item = iterator.next();
			list.add(item);
		}
		return list;
	}


	public static <T> Float forwardsScore(Result<T> result, String matcher) {
		return result.getScore().getForwardsScore(matcher);
	}

	public static <T> Float reverseScore(Result<T> result, String matcher) {
		return result.getScore().getReverseScore(matcher);
	}

	public static <T> Float forwardsTotal(Result<T> result) {
		return result.getScore().forwardsTotal();
	}

	public static <T> Float reverseTotal(Result<T> result) {
		return result.getScore().reverseTotal();
	}

	/**
	 * A little help for things like Arrays
	 * @param object
	 * @return formatted string representation
	 */
	public static String toString(Object object) {
		if (object == null) {
			return "";
		}

		if (object instanceof Object[]) {
			Object[] array = (Object[]) object;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; i++) {
				sb.append(array[i].toString());
				if (i < array.length - 1) {
					sb.append(", ");
				}
			}
			return sb.toString();
		}
		else if (object instanceof float[]) {
			float[] array = (float[]) object;
			StringBuilder sb = new StringBuilder();
			Formatter f = new Formatter(sb);
			for (int i = 0; i < array.length; i++) {
				f.format("%.1f", array[i]);
				if (i < array.length - 1) {
					sb.append(", ");
				}
			}
			 String result = sb.toString();
			 f.close();
			 return result;
		}
		else if (object instanceof Float) {
			return String.format("%.0f", object);
		}

		return object.toString();
	}

	public static String dateAsMinsHoursDaysEtcAgo(Date date) {

        float hours = DateTimeUtils.getMillisDiff(System.currentTimeMillis(), date.getTime(), Calendar.HOUR);


        if ( hours < 1f ) {
            int mins = (int) (hours * 60f);
            return (mins < 2) ? "< 2 mins ago" : mins + " mins ago";
        }
        if ( hours < 2f ) {
            return "1 hour ago";
        }
        if ( hours < 48f ) {
            return (int)hours + " hours ago";
        }
        if ( hours < 24f * 7f) {
            return (int)(hours / 24f) + " days ago";
        }
        if ( hours < 24f * 7f * 2f) {
            return "a week ago";
        }
        if ( hours < 24f * 30f) {
            return (int)(hours / 24f / 7f) + " weeks ago";
        }
        if ( hours < 24f * 30f * 2f) {
            return "a month ago";
        }
        if ( hours < 24f * 365f) {
            return (int)(hours / 24f / 30f) + " months ago";
        }
        return "> 1 year ago";

	}
}

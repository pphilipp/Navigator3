package com.innotech.imap_taxi.network;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import com.innotech.imap_taxi.helpers.ContextHelper;
import com.innotech.imap_taxi3.R;

/**
 * Created with IntelliJ IDEA. User: Kvest Date: 15.09.13 Time: 11:28 To change
 * this template use File | Settings | File Templates.
 */
public class Utils {
	private Utils() {
	}

	public static int byteToInt(byte[] val) {
		int result = 0;
		long bitValue = 1;
		for (int i = 0; i < val.length; ++i) {
			for (int j = 1; j <= 128; j *= 2) {
				if ((val[i] & j) == j) {
					result |= bitValue;
				}

				bitValue *= 2;
			}
		}

		return result;
	}

	public static byte boolToByte(boolean val) {
		if (val) {
			return 0x01;
		} else {
			return 0x00;
		}
	}

	public static byte[] int16ToByte(int val) {
		byte t;
		int temp;
		byte[] result;
		result = new byte[2];
		Integer i = new Integer(val);
		t = i.byteValue();
		result[0] = t;
		temp = i.intValue();
		temp = temp >> 8;
		i = new Integer(temp);
		t = i.byteValue();
		result[1] = t;
		System.gc();
		return result;

	}

	public static long byteToLong(byte[] val) {
		long result = 0;
		long bitValue = 1;
		for (int i = 0; i < val.length; ++i) {
			for (int j = 1; j <= 128; j *= 2) {
				if ((val[i] & j) == j) {
					result |= bitValue;
				}

				bitValue = bitValue << 1;
			}
		}

		return result;
	}

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static float byteToDecimal(byte[] val) {
		float k, temp;
		String tempStr;
		int exp;
		tempStr = "";
		temp = 0;

		for (int i = 0; i < val.length; i++) {
			String substr;
			substr = Integer.toBinaryString(val[i]);
			if (substr.length() > 8) {
				substr = substr.substring(substr.length() - 8);
			}
			int delta = 8 - substr.length();
			for (int j = 0; j < delta; j++) {
				substr = '0' + substr;
			}
			tempStr = substr + tempStr;
		}
		k = 1;

		for (int i = tempStr.length() - 1; i >= 32; i--) {
			if (tempStr.charAt(i) == '1') {
				temp += k;
			}
			k *= 2;
		}

		k = 1;
		exp = 0;
		for (int i = 15; i >= 8; i--) {
			if (tempStr.charAt(i) == '1') {
				exp += k;
			}
			k *= 2;
		}
		if (exp > 28) {
			exp = 28;
		}
		k = 1;
		for (int i = 1; i <= exp; i++) {
			k *= 10;
		}
		temp = temp / k;

		if (tempStr.charAt(0) == '1') {
			temp = -temp;
		}
		return temp;
	}

	public static byte[] dateToByte(long val) {
		val += 62135596800000l;
		val = val * 10000l;

		return longToByte(val);
	}

	public static long byteToDate(byte[] val) {
		long l;
		l = byteToLong(val);

		l = l / 10000;

		l -= 62135596800000l;
		return l;
	}

	public static String dateToString(long ticks) {
		Date d = new Date(ticks);
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));// getDefault());
		calendar.setTime(d);
		String temp = "";
		if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
			temp += "0" + Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
		} else {
			temp += Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
		}
		if ((calendar.get(Calendar.MONTH) + 1) < 10) {
			temp += ".0" + Integer.toString(calendar.get(Calendar.MONTH) + 1);
		} else {
			temp += "." + Integer.toString(calendar.get(Calendar.MONTH) + 1);
		}
		temp += "." + Integer.toString(calendar.get(Calendar.YEAR));
		temp += " " + Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		if (calendar.get(Calendar.MINUTE) < 10) {
			temp += ":0" + Integer.toString(calendar.get(Calendar.MINUTE));
		} else {
			temp += ":" + Integer.toString(calendar.get(Calendar.MINUTE));
		}
		return temp;
	}

	public static long stringToDate(String value) {
		if (!isInvalidDate(value)) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date(0));

			calendar.set(Calendar.DAY_OF_MONTH,
					Integer.parseInt(value.substring(0, 2)));
			calendar.set(Calendar.MONTH,
					Integer.parseInt(value.substring(3, 5)));
			calendar.set(Calendar.YEAR,
					Integer.parseInt(value.substring(6, 10)));

			return calendar.getTime().getTime();
		} else {
			return -1;
		}
	}

	// 01.01.2015
	public static String dateToDateString(long ticks) {
		Date d = new Date(ticks);
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));// getDefault());
		calendar.setTime(d);
		String temp = "";
		if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
			temp += "0" + Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
		} else {
			temp += Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
		}
		if ((calendar.get(Calendar.MONTH) + 1) < 10) {
			temp += ".0" + Integer.toString(calendar.get(Calendar.MONTH) + 1);
		} else {
			temp += "." + Integer.toString(calendar.get(Calendar.MONTH) + 1);
		}
		temp += "." + Integer.toString(calendar.get(Calendar.YEAR));
		return temp;
	}

	// 01.01.15
	public static String getDateString(long input) {
		Date date = new Date(input);
		Calendar cal = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
		sdf.setCalendar(cal);
		cal.setTime(date);
		return sdf.format(date);

	}

	/**
	 * Formats time as it in server DB
	 * 
	 * @param input
	 *            time as long value
	 * @return date in yyyy-MM-dd HH:mm:ss format
	 */
	public static String getDateStringServerFormat(long input) {
		Date date = new Date(input);
		Calendar cal = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setCalendar(cal);
		cal.setTime(date);
		return sdf.format(date);

	}

	public static String dateToTimeString(long ticks) {
		Date d = new Date(ticks);
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));// getDefault());
		calendar.setTime(d);
		String temp = "";
		temp += Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		if (calendar.get(Calendar.MINUTE) < 10) {
			temp += ":0" + Integer.toString(calendar.get(Calendar.MINUTE));
		} else {
			temp += ":" + Integer.toString(calendar.get(Calendar.MINUTE));
		}
		return temp;
	}

	public static byte[] int32ToByte(int val) {
		/*
		 * byte t; int temp; byte[] result; result = new byte[4]; Integer i =
		 * new Integer(val); t = i.byteValue(); result[0] = t; temp =
		 * i.intValue(); temp = temp >> 8; i = new Integer(temp); t =
		 * i.byteValue(); result[1] = t; temp = i.intValue(); temp = temp >> 8;
		 * i = new Integer(temp); t = i.byteValue(); result[2] = t; temp =
		 * i.intValue(); temp = temp >> 8; i = new Integer(temp); t =
		 * i.byteValue(); result[3] = t; System.gc(); return result;
		 */
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		bb.putInt(val);
		return bb.array();
	}

	public static byte[] longToByte(long val) {
		byte[] result, temp;
		result = new byte[8];
		int intVal;

		intVal = (int) val;
		temp = int32ToByte(intVal);
		for (int i = 0; i < 4; i++) {
			result[i] = temp[i];
		}

		val = val >> 32;
		intVal = (int) val;
		temp = int32ToByte(intVal);
		for (int i = 0; i < 4; i++) {
			result[i + 4] = temp[i];
		}

		return result;
	}

	public static float byteToFloat(byte[] val) {
		int bits = byteToInt(val);
		return Float.intBitsToFloat(bits);
	}

	public static byte[] floatToByte(float val) {
		int bits = Float.floatToIntBits(val);
		return int32ToByte(bits);
	}

	public static boolean isInvalidDate(String value) {
		value = value.trim();
		return ((value.length() != 10) || (value.charAt(2) != '.')
				|| (value.charAt(5) != '.')
				|| (!Character.isDigit(value.charAt(0)))
				|| (!Character.isDigit(value.charAt(1)))
				|| (!Character.isDigit(value.charAt(3)))
				|| (!Character.isDigit(value.charAt(4)))
				|| (!Character.isDigit(value.charAt(6)))
				|| (!Character.isDigit(value.charAt(7)))
				|| (!Character.isDigit(value.charAt(8))) || (!Character
					.isDigit(value.charAt(9))));
	}

	public static SpannableString intToSpannableStringKm(int val) {
		Log.d("philipp", "val = " + String.valueOf(val));

		int left = val / 1000;
		int right = val % 1000;

		while(right > 9){right/=10;}

		String dist = (left > 100) ? ">100"
				+ ContextHelper.getInstance().getCurrentContext()
				.getString(R.string.km)
				: String.format("%d,%d", left, right)
				+ ContextHelper.getInstance().getCurrentContext()
				.getString(R.string.km);

		Log.d("philipp", " String format" + dist);

		SpannableString ss = null;
		ss = new SpannableString(dist);
		ss.setSpan(new RelativeSizeSpan(0.4f), dist.length() - 2,
				dist.length(), 0);
		ss.setSpan(new ForegroundColorSpan(ContextHelper.getInstance()
						.getCurrentContext().getResources()
						.getColor(R.color.greyText)),
				dist.length() - 2, dist.length(), 0);

		return ss;
	}

}

package com.petterroea.mcmapgen;

import java.awt.Color;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {
	public static boolean isInt(String str)
	{
		try{
			int i = Integer.parseInt(str);
		} catch(Exception e) {
			return false;
		}
		return true;
	}

	public static byte toByte(int i) 
	{	
		return (byte)((int)i+Byte.MIN_VALUE);
	}

	public static String getMD5(byte[] data) 
	{
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		md5.update(data);
		byte[] digested = md5.digest();
		StringBuilder sb = new StringBuilder(digested.length*2);
		for (byte b : digested) {
		    sb.append("0123456789ABCDEF".charAt((b & 0xF0) >> 4));
		    sb.append("0123456789ABCDEF".charAt((b & 0x0F)));
		}
		return sb.toString();
	}

	public static byte[] subArray(byte[] data, long byteIndex, long byteStopFetch) {
		byte[] newArray = new byte[(int) (byteStopFetch-byteIndex)];
		for(long i = byteIndex; i < byteStopFetch; i++)
		{
			newArray[(int) (i-byteIndex)] = data[(int) i];
		}
		return newArray;
	}
	/**
	 * To eliminate messy code
	 * @param amount the time in milliseconds(1/1000th of a second)
	 */
	public static void sleep(long amount)
	{
		try {
			Thread.sleep(amount);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static String byteHex(byte b) {
		 final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		 char[] hexChars = new char[2];
		 int v;
		 v = b & 0xFF;
		 hexChars[0] = hexArray[v >>> 4];
		 hexChars[1] = hexArray[v & 0x0F];
		 return new String(hexChars);
	}
	public static int avColor(int rgb) {
		Color c = new Color(rgb);
		int col = 0;
		col += c.getRed();
		col += c.getGreen();
		col += c.getBlue();
		return col/3;
	}
}

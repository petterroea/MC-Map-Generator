package com.petterroea.mcmapgen;

import java.awt.Color;
import java.io.IOException;
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
	public static int uByteToSignedInt(byte b)
	{
		boolean isNegative = b<(byte)0;
		byte masked = (byte) (b & 0x7F);
		int i = (int)masked;
		if(isNegative) i += 128;
		return i;
	}
	public static String getTimeString(long seconds)
	{
		String str = "";
		int days = (int) (seconds/86400L);
		int rest = (int) (seconds%86400L);
		int hours = rest/3600;
		rest = rest%3600;
		int minutes = rest / 60;
		int secondsLeft = rest % 60;
		str = str + (int)days + "d, " + (int)hours + "h, " + (int)minutes + "m, " + (int)secondsLeft + "s";
 		return str;
	}
	public static byte toByte(int i) 
	{	
		return (byte)((int)i+Byte.MIN_VALUE);
	}
	public static String getInput()
	{
		try {
			return McMapGen.br.readLine();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public static int getIntFromInput(String query)
	{
		String str = "";
		while(str.equals("")||!isInt(str))
		{
			System.out.println(query);
			str = getInput();
		}
		return Integer.parseInt(str);
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
	public static int Max(int a, int b)
	{
		if(a>b) return a;
		return b;
	}
	public static int Min(int a, int b)
	{
		if(a<b) return a;
		return b;
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
	/*
	 * Stolen from http://www.minecraftwiki.net/wiki/Chunk_format
	 */
	public static byte Nibble4(byte[] arr, int index)
	{ 
		return (byte) (index%2 == 0 ? arr[index/2]&0x0F : (arr[index/2]>>4)&0x0F); 
	}
	//Converts byte to part of a byte. (Yo dawg...)
	public static byte toNibble4(byte b, int index, byte in)
	{
		//First, divide the byte into two nibbles.
		byte first = (byte) (b&0x0F);
		byte last = (byte) ((b>>4)&0x0F);
		//Choose which part we change
		if(index%2==0) { first = (byte) (in&0x0F); } else { last = (byte) (in&0x0F); }
		//Put the byte together again
		return (byte) (first + (last << 4));
	}
	public static boolean yn() {
		String in = "";
		while(!in.equalsIgnoreCase("y")&&!in.equalsIgnoreCase("n"))
		{
			in = getInput();
		}
		if(in.equalsIgnoreCase("y")) return true;
		return false;
	}

	public static boolean isFloat(String sc) {
		try {
			float f = Float.parseFloat(sc);
		} catch(Exception e) {
			return false;
		}
		return true;
	}
}

package com.petterroea.gwg;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
/**
 * GWG - Geographic world geometry - File format made for storing terrain heights.
 * @author petterroea
 *
 */
public abstract class GwgFile {
	public static final byte STORAGE_BYTE = (byte)0;
	public static final byte STORAGE_SHORT = (byte)1;
	public static final byte STORAGE_INT = (byte)2;
	public static final byte STORAGE_LONG = (byte)3;
	public abstract void save(DataOutputStream os) throws IOException;
	public void save(File f) throws IOException
	{
		save(new DataOutputStream(new FileOutputStream(f)));
	}
	public static GwgFile load(DataInputStream dis) throws IOException
	{
		DataInputStream is = new DataInputStream(new GZIPInputStream(dis));
		long w = is.readLong();
		long h = is.readLong();
		byte format = is.readByte();
		if(format==STORAGE_BYTE)
		{
			GwgByteFile f = new GwgByteFile(w, h);
			long precentageMark = (w*h)/50L;
			for(long l = 0; l < w*h; l++)
			{
				f.set(l, is.readByte());
				if(l%precentageMark==0&&l!=0)
				{
					double precentage = (double)l/(double)(w*h);
					precentage = precentage * 100.0D;
					System.out.println("Read " + l + " bytes, " + precentage + "% done.");
				}
			}
			is.close();
			return f;
		}
		else if(format==STORAGE_SHORT)
		{
			GwgShortFile f = new GwgShortFile(w, h);
			for(long i = 0; i < w*h; i++)
			{
				f.set(i, is.readShort());
			}
			is.close();
			return f;
		}
		else if(format==STORAGE_INT)
		{
			GwgIntFile f = new GwgIntFile(w, h);
			for(long i = 0; i < w*h; i++)
			{
				f.set(i, is.readInt());
			}
			is.close();
			return f;
		}
		else if(format==STORAGE_LONG)
		{
			GwgLongFile f = new GwgLongFile(w, h);
			for(long i = 0; i < w*h; i++)
			{
				f.set(i, is.readLong());
			}
			is.close();
			return f;
		}
		return null;
	}
	public static GwgFile load(File file) throws IOException
	{
		return load(new DataInputStream(new FileInputStream(file)));
	}
}

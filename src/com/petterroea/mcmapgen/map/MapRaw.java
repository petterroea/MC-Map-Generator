package com.petterroea.mcmapgen.map;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

import com.petterroea.mcmapgen.Util;
import com.petterroea.util.CommandLineUtils;
import com.petterroea.util.PropertiesFile;

public class MapRaw extends Map {
	private byte[][] rawByteMap;
	int byteSize = 8;
	public MapRaw(File file, PropertiesFile properties)
	{
		if(!properties.containsKey("rawMapWidth")) properties.setInt("rawMapWidth", CommandLineUtils.promptInt("rawMapWidth", 0, Integer.MAX_VALUE));
		if(!properties.containsKey("rawMapHeight")) properties.setInt("rawMapHeight", CommandLineUtils.promptInt("rawMapHeight", 0, Integer.MAX_VALUE));
		w = properties.getInt("rawMapWidth");
		h = properties.getInt("rawMapHeight");
		if(!properties.containsKey("rawByteSize"))
		{
			int in = 0;
			while(true)
			{
				in = CommandLineUtils.promptInt("rawByteSize", 8, 32);
				if(in%8==0) break;
				System.out.println("Sorry, the number has to be either 8, 16, or 32(byte, short, int)");
			}
			properties.setInt("rawByteSize", in);
		}
		byteSize = properties.getInt("rawByteSize");
		rawByteMap = new byte[w][h];
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(file));
			for(int y = 0; y < h; y++)
			{
				for(int x = 0; x < w; x++)
				{
					if(byteSize == 8) rawByteMap[x][y] = dis.readByte();
					if(byteSize == 16) rawByteMap[x][y] = Util.toByte(dis.readShort()/256);
					if(byteSize == 32) rawByteMap[x][y] = Util.toByte(dis.readInt()/16777216);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public MapRaw(File file) {
		w = CommandLineUtils.getIntFromInput("What is the width of the map?");
		h = CommandLineUtils.getIntFromInput("What is the height of the map?");
		int in = 0;
		while(true)
		{
			in = CommandLineUtils.promptInt("rawByteSize", 8, 32);
			if(in%8==0) break;
			System.out.println("Sorry, the number has to be either 8, 16, or 32(byte, short, int)");
		}
		byteSize = in;
		rawByteMap = new byte[w][h];
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(file));
			for(int y = 0; y < h; y++)
			{
				for(int x = 0; x < w; x++)
				{
					if(byteSize == 8) rawByteMap[x][y] = dis.readByte();
					if(byteSize == 16) rawByteMap[x][y] = Util.toByte(dis.readShort()/256);
					if(byteSize == 32) rawByteMap[x][y] = Util.toByte(dis.readInt()/16777216);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public int get(int x, int y) {
		if(x<0||x>w||y<0||y>h) throw new RuntimeException("Coordinates outside of bounds");// TODO Auto-generated method stub
		return Util.uByteToSignedInt(rawByteMap[x][y]);
	}

	@Override
	public void set(int x, int y, int value) {
		if(x<0||x>w||y<0||y>h) throw new RuntimeException("Coordinates outside of bounds");// TODO Auto-generated method stub
		if(value<0||value>255) throw new RuntimeException("Value must be in the range 0-255");
		rawByteMap[x][y] = Util.toByte(value);
	}

}

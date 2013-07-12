package com.petterroea.mcmapgen.map;

import java.io.File;
import java.io.IOException;

import com.petterroea.gwg.GwgByteFile;
import com.petterroea.gwg.GwgFile;

public class MapGwg extends Map {
	private GwgByteFile gwg;
	public MapGwg(File file)
	{
		if(!file.getAbsolutePath().endsWith(".gwg")) throw new RuntimeException("Image file must be .gwg");
		try {
			gwg = (GwgByteFile) GwgFile.load(file);
			w = (int)gwg.w;
			h = (int)gwg.h;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public int get(int x, int y) {
		if(x<0||x>w||y<0||y>h) throw new RuntimeException("Coordinates outside of bounds");
		return gwg.get(x, y);
	}

	@Override
	public void set(int x, int y, int value) {
		if(x<0||x>w||y<0||y>h) throw new RuntimeException("Coordinates outside of bounds");// TODO Auto-generated method stub
		if(value<0||value>255) throw new RuntimeException("Value must be in the range 0-255"); //Not a restriction with gwg generally, but this program forces use of GwgByte, which has a range of 0-255
		gwg.set(x, y, (byte)value);
	}

}

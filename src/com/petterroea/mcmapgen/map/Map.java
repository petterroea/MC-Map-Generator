package com.petterroea.mcmapgen.map;

import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.petterroea.gwg.GwgByteFile;
import com.petterroea.gwg.GwgFile;
import com.petterroea.util.CommandLineUtils;
import com.petterroea.util.PropertiesFile;

public abstract class Map {
	public int w, h;
	public static Map load(File file)
	{
		if(file.getAbsolutePath().endsWith(".png")) { System.out.println("Loading map file as image heightmap..."); return new MapImage(file); }
		else if(file.getAbsolutePath().endsWith(".gwg")) { System.out.println("Loading map file as gwg..."); return new MapGwg(file); }
		else { System.out.println("Loading map file as raw..."); return new MapRaw(file); }
	}
	public static Map load(File file, PropertiesFile properties)
	{
		if(file.getAbsolutePath().endsWith(".png")) { System.out.println("Loading map file as image heightmap..."); return new MapImage(file); }
		else if(file.getAbsolutePath().endsWith(".gwg")) { System.out.println("Loading map file as gwg..."); return new MapGwg(file); }
		else { System.out.println("Loading map file as raw..."); return new MapRaw(file, properties); }
	}
	public abstract int get(int x, int y);
	public abstract void set(int x, int y, int value);
}

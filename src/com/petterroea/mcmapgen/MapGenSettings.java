package com.petterroea.mcmapgen;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 
 * @author petterroea
 *
 */
public class MapGenSettings {
	public byte[] mapData;
	public int mapw, maph;
	public int regionsx, regionsy;
	public boolean silent = false;
	public MapGenSettings(String mapGeometry)
	{
		BufferedImage map = null;
		try {
			map = ImageIO.read(new File(mapGeometry));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);	
		}
		mapw = map.getWidth();
		maph = map.getHeight();
		mapData = new byte[mapw*maph];
		System.out.println("Map data takes " + (int)((Byte.SIZE*mapData.length)/1000) + "kb in RAM");
		regionsx = (mapw/512)+1;
		regionsy = (maph/512)+1;
		if(mapw%512==0) { regionsx--; }
		if(maph%512==0) { regionsy--; }
		System.out.println("Map will take " + (regionsx*regionsy) + " regions.");
		System.out.println("Loading map...");
		//Load map to bytes.
		for(int xpos = 0; xpos < map.getWidth(); xpos++)
		{
			for(int ypos = 0; ypos < map.getHeight(); ypos++)
			{
				mapData[xpos+(ypos*mapw)]=Util.toByte(Util.avColor(map.getRGB(xpos, ypos)));
			}
		}
		//Map loaded.
		System.out.println("Done.");
	}
	public static MapGenSettings getSettings() {
		boolean cont = true;
		MapGenSettings settings = null;
		while(cont)
		{
			System.out.println("Now, what is the path to the image?");
			String imageURL = "";
			try {
				imageURL = McMapGen.br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			File file = new File(imageURL);
			if(!file.exists()) { System.out.println("The image does not exist!"); continue; }
			settings = new MapGenSettings(imageURL);
			break;
		}
		cont = true;
		
		return settings;
	}
}
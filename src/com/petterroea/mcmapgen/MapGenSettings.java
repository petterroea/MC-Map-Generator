package com.petterroea.mcmapgen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.petterroea.gwg.GwgByteFile;
import com.petterroea.gwg.GwgFile;

/**
 * 
 * @author petterroea
 *
 */
public class MapGenSettings {
	public int mapw, maph;
	public int regionsx, regionsz;
	public int spawnx, spawnz;
	public boolean silent = false;
	public boolean populate = false;
	public GwgByteFile map;
	public int waterHeight = 12;
	public MapGenSettings(String mapGeometry)
	{
		try {
			map = (GwgByteFile)GwgFile.load(new File(mapGeometry));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mapw = (int) map.w;
		maph = (int) map.h;
		System.out.println("Map data takes " + (int)((Byte.SIZE*map.w*map.h)/1000L) + "kb in RAM");
		regionsx = (mapw/512)+1;
		regionsz = (maph/512)+1;
		//if(mapw%512==0) { regionsx--; }
		//if(maph%512==0) { regionsz--; }
		System.out.println("Map will take " + (regionsx*regionsz) + " regions.");
		System.out.println("Now, where is the spawn? ");
		String spawnxs = "";
		String spawnzs = "";
		try { 
			while(spawnxs.equals("")||!Util.isInt(spawnxs))
			{
				System.out.print("X: ");
				spawnxs = McMapGen.br.readLine();
			}
			while(spawnzs.equals("")||!Util.isInt(spawnzs))
			{
				System.out.print("Z: ");
				spawnzs = McMapGen.br.readLine();
			}
			spawnx = Integer.parseInt(spawnxs);
			spawnz = Integer.parseInt(spawnzs);
		} catch(Exception e) {
			e.printStackTrace();
		}
		String wh = "";
		while(wh.equals("")||!Util.isInt(wh))
		{
			System.out.print("Water height?: ");
			wh = Util.getInput();
		}
		waterHeight = Integer.parseInt(wh);
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
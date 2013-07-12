package com.petterroea.mcmapgen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.petterroea.gwg.GwgByteFile;
import com.petterroea.gwg.GwgFile;
import com.petterroea.mcmapgen.map.BiomeMap;
import com.petterroea.mcmapgen.map.Map;

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
	public Map map;
	public BiomeMap biomeMap;
	public int waterHeight = 12;
	public boolean smooth = true;
	public float scale = 1.0f;
	public int smoothSize = 1;
	public float oreGenerationRate = 1.0f;
	public MapGenSettings()
	{
		
	}
	public void manualConfigure(String mapGeometry)
	{
		map = Map.load(new File(mapGeometry));
		System.out.println("Do you want the map at original scale");
		if(Util.yn())
		{
			System.out.println("Ok. Setting scale to 1.0");
			scale = 1.0f;
		}
		else
		{
			String sc = "";
			while(sc.equals("")||!Util.isFloat(sc))
			{
				System.out.println("Ok, what do you want the scale to be?");
				sc = Util.getInput();
			}
			scale = Float.parseFloat(sc);
			System.out.println("Set scale to " + scale);
		}
		System.out.println("Do you want more or less ores? No is good for single player, but you may want to say yes if you know a lot of people are going to play on the map.");
		if(Util.yn())
		{
			String gr = "emm";
			while(gr.equals("")||!Util.isFloat(gr))
			{
				System.out.println("Ok, what do you want the ore generation rate to be?");
				gr = Util.getInput();
			}
			oreGenerationRate = Float.parseFloat(gr);
			System.out.println("Set ore generation rate to " + oreGenerationRate);
		}
		else
		{
			System.out.println("Ok! Ore generation rate set to 1.0");
			oreGenerationRate = 1.0f;
		}
		mapw = (int) ((float)map.w*scale);
		maph = (int) ((float)map.h*scale);
		System.out.println("Map data takes " + (int)((Byte.SIZE*mapw*maph)/1000L) + "kb in RAM");
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
		biomeMap = new BiomeMap(this);
		System.out.println("Whan you scale maps down, they can become very choppy. This also can happen with some maps at original size. Do you want to try to smooth the landscape?");
		smooth = Util.yn();
		if(smooth)
		{
			smoothSize = Util.getIntFromInput("How big do you want the range of the smoothing to be(Default 2)");
		}
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
			settings = new MapGenSettings();
			settings.manualConfigure(imageURL);
			break;
		}
		cont = true;
		
		return settings;
	}
}
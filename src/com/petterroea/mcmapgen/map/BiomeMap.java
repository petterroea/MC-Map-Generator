package com.petterroea.mcmapgen.map;

import java.io.File;
import java.io.IOException;

import com.petterroea.gwg.GwgByteFile;
import com.petterroea.gwg.GwgFile;
import com.petterroea.mcmapgen.MapGenSettings;
import com.petterroea.mcmapgen.Util;
import com.petterroea.util.TotallyTheUsersFaultException;

public class BiomeMap {
	public Map map;
	public MapGenSettings settings;
	public BiomeMap(MapGenSettings settings)
	{
		this.settings = settings;
		System.out.print("Do you have a biome map? If so, please enter path(Empty for none): ");
		String in = Util.getInput();
		File f = new File(in);
		if(f.exists()) 
		{
			System.out.println("Loading biome map...");
			map = Map.load(f);
		}
		else
		{
			
		}
		throw new RuntimeException("File not found");
	}
	public BiomeMap()
	{
		
	}
	public int getBiome(int x, int y)
	{
		if(map==null) return 1;
		float biomeMapX = ((float)x/((float)settings.map.w*settings.scale))*(float)map.w;
		float biomeMapY = ((float)y/((float)settings.map.h*settings.scale))*(float)map.h;
		return map.get(Util.Min((int)biomeMapX, (int)map.w-1), Util.Min((int)biomeMapY, (int)map.h-1));
	}
}

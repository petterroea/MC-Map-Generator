package com.petterroea.mcmapgen.client;

import com.petterroea.mcmapgen.MapGenSettings;
import com.petterroea.mcmapgen.Region;
import com.petterroea.util.PropertiesFile;

public interface FarmHandler {
	public void setup();
	public void setup(PropertiesFile config, MapGenSettings settings);
	public Region getRegionToHandle();
	public void sendRegion(Region region);
	public MapGenSettings getSettings();
	public int toDo();
	public int processed();
	public void doneWithRegion();
}

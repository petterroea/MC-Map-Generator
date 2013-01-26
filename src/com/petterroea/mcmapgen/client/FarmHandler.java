package com.petterroea.mcmapgen.client;

import com.petterroea.mcmapgen.MapGenSettings;
import com.petterroea.mcmapgen.Region;

public interface FarmHandler {
	public void setup();
	public void regionDone();
	public Region getRegionToHandle();
	public void sendRegion(Region region);
	public MapGenSettings getSettings();
}

package com.petterroea.mcmapgen.client;

import com.petterroea.mcmapgen.MapGenSettings;
import com.petterroea.mcmapgen.Region;

/**
 * Farm handler used for multi-instance(When you have connected to a server)
 * @author petterroea
 *
 */
public class MultiFarmHandler implements FarmHandler {

	@Override
	public void setup() {
		// TODO Auto-generated method stub

	}

	@Override
	public Region getRegionToHandle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendRegion(Region region) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MapGenSettings getSettings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void doneGenerating() {
		// TODO Auto-generated method stub
		
	}

}

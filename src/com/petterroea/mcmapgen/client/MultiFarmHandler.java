package com.petterroea.mcmapgen.client;

import com.petterroea.mcmapgen.MapGenSettings;
import com.petterroea.mcmapgen.Region;
import com.petterroea.util.PropertiesFile;

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
	public int toDo() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int processed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void doneWithRegion() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setup(PropertiesFile config, MapGenSettings settings) {
		// TODO Auto-generated method stub
		
	}

}

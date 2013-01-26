package com.petterroea.mcmapgen.client;

import com.petterroea.mcmapgen.Region;

public class FarmThread implements Runnable {
	FarmHandler handler;
	public FarmThread(FarmHandler handler)
	{
		this.handler = handler;
	}
	public void run() {
		while(true)
		{
			//TODO: Generation loop
			Region region = handler.getRegionToHandle();
			region.generate(handler.getSettings());
			handler.sendRegion(region);
		}
	}

}

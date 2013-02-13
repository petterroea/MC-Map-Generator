package com.petterroea.mcmapgen.client;

import com.petterroea.mcmapgen.Region;
import com.petterroea.mcmapgen.Util;

public class FarmThread implements Runnable {
	FarmHandler handler;
	public FarmThread(FarmHandler handler)
	{
		this.handler = handler;
	}
	public void run() {
		long startTime = System.currentTimeMillis();
		int processed =  0;
		while(true)
		{
			long regionStartTime = System.currentTimeMillis();
			Region region = handler.getRegionToHandle();
			if(region==null) { System.out.println("Done generating!"); break; };
			if(processed%4==0)
			{
				System.out.println("Uptime: " + Util.getTimeString((System.currentTimeMillis()-startTime)/1000));
				int toDo = (handler.getSettings().regionsx*handler.getSettings().regionsz);
				System.out.println("Regions processed: " + processed + "/" + toDo + "(" + (((float)processed/(float)toDo)*100.0f) + "%)");
			}
			region.generate(handler.getSettings());
			handler.sendRegion(region);
			region = null;
			long timeToGenerate = System.currentTimeMillis()-regionStartTime;
			processed++;
		}
		handler.doneGenerating();
	}

}

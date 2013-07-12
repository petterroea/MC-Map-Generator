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
		while(true)
		{
			long regionStartTime = System.currentTimeMillis();
			Region region = handler.getRegionToHandle();
			if(region==null) { System.out.println("Done generating!"); break; };
			if(handler.processed()%4==0&&handler.processed()!=0)
			{
				System.out.println("Uptime: " + Util.getTimeString((System.currentTimeMillis()-startTime)/1000) + ", ETA " + Util.getTimeString((((System.currentTimeMillis()-startTime)/handler.processed())*(handler.toDo()-handler.processed()))/1000) + "(" + (((float)(System.currentTimeMillis()-startTime)/1000.0f)/(float)handler.processed()) + " seconds per region)");
				//int toDo = (handler.getSettings().regionsx*handler.getSettings().regionsz);
				System.out.println("Regions processed: " + handler.processed() + "/" + handler.toDo() + "(" + (((float)handler.processed()/(float)handler.toDo())*100.0f) + "%)");
			}
			region.generate(handler.getSettings());
			//handler.sendRegion(region);
			region.tempGenerate(region.getRegionFile(), handler.getSettings());
			region = null;
			long timeToGenerate = System.currentTimeMillis()-regionStartTime;
			handler.doneWithRegion();
		}
	}

}

package com.petterroea.mcmapgen.client;

import java.io.IOException;

import com.petterroea.mcmapgen.Chunk;
import com.petterroea.mcmapgen.McMapGen;
import com.petterroea.util.CommandLineUtils;

public class FarmWizard {

	public static void init() {
		System.out.println("Loading models and other stuff...");
		Chunk.preload();
		System.out.println("Do you want to connect to a server?");
		String reply = "";
		try {
			reply = McMapGen.br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FarmHandler handler = null;
		if(reply.equalsIgnoreCase("y"))
		{
			handler = new MultiFarmHandler();
		}
		else
		{
			handler = new SingleFarmHandler();
		}
		handler.setup();
		int priority = CommandLineUtils.promptInt("Thread priority", Thread.MIN_PRIORITY, Thread.MAX_PRIORITY);
		startThreads(handler, priority);
	}
	public static Thread[] threads;
	//public static Thread thread;
	public static void startThreads(FarmHandler handler, int priority)
	{
		if(threads!=null)
			//if(thread!=null)
			{
				System.out.println("There allready exists a client thread array :O");
			}
			else
			{
				threads = new Thread[Runtime.getRuntime().availableProcessors()];
				System.out.println("Starting " + threads.length + " threads with priority " + priority + "(max is " + Thread.MAX_PRIORITY + ")");
				for(int i = 0; i < threads.length; i++)
				{
					threads[i] = new Thread(new FarmThread(handler));
					threads[i].setPriority(priority);
					threads[i].start();
				}
				/*thread = new Thread(new FarmThread(handler));
				thread.start();*/
			}
	}
}

package com.petterroea.mcmapgen.server;

import java.io.IOException;

import com.petterroea.mcmapgen.MapGenSettings;
import com.petterroea.mcmapgen.McMapGen;
import com.petterroea.mcmapgen.Util;

public class ServerWizard {
	/*
	 * Server
	 * 
	 * The server is different from the client, in that it accepts connections, and 
	 */
	static boolean runGenThread = false;
	static int port = 24545;
	public static void init() 
	{
		System.out.println("Server chosen.");
		System.out.println("Do you want to run a generation-thread with the server?(Y/N)");
		String input = "";
		try {
			input = McMapGen.br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(input.equalsIgnoreCase("y")|| input.equalsIgnoreCase("yes"))
		{
			System.out.println("Ok. Running a generation thread as well. Note that you cant see the progress of it ;)");
			runGenThread=true;
		}
		else
		{
			System.out.println("Ok. Running only work-distribution.");
		}
		System.out.println("What port number do you want to use(none, or no-number for 24545, standard)? ");
		try {
			input = McMapGen.br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(input.length()>0&&Util.isInt(input))
		{
			port = Integer.parseInt(input);
		}
		//Now, get infodata on generation.
		MapGenSettings settings = MapGenSettings.getSettings();
		//Start the server thread.
		if(thread!=null)
		{
			System.out.println("There allready exists a server thread :O");
		}
		else
		{
			thread = new Thread(new ServerThread(settings, runGenThread, port));
			thread.start();
		}
	}
	public static Thread thread;
}

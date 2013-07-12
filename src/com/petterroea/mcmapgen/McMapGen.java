package com.petterroea.mcmapgen;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import com.petterroea.gwg.GwgByteFile;
import com.petterroea.gwg.GwgFile;
import com.petterroea.mcmapgen.client.FarmHandler;
import com.petterroea.mcmapgen.client.FarmWizard;
import com.petterroea.mcmapgen.client.MultiFarmHandler;
import com.petterroea.mcmapgen.client.SingleFarmHandler;
import com.petterroea.mcmapgen.map.BiomeMap;
import com.petterroea.mcmapgen.map.Map;
import com.petterroea.mcmapgen.server.ServerWizard;
import com.petterroea.util.CommandLineUtils;
import com.petterroea.util.MiscUtils;
import com.petterroea.util.PropertiesFile;

public class McMapGen {
	public static BufferedReader br;
	public final static String VERSION = "1.1.0";
	public static void main(String[] args) {
		br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("===McMapGen " + VERSION + "===");
		System.out.println("Made by petterroea");
		//Add your name on this line if you do a pull request. That way, your name will be in the credits if you get accepted ;)
		System.out.println(""); 
		System.out.println("Released under GPL 3 licence found here: http://www.gnu.org/licenses/gpl.html");
		System.out.println("Go download it from http://github.com/petterroea/mcmapgen");
		System.out.println("If you paid for this software, you shouldnt. It is free, and someone is trying to make money of you and my free, open source program. I use GPL, tho, so i cant do anything about it.");
		System.out.println("");
		boolean shouldLoadConfig = false;
		File configFile = null;
		if(args.length>0)
		{
			for(int i = 0; i < args.length; i++)
			{
				if(args[i].equalsIgnoreCase("-help")||args[i].equalsIgnoreCase("--help"))
				{
					System.out.println("###McMapGen help###");
					System.out.println("");
					System.out.println("Command:");
					System.out.println("    java [insert size arguments here] -jar " + MiscUtils.makeSafeForArgs(new File(McMapGen.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName()) + " [arguments] [config file]");
					System.out.println("Any number of arguments can be used, in any mix");
					System.out.println("");
					System.out.println("    -help         - Displays this message");
					System.out.println("    [Config file] - Loads the specified config file, and tries to use it instead of the wizard.");
					System.out.println("");
					System.out.println("                   NOTE:");
					System.out.println("                   If the config file does not exist, it will try to make it, and use default settings except when user input is needed. You will then be prompted.");
				}
				else
				{
					File f = new File(args[i]);
					if(!f.exists()) System.out.println("Error: File not found, or unknown command.");
					else 
					{
						shouldLoadConfig = true;
						configFile = f;
					}
				}
			}
		}
		if(shouldLoadConfig)
		{
			System.out.println("Attempting to load " + configFile.getAbsolutePath());
			if(loadConfig(configFile))
			{
				return;
			}
			else
			{
				System.out.println("There was an error loading the config file. Taking you through the wizard...");
				System.out.println("");
			}
		}
		System.out.println("Startup wizard:");
		System.out.println("If you want to host a server(For farming the generation), type 1, press enter");
		System.out.println("If you want to run the generation only on your pc, type 2, press enter");
		System.out.println("Anything else closes the program");
		System.out.print("Choice: ");
		String in = "";
		try {
			in = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		if(in.equals("1"))
		{
			ServerWizard.init();
		}
		else if(in.equals("2"))
		{
			FarmWizard.init();
		}
		else
		{
			System.out.println("I'm sorry, but i don't understand what you wrote :(");
		}
	}
	/**
	 * Entry point if you have a config file ready.
	 * @param configFile the config file
	 * @return false if it failed
	 */
	private static boolean loadConfig(File configFile) {
		// TODO Auto-generated method stub
		PropertiesFile config = new PropertiesFile(configFile);
		
		if(!config.containsKey("mode")) config.setInt("mode", promptInt("mode", 0, 1));
		if(config.getInt("mode")<0||config.getInt("mode")>1) return false;
		if(!config.containsKey("map")||!new File(config.getString("map")).exists()) config.setString("map", promptFile("map").toString());
		if(!config.containsKey("scale")) config.setFloat("scale", 1.0f);
		if(!config.containsKey("oreGenerationRate")) config.setFloat("oreGenerationRate", 1.0f);
		if(!config.containsKey("spawnx")) config.setInt("spawnx", 10); //Don't ask why 10. Just my default preference
		if(!config.containsKey("spawnz")) config.setInt("spawnz", 10);
		if(!config.containsKey("waterHeight")) config.setInt("waterHeight", promptInt("waterHeight", 1, 255));
		if(!config.containsKey("biomeMap")) config.setString("biomeMap", "");
		if(!config.containsKey("smoothMap")) config.setBool("smoothMap", false);
		if(!config.containsKey("smoothRadius")) config.setInt("smoothRadius", 2);
		if(!config.containsKey("threadPriority")) config.setInt("threadPriority", Thread.MAX_PRIORITY);
		
		System.out.println("Loading...");		
		MapGenSettings settings = new MapGenSettings();
		
		settings.map = Map.load(new File(config.getString("map")));
		settings.scale = config.getFloat("scale");
		settings.oreGenerationRate = config.getFloat("oreGenerationRate");
		settings.mapw = (int) ((float)settings.map.w*settings.scale);
		settings.maph = (int) ((float)settings.map.h*settings.scale);
		System.out.println("Funny fact: Map data takes " + (int)((Byte.SIZE*settings.mapw*settings.maph)/1024L) + "kb in RAM");
		settings.regionsx = (settings.mapw/512)+1;
		settings.regionsz = (settings.maph/512)+1;
		System.out.println("Map will take " + (settings.regionsx*settings.regionsz) + " regions.");
		settings.spawnx = config.getInt("spawnx");
		settings.spawnz = config.getInt("spawnz");
		settings.waterHeight = config.getInt("waterHeight");
		settings.biomeMap = new BiomeMap();
		settings.biomeMap.settings = settings;
		File f = new File(config.getString("biomeMap"));
		if(f.exists()) settings.biomeMap.map = Map.load(new File(config.getString("biomeMap")));
		settings.smooth = config.getBool("smoothMap");
		settings.smoothSize = config.getInt("smoothRadius");
		
		FarmHandler handler = null;
		if(config.getInt("mode")==0) handler = new SingleFarmHandler();
		if(config.getInt("mode")==1) handler = new MultiFarmHandler();
		handler.setup(config, settings);
		FarmWizard.startThreads(handler, config.getInt("threadPriority"));
		Chunk.preload();
		return true;
	}
	private static File promptFile(String missing) {
		System.out.println("The value \""+missing+"\" is missing from the config file. What do you want it to be?(Filename)");
		while(true)
		{
			System.out.print("What file do you want to use?: ");
			String in = CommandLineUtils.getInput();
			File f = new File(in);
			if(f.exists()) return f;
			System.out.println("Hmm... Please try that again...");
		}
	}
	private static int promptInt(String missing, int startRange, int endRange)
	{
		System.out.println("The value \""+missing+"\" is missing from the config file. What do you want it to be? Range: " + startRange + " - " + endRange);
		while(true)
		{
			int in = CommandLineUtils.getIntFromInput("What do you want it to be? Range: " + startRange + " - " + endRange);
			if(in>=startRange&&in<=endRange)
			{
				return in;
			}
			System.out.println("Hmm... Please try that again...");
		}
	}
	private static float promptFloat(String missing, float startRange, float endRange)
	{
		System.out.println("The value \""+missing+"\" is missing from the config file. What do you want it to be? Range: " + startRange + " - " + endRange);
		while(true)
		{
			float in = CommandLineUtils.getFloatFromInput("What do you want it to be? Range: " + startRange + " - " + endRange);
			if(in>=startRange&&in<=endRange)
			{
				return in;
			}
			System.out.println("Hmm... Please try that again...");
		}
	}
	private static boolean promptBool(String missing)
	{
		System.out.println("The value \""+missing+"\" is missing from the config file. What do you want it to be?(true/false)");
		while(true)
		{
			String in = CommandLineUtils.getInput();
			if(in.equalsIgnoreCase("true")) return true;
			if(in.equalsIgnoreCase("false")) return false;
			System.out.println("Hmm... Please try that again...");
		}
	}
}
package com.petterroea.mcmapgen.client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.petterroea.mcmapgen.MapGenSettings;
import com.petterroea.mcmapgen.Region;
import com.petterroea.mcmapgen.Util;
import com.petterroea.nbt.Tag;
import com.petterroea.nbt.TagByte;
import com.petterroea.nbt.TagCompound;
import com.petterroea.nbt.TagInt;
import com.petterroea.nbt.TagLong;
import com.petterroea.nbt.TagString;

/**
 * Handler used when handling local generation
 * @author petterroea
 *
 */
public class SingleFarmHandler implements FarmHandler {
	private MapGenSettings settings = null;
	boolean[] regions;
	File saveFolder;
	//For level.dat
	public byte hardcore = (byte)0;
	public byte features = (byte)0;
	public byte raining = (byte)0;
	public byte thundering = (byte)0;
	public int gameType = 0;
	public byte allowCommands = (byte)1;
	public String levelName = "UNTITLED";
	@Override
	public void setup() {
		System.out.println("What is the path to the map file?");
		String path = Util.getInput();
		settings = new MapGenSettings(path);
		regions = new boolean[settings.regionsx*settings.regionsz];
		for(int i = 0; i < regions.length; i++)
		{
			regions[i] = false;
		}
		System.out.println("Set up regions.");
		String in = "";
		while(in.equals(""))
		{
			System.out.println("Where do you want to save the regions to?");
			in = Util.getInput();
		}
		saveFolder = new File(in);
		if(saveFolder.exists())
		{
			if(saveFolder.isFile())
			{
				System.out.println("There is a file with that name :/");
				System.out.println("Please delete the file and re-run this program ;)");
			}
			else
			{
				System.out.println("There is a folder there allready called that. This program will now overwrite any Minecraft-related map files in that folder. Continue?");
				if(!Util.yn())
				{
					System.exit(0);
				}
			}
		}
		else
		{
			saveFolder.mkdirs();
			new File(saveFolder, "region").mkdirs();
		}
		System.out.println("Time to set level settings.");
		System.out.print("Hardcore?(Y/N): "); if(Util.yn()) { hardcore=(byte)1; } else { hardcore=(byte)0; }
		System.out.print("Want map features?(Villages, etc)(Y/N): "); if(Util.yn()) { features=(byte)1; } else { features=(byte)0; }
		System.out.print("Raining?(Y/N): "); if(Util.yn()) { raining=(byte)1; } else { raining=(byte)0; }
		System.out.print("Thundering?(Y/N): "); if(Util.yn()) { thundering=(byte)1; } else { thundering=(byte)0; }
		System.out.print("Creative?(Y/N): "); if(Util.yn()) { gameType=1; } else { gameType=0; }
		System.out.print("Allow singleplayer commands?(Y/N): "); if(Util.yn()) { allowCommands=(byte)1; } else { allowCommands=(byte)0; }
		while(levelName.equals("UNTITLED")||levelName.equals(""))
		{
			System.out.print("Level name?: ");
			levelName = Util.getInput();
		}
	}

	@Override
	public Region getRegionToHandle() {
		for(int i = 0; i < regions.length; i++)
		{
			//System.out.println("Checking region...");
			//System.out.println(regions[i]);
			if(regions[i]==false)
			{
				regions[i] = true;
				return new Region(i%settings.regionsx, i/settings.regionsx);
			}
		}
		return null;
	}

	@Override
	public void sendRegion(Region region) {
		try {
		File regionFile = new File(saveFolder, "/region/" + "r." + region.regionx + "." + region.regionz + ".mca");
		byte[] bytes = region.getBytes(settings);
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(regionFile));
		dos.write(bytes);
		dos.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public MapGenSettings getSettings() {
		// TODO Auto-generated method stub
		return settings;
	}

	@Override
	public void doneGenerating() {
		//Now we need to write the level.dat file
		TagCompound root = new TagCompound("");
		TagCompound data = new TagCompound("Data");
		//Stuff in data
		data.put(new TagByte("hardcore", hardcore));
		data.put(new TagInt("version", 19133));
		data.put(new TagByte("initalized", (byte)1));
		data.put(new TagString("LevelName", levelName));
		data.put(new TagString("generatorName", "default"));
		data.put(new TagInt("generatorVersion", 0));
		data.put(new TagString("generatorOptions", ""));
		data.put(new TagLong("RandomSeed", 1337691337L));
		data.put(new TagByte("MapFeatures", features));
		data.put(new TagLong("LastPlayed", System.currentTimeMillis()));
		data.put(new TagLong("SizeOnDisk", 1024*1024));
		data.put(new TagByte("allowCommands", allowCommands));
		data.put(new TagInt("GameType", gameType));
		data.put(new TagLong("Time", 0L));
		data.put(new TagLong("DayTime", 5000L));
		data.put(new TagInt("SpawnX", settings.spawnx));
		if(settings.spawnx<=settings.map.w&&settings.spawnz<=settings.map.h)
		{
			data.put(new TagInt("SpawnY", settings.map.get(settings.spawnx, settings.spawnz)));
		}
		else
		{
			data.put(new TagInt("SpawnY", 180));
		}
		data.put(new TagInt("SpawnZ", settings.spawnz));
		data.put(new TagByte("Raining", raining));
		data.put(new TagInt("rainTime", 1337*69));
		data.put(new TagByte("thundering", thundering));
		data.put(new TagInt("thunderTime", 1337*69*69));
		TagCompound gamerules = new TagCompound("GameRules");
			gamerules.put(new TagString("commandBlockOutput", "true"));
			gamerules.put(new TagString("doFireTick", "true"));
			gamerules.put(new TagString("doMobLoot", "true"));
			gamerules.put(new TagString("doMobSpawning", "true"));
			gamerules.put(new TagString("doTileDrops", "true"));
			gamerules.put(new TagString("keepInventory", "true"));
			gamerules.put(new TagString("mobGriefing", "true"));
		data.put(gamerules);
		//Put the data tag in root
		root.put(data);
		try {
			Tag.writeGzipped(new DataOutputStream(new FileOutputStream(new File(saveFolder, "level.dat"))), root);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
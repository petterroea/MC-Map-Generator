package com.petterroea.mcmapgen.client;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.petterroea.gwg.GwgConverter;
import com.petterroea.mcmapgen.MapGenSettings;
import com.petterroea.mcmapgen.Region;
import com.petterroea.mcmapgen.Util;
import com.petterroea.nbt.*;
import com.petterroea.util.PropertiesFile;

/**
 * Handler used when handling local generation
 * @author petterroea
 *
 */
public class SingleFarmHandler implements FarmHandler {
	boolean[] regions;
	public static File saveFolder;
	private MapGenSettings settings = null;
	//For level.dat
	public byte hardcore = (byte)0;
	public byte features = (byte)0;
	public byte raining = (byte)0;
	public byte thundering = (byte)0;
	public int gameType = 0;
	public byte allowCommands = (byte)1;
	public String levelName = "UNTITLED";
	private static int toDo = 0;
	private static int processed = 0;
	@Override
	public void setup(PropertiesFile config, MapGenSettings settings) {
		this.settings = settings;
		
		if(!config.containsKey("doEntireMap")) config.setBool("doEntireMap", true);
		if(!config.containsKey("saveLocation")) config.setString("saveLocation", "world_generated");
		if(!config.containsKey("hardcoreMode")) config.setBool("hardcoreMode", false);
		if(!config.containsKey("generateFeatures")) config.setBool("generateFeatures", false);
		if(!config.containsKey("rain")) config.setBool("rain", false);
		if(!config.containsKey("thunder")) config.setBool("thunder", false);
		if(!config.containsKey("creativeMode")) config.setBool("creativeMode", false);
		if(!config.containsKey("allowCommands")) config.setBool("allowCommands", false);
		if(!config.containsKey("levelName")) config.setString("levelName", "UNTITLED");
		
		
		int rangeStart = 0;
		int rangeEnd = settings.regionsx*settings.regionsz;
		toDo = settings.regionsx*settings.regionsz;
		if(!config.getBool("doEntireMap"))
		{
			rangeStart = config.getInt("rangeStart");
			rangeEnd = config.getInt("rangeEnd");
			toDo = rangeEnd-rangeStart;
		}
		regions = new boolean[settings.regionsx*settings.regionsz];
		for(int i = 0; i < regions.length; i++)
		{
			if(i>=rangeStart&&i<rangeEnd)
			{
				regions[i] = false;
			}
			else
			{
				regions[i] = true;
			}
		}
		System.out.println("Set up regions.");
		saveFolder = new File(config.getString("saveLocation"));
		saveFolder.mkdirs();
		new File(saveFolder, "region").mkdirs();
		if(config.getBool("hardcoreMode")) { hardcore=(byte)1; } else { hardcore=(byte)0; }
		if(config.getBool("generateFeatures")) { features=(byte)1; } else { features=(byte)0; }
		if(config.getBool("rain")) { raining=(byte)1; } else { raining=(byte)0; }
		if(config.getBool("thunder")) { thundering=(byte)1; } else { thundering=(byte)0; }
		if(config.getBool("creativeMode")) { gameType=(byte)1; } else { gameType=(byte)0; }
		if(config.getBool("allowCommands")) { allowCommands=(byte)1; } else { allowCommands=(byte)0; }
		levelName = config.getString("levelName");
	}
	@Override
	public void setup() {
		System.out.println("What is the path to the map file?");
		String path = Util.getInput();
		settings = new MapGenSettings();
		settings.manualConfigure(path);
		System.out.println("Do you want to do the whole thing?");
		int rangeStart = 0;
		int rangeEnd = settings.regionsx*settings.regionsz;
		if(!Util.yn())
		{
			String rs = "";
			while(rs.equals("")||!Util.isInt(rs))
			{
				System.out.println("Ok. What is the start number of chunks?");
				rs = Util.getInput();
			}
			String re = "";
			while(re.equals("")||!Util.isInt(re))
			{
				System.out.println("What is the end index?(i < End index)");
				re = Util.getInput();
			}
			rangeStart = Integer.parseInt(rs);
			rangeEnd = Integer.parseInt(re);
			toDo = rangeEnd-rangeStart;
		}
		else
		{
			System.out.println("Ok!");
			toDo = settings.regionsx*settings.regionsz;
		}
		regions = new boolean[settings.regionsx*settings.regionsz];
		for(int i = 0; i < regions.length; i++)
		{
			if(i>=rangeStart&&i<rangeEnd)
			{
				regions[i] = false;
			}
			else
			{
				regions[i] = true;
			}
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
		synchronized(this)
		{
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
			if(!finished)
			{
				doneGenerating();
				finished = true;
			}
			return null;
		}
	}

	@Override
	public void sendRegion(Region region) {
		try {
		
		byte[] bytes = region.getBytes(settings);
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(region.getRegionFile()));
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
	private boolean finished = false;
	private void doneGenerating() {
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
		//Player data. Copypaste from old project.
		TagCompound player = new TagCompound("Player");
		TagList<TagDouble> motion = new TagList<TagDouble>("Motion");
		motion.add(new TagDouble("", 0.0));
		motion.add(new TagDouble("", 0.0));
		motion.add(new TagDouble("", 0.0));
		player.put("Motion", motion);
		player.put("foodExhaustionLevel", new TagFloat("foodExhaustionLevel", 0.0f));
		player.put("foodTickTimer", new TagInt("foodTickTimer", 0));
		player.put("PersistentId", new TagInt("PersistentId", 814667874));
		player.put("XpLevel", new TagInt("XpLevel", 0));
		player.put("Health", new TagShort("Health", (short)19));
		player.put("Inventory", new TagList<TagByte>("Inventory"));
		player.put("AttackTime", new TagShort("AttackTime", (short)0));
		player.put("Sleeping", new TagByte("Sleeping", (byte)0));
		player.put("Fire", new TagShort("Fire", (short)-20));
		player.put("foodLevel", new TagInt("foodLevel", 20));
		player.put("Score", new TagInt("Score", 0));
		player.put("DeathTime", new TagShort("DeathTime", (short)0));
		player.put("XpP", new TagFloat("XpP", 0.0f));
		player.put("SleepTimer", new TagShort("SleepTimer", (short)0));
		player.put("HurtTime", new TagShort("HurtTime", (short)0));
		player.put("OnGround", new TagByte("OnGround", (byte)1));
		player.put("Dimension", new TagInt("Dimension", 0));
		player.put("Air", new TagShort("Air", (short)300));
		TagList<TagDouble> pos = new TagList<TagDouble>("Pos");
		pos.add(new TagDouble("", settings.spawnx));
		pos.add(new TagDouble("", 255));
		pos.add(new TagDouble("", settings.spawnz));
		player.put("Pos", pos);
		player.put("foodSaturationLevel", new TagFloat("foodSaturationLevel", 0.0f));
		TagCompound abilities = new TagCompound("abilities");
			player.put("flying", new TagByte("flying", (byte)1));
			player.put("mayfly", new TagByte("mayfly", (byte)1));
			player.put("instabuild", new TagByte("instabuild", (byte)1));
			player.put("invulnerable", new TagByte("invulnerable", (byte)1));
		player.put("abilities", abilities);
		player.put("FallDistance", new TagFloat("FallDistance", 0.0f));
		player.put("XpTotal", new TagInt("XpTotal", 0));
		TagList<TagFloat> rot = new TagList<TagFloat>("Rotation");
			rot.add(new TagFloat("", 0.0f));
			rot.add(new TagFloat("", 0.0f));
		player.put("Rotation", rot);
		
		data.put(player);
		//Put the data tag in root
		root.put(data);
		try {
			Tag.writeGzipped(new DataOutputStream(new FileOutputStream(new File(saveFolder, "level.dat"))), root);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int toDo() {
		// TODO Auto-generated method stub
		return toDo;
	}

	@Override
	public int processed() {
		// TODO Auto-generated method stub
		return processed;
	}

	@Override
	public void doneWithRegion() {
		processed++;
	}
}
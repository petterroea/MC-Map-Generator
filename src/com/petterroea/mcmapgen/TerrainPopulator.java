package com.petterroea.mcmapgen;

import java.io.DataInputStream;
import java.util.Random;

import com.petterroea.mcmapgen.client.Model;

public class TerrainPopulator {
	public static void populateRegion(Region r, MapGenSettings settings)
	{
		populateOre(3, r, 32, (int)(20.0 /* *settings.oreGenerationRate */ ), 0, 128); //Dirt
		populateOre(13, r, 32, (int)(10.0 /* *settings.oreGenerationRate */ ), 0, 128); //Gravel
		populateOre(16, r, 16, (int)(20.0*settings.oreGenerationRate), 0, 128); //Coal
		populateOre(15, r, 8, (int)(20.0*settings.oreGenerationRate), 0, 64); //Iron
		populateOre(14, r, 8, (int)(2.0*settings.oreGenerationRate), 0, 32); //Gold
		populateOre(73, r, 7, (int)(8.0*settings.oreGenerationRate), 0, 16); //Redstone
		populateOre(56, r, 7, (int)(1.0*settings.oreGenerationRate), 0, 16); //Diamond
		populateOre(21, r, 6, (int)(2.0*settings.oreGenerationRate), 0, 16); //Lapis from 0 - 16
		populateOre(21, r, 6, (int)(1.0*settings.oreGenerationRate), 16, 32); //Lapis from 16 - 32
		//populateTallGrass(r);
	}
	//CRAPPY FUNCTION THAT EATS CPU TIME. DO NOT USE.
	public static void populateTallGrass(Region r)
	{
		Random rand = new Random();
		for(int generated = 0; generated < 16*16*32*32; generated++)
		{
			int randX = rand.nextInt(16*32);
			int randZ = rand.nextInt(16*32);
			int biome = r.chunks[r.getChunkIndex(randX, randZ)].getBiomeAt(randX%16, randZ%16).ordinal();
			if(Chunk.tallGrassPer100Terrain[biome]<generated)
			{
				//Try generation
				for(int y = 254; y > 1; y--)
				{
					if(r.getBlockId(randX, y, randZ)==2)
					{
						if(biome==Chunk.Biome.DESERT.ordinal()||biome==Chunk.Biome.DESERT_HILLS.ordinal())
						{
							//r.setBlockAndMetadata(randX, y+1, randZ, 31, 0);
						}
						else
						{
							//r.setBlockAndMetadata(randX, y+1, randZ, 31, 1);
						}
						r.setBlockAndMetadata(randX, y+1, randZ, 31, 1);
						break;
					}
					if(r.getBlockId(randX, y, randZ)!=0)
					{
						break;
					}
				}
			}
		}
	}
	public static void populateOre(int id, Region r, int amountOfOre, int iterationsPerChunk, int startHeight, int stopHeight)
	{
		Random rand = new Random();
		for(int i = 0; i < iterationsPerChunk*32*32; i++)
		{
			int xPos = rand.nextInt((32*16)-1);
			int yPos = rand.nextInt(stopHeight-startHeight)+startHeight;
			int zPos = rand.nextInt((32*16)-1);
			int cubeSize = (int)(Math.cbrt(amountOfOre)*1.5);
			for(int a = 0; a < amountOfOre; a++)
			{
				xPos = Util.Min((32*16)-1, Util.Max(0, rand.nextInt(cubeSize)-(cubeSize/2)+xPos));
				zPos = Util.Min((32*16)-1, Util.Max(0, rand.nextInt(cubeSize)-(cubeSize/2)+zPos));
				yPos = Util.Min(255, Util.Max(0, rand.nextInt(cubeSize)-(cubeSize/2)+yPos));
				if(r.getBlockId(xPos, yPos, zPos)==1) { r.chunks[r.getChunkIndex(xPos, zPos)].setBlock(xPos%16, yPos, zPos%16, id); /* System.out.println("Set ore " + id + " at X:" + xPos + ", Y:" + yPos + ", Z:" + zPos); System.out.println("Deg:" + deg + ", Rad:" + rad); */ }
			}
		}
	}
	public static Random rand;
	public static boolean shouldDoTallGrass(int x, int z, int biome) {
		if(rand==null) rand=new Random();
		float ratio = (float)Chunk.tallGrassPer100Terrain[biome]/100.0f;
		if(rand.nextFloat()<=ratio) return true;
		return false;
	}
	public boolean tryGenerateTree(Region r, int x, int height, int z, int biome) {
		if(rand==null) rand=new Random();
		float factor = (float)Chunk.treesPer1000Terrain[biome]/1000.0f;
		if(rand.nextFloat()>factor) return false;
		Chunk.getTree(biome).build(r, x-2, height+1, z-2);
		return true;
	}
	public static void setTallGrass(Region r, int x, int y, int z, int biome) {
		if(rand==null) rand=new Random();
		if(rand.nextInt(300)==0&&!(biome==Chunk.Biome.DESERT.ordinal()||biome==Chunk.Biome.DESERT_HILLS.ordinal()))
		{
			int ra = rand.nextInt(100);
			if(ra<30) { r.setBlockAndMetadata(x, y, z, 37, 0); }
			else if(ra<60) { r.setBlockAndMetadata(x, y, z, 38, 0); }
			else if(ra<80) { r.setBlockAndMetadata(x, y, z, 39, 0); }
			else { r.setBlockAndMetadata(x, y, z, 40, 0); }
		}
		else
		{
			if(biome==Chunk.Biome.DESERT.ordinal()||biome==Chunk.Biome.DESERT_HILLS.ordinal()) { r.setBlockAndMetadata(x, y, z, 31, 0); } else { r.setBlockAndMetadata(x, y, z, 31, 1); }
		}		
	}
}

package com.petterroea.mcmapgen;

import java.util.Random;

public class TerrainPopulator {
	public static void populateRegion(Region r, MapGenSettings settings)
	{
		populateOre(3, r, 32, (int)(20.0*settings.oreGenerationRate), 0, 128); //Dirt
		populateOre(13, r, 32, (int)(10.0*settings.oreGenerationRate), 0, 128); //Gravel
		populateOre(16, r, 16, (int)(20.0*settings.oreGenerationRate), 0, 128); //Coal
		populateOre(15, r, 8, (int)(20.0*settings.oreGenerationRate), 0, 64); //Iron
		populateOre(14, r, 8, (int)(2.0*settings.oreGenerationRate), 0, 32); //Gold
		populateOre(73, r, 7, (int)(8.0*settings.oreGenerationRate), 0, 16); //Redstone
		populateOre(56, r, 7, (int)(1.0*settings.oreGenerationRate), 0, 16); //Diamond
		populateOre(21, r, 6, (int)(2.0*settings.oreGenerationRate), 0, 16); //Lapis from 0 - 16
		populateOre(21, r, 6, (int)(1.0*settings.oreGenerationRate), 16, 32); //Lapis from 16 - 32
		populateTallGrass(r);
	}
	public static void populateTallGrass(Region r)
	{
		Random rand = new Random();
		for(int generated = 0; generated < 16*16*32*32; generated++)
		{
			int randX = rand.nextInt(16*32);
			int randZ = rand.nextInt(16*32);
			int biome = r.chunks[r.getChunkIndex(randX, randZ)].getBiomeAt(randX%16, randZ%16).ordinal();
			if(Chunk.tallGrassPerRegion[biome]<generated)
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
}

package com.petterroea.mcmapgen;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import com.petterroea.nbt.Tag;
import com.petterroea.nbt.TagByte;
import com.petterroea.nbt.TagByteArray;
import com.petterroea.nbt.TagCompound;
import com.petterroea.nbt.TagInt;
import com.petterroea.nbt.TagIntArray;
import com.petterroea.nbt.TagList;
import com.petterroea.nbt.TagLong;

public class Chunk {
	byte[] blocks = new byte[16*16*256]; //Done
	byte[] add = new byte[(16*16*256)/2]; //Done
	byte[] data = new byte[(16*16*256)/2]; //Done
	byte[] blockLight = new byte[(16*16*256)/2]; //Done
	byte[] skyLight = new byte[(16*16*256)/2]; //Done
	int[] heightMap = new int[16*16]; //Implemented.
	byte[] biome = new byte[16*16]; //Implemented
	int chunkx, chunkz;
	public static Biome[] values = Biome.values();
	public enum Biome{
		OCEAN,
		PLAINS,
		DESERT,
		EXTREME_HILLS,
		FOREST, 
		TAIGA,
		SWAMPLAND,
		RIVER,
		HELL,
		END,
		FROZEN_OCEAN,
		FROZEN_RIVER,
		ICE_PLAINS,
		ICE_MOUNTAINS,
		MUSHROOM_ISLAND,
		MUSHROOM_ISLAND_SHORE,
		BEACH,
		DESERT_HILLS,
		FOREST_HILLS,
		TAIGA_HILLS,
		EXTREME_HILLS_EDGE,
		JUNGLE,
		JUNGLE_HILLS
	}
	public static int[] tallGrassPerRegion = {
			0, //Ocean
			((16*16)-16)*32*32, //Plains
			(16)*32*32, //Desert
			((16*16)-(16*12))*32*32, //Extreme hills
			((16*16)-(16*4))*32*32, //Forest
			((16*16)-(16*4))*32*32, //Taiga
			((16*16)-(16*8))*32*32, //Swamp
			0, //River
			0, //Nether
			0, //End
			0, //Frozen Ocean
			0, //Frozen River
			0, //Ice plains
			0, //Ice mountains
			0, //Mushroom Island
			0, //Mushroom island shore
			0, //Beach
			(16)*32*32, //Desert hills
			((16*16)-(16*10))*32*32, //Forest hills
			((16*16)-(16*10))*32*32, //Taiga hills
			((16*16)-(16*12))*32*32, //Extreme hills edge
			((16*16)-(16*4))*32*32, //Jungle
			((16*16)-(16*10))*32*32, //Jungle hills
	};
	public static int getTopCoverId(int biome, int yFromTop, boolean underWater)
	{
		switch(biome)
		{
		case 2:
			if(yFromTop<4) { return 12; } else { return 24; }
		case 16:
			if(yFromTop<4) { return 12; } else { return 24; }
		case 17:
			if(yFromTop<4) { return 12; } else { return 24; }
		case 12:
			if(underWater) { return 79; } else { if(yFromTop==0) { return 78; } else if(yFromTop<3) { return 79; } else { return 79; } }
		case 13:
			if(underWater) { return 79; } else { if(yFromTop==0) { return 78; } else if(yFromTop<3) { return 79; } else { return 79; } }
		default:
			if(yFromTop==0) { return 2; } else { return 3; }
		}
	}
	public Chunk(int chunkx, int chunkz)
	{
		this.chunkx = chunkx;
		this.chunkz = chunkz;
		for(int i = 0; i < biome.length; i++)
		{
			biome[i]=1;
		}
		for(int i = 0; i < skyLight.length; i++)
		{
			skyLight[i] = (byte)((int)255);
		}
		for(int i = 0; i < blockLight.length; i++)
		{
			blockLight[i] = (byte)((int)255);
		}
	}
	public void generateHeightMap()
	{
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				for(int y = 255; y >= 0; y++)
				{
					if(getBlockId(x, y, z)!=0)
					{
						heightMap[x+z*16]=(byte)(y&0xFF);
						break;
					}
				}
			}
		}
	}
	public Biome getBiomeAt(int x, int z)
	{
		return values[biome[x+z*16]];
	}
	public void setBiome(int x, int z, Biome b)
	{
		biome[x+z*16] = (byte)b.ordinal();
	}
	public int getHeightMapData(int x, int z)
	{
		return heightMap[Util.Min(Util.Max(x, 0), 15)+(Util.Min(Util.Max(z, 0), 15)*16)]; //Clamps both x and z to 0-16
	}
	public void setHeightMapData(int x, int z, int height)
	{
		int toDo = Util.Max(0, Util.Min(height, 255)); //Clamps the height map data to 0-255 to not fuck up something. Low chance of it happening, but lets keep it safe ;)
		byte b = (byte) toDo;
		heightMap[x+z*16] = b;
	}
	public void setBlockAndMetadata(int x, int y, int z, int id, int meta)
	{
		setBlock(x, y, z, id);
		setMetadata(x, y, z, meta);
	}
	public void setBlock(int x, int y, int z, int id)
	{
		if(id>4096) id=4096;
		int blockPos = (y*16*16) + (z*16) + x;
		blocks[blockPos] = (byte) (id&0xFF);
		add[blockPos/2] = Util.toNibble4(add[blockPos/2], blockPos, (byte) (id >> 8));
	}
	public int getBlockId(int x, int y, int z)
	{
		int blockPos = y*16*16 + z*16 + x;
		byte blockId_a = blocks[blockPos];
		byte blockId_b = Util.Nibble4(add, blockPos);
		return blockId_a + (blockId_b << 8);
	}
	public void setMetadata(int x, int y, int z, int meta)
	{
		int blockPos = y*16*16 + z*16 + x;
		data[blockPos/2] = Util.toNibble4(data[blockPos/2], blockPos, (byte)(meta&0x0F));
	}
	public byte getMetadata(int x, int y, int z)
	{
		int blockPos = y*16*16 + z*16 + x;
		return Util.Nibble4(data, blockPos);
	}
	public void setSkyLight(int x, int y, int z, int meta)
	{
		int blockPos = y*16*16 + z*16 + x;
		skyLight[blockPos/2] = Util.toNibble4(skyLight[blockPos/2], blockPos, (byte)(meta&0x0F));
	}
	public byte getSkyLight(int x, int y, int z)
	{
		int blockPos = y*16*16 + z*16 + x;
		return Util.Nibble4(skyLight, blockPos);
	}
	public void setBlockLight(int x, int y, int z, int meta)
	{
		int blockPos = y*16*16 + z*16 + x;
		blockLight[blockPos/2] = Util.toNibble4(blockLight[blockPos/2], blockPos, (byte)(meta&0x0F));
	}
	public byte getBlockLight(int x, int y, int z)
	{
		int blockPos = y*16*16 + z*16 + x;
		return Util.Nibble4(blockLight, blockPos);
	}
	public TagCompound getTagCompound(MapGenSettings settings)
	{
		TagCompound tag = new TagCompound("");
		TagCompound level = new TagCompound("Level");
		//Level
			if(settings.populate)
			{
				//level.put(new TagByte("TerrainPopulated", (byte)1));
			}
			else
			{
				//level.put(new TagByte("TerrainPopulated", (byte)0));
			}
			level.put(new TagByte("TerrainPopulated", (byte)1));
			level.put(new TagInt("xPos", chunkx));
			level.put(new TagInt("zPos", chunkz));
			level.put(new TagLong("LastUpdate", System.currentTimeMillis()));
			level.put(new TagByteArray("Biomes", biome));
			level.put(new TagList<TagCompound>("Entities"));
			level.put(getSections());
			level.put(new TagList<TagCompound>("TileEntities"));
			level.put(new TagIntArray("HeightMap", heightMap));
		tag.put("Level", level);
		//tag.print(0);
		return tag;
	}
	private TagList getSections()
	{
		TagList<TagCompound> list = new TagList<TagCompound>("Sections");
		for(int ypos = 0; ypos < 16; ypos++)
		{
			TagCompound compound = new TagCompound("");
			compound.put(new TagByte("Y", (byte)ypos));
			byte[] tempBlockLight = new byte[16*16*8];
			for(int a = 0; a < 16*16*8; a++)
			{
				tempBlockLight[a]=blockLight[a+(ypos*16*8*16)];
			}
			compound.put(new TagByteArray("BlockLight", tempBlockLight));
			byte[] tempBlockData = new byte[16*16*16];
			for(int a = 0; a < 16*16*16; a++)
			{
				tempBlockData[a]=blocks[a+(ypos*16*16*16)];
			}
			compound.put(new TagByteArray("Blocks", tempBlockData));
			byte[] tempAdd = new byte[16*16*8];
			for(int a = 0; a < 16*16*8; a++)
			{
				tempAdd[a] = add[a+(ypos*16*8*16)];
			}
			compound.put(new TagByteArray("Add", tempAdd));
			byte[] tempData = new byte[16*16*8];
			for(int a = 0; a < 16*16*8; a++)
			{
				tempData[a] = data[a+(ypos*16*8*16)];
			}
			compound.put(new TagByteArray("Data", tempData));
			byte[] tempSkyLight = new byte[16*16*8];
			for(int a = 0; a < 16*16*8; a++)
			{
				tempSkyLight[a] = skyLight[a+(ypos*16*8*16)];
			}
			compound.put(new TagByteArray("SkyLight", tempSkyLight));
			list.add(compound);
		}
		return list;
	}
	public ByteArrayOutputStream getData(MapGenSettings settings) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			TagCompound compound = getTagCompound(settings);
			//compound.print(0);
			Tag.writeDeflated(new DataOutputStream(bos), compound);
			return bos;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

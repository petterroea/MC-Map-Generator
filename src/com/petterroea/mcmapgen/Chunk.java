package com.petterroea.mcmapgen;

public class Chunk {
	byte[] blocks = new byte[16*16*256]; //Done
	byte[] add = new byte[(16*16*256)/2]; //Done
	byte[] data = new byte[(16*16*256)/2]; //Done
	byte[] blockLight = new byte[(16*16*256)/2]; //Done
	byte[] skyLight = new byte[(16*16*256)/2]; //Done
	byte[] heightMap = new byte[16*16]; //Implemented.
	byte[] biome = new byte[16*16]; //Implemented
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
	public Chunk()
	{
		for(int i = 0; i < biome.length; i++)
		{
			biome[i]=1;
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
		return Biome.values()[biome[x+z*16]];
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
		int blockPos = y*16*16 + z*16 + x;
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
}

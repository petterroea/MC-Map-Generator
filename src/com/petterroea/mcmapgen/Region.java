package com.petterroea.mcmapgen;

import com.petterroea.mcmapgen.Chunk.Biome;

public class Region {
	public Chunk[] chunks;
	public Region()
	{
		chunks = new Chunk[16*16];
		for(int i = 0; i < 16*16; i++)
		{
			chunks[i] = new Chunk();
		}
	}
	public void generate(MapGenSettings settings) 
	{
		
	}
	/*
	 * Helpers for block placement. They take region-local coordinates and target the right chunk with the right local coordinates.
	 */
	public int getChunkIndex(int x, int z)
	{
		return (x/16)+((z/16)*16);
	}
	public void generateHeightmap()
	{
		for(int i = 0; i < 16*16; i++)
		{
			chunks[i].generateHeightMap();
		}
	}
	public void setBlockAndMetadata(int x, int y, int z, int id, int meta)
	{
		setBlock(x, y, z, id);
		setBlockMetadata(x, y, z, meta);
	}
	public void setBlock(int x, int y, int z, int id)
	{
		int chunkx = x/16;
		int chunkz = z/16;
		int locx = x%16;
		int locz = z%16;
		chunks[x+(z*16)].setBlock(locx, y, locz, id);
	}
	public void setBlockMetadata(int x, int y, int z, int meta)
	{
		int chunkx = x/16;
		int chunkz = z/16;
		int locx = x%16;
		int locz = z%16;
		chunks[x+(z*16)].setMetadata(locx, y, locz, meta);
	}
	public byte getMetadata(int x, int y, int z)
	{
		int locx = x%16;
		int locz = z%16;
		return chunks[getChunkIndex(x, z)].getMetadata(locx, y, locz);
	}
	public int getBlockId(int x, int y, int z)
	{
		int locx = x%16;
		int locz = z%16;
		return chunks[getChunkIndex(x, z)].getBlockId(locx, y, locz);
	}
	public Biome getBiomeAt(int x, int z)
	{
		int locx = x%16;
		int locz = z%16;
		return chunks[getChunkIndex(x, z)].getBiomeAt(locx, locz);
	}
	public void setBiome(int x, int z, Biome b)
	{
		int locx = x%16;
		int locz = z%16;
		chunks[getChunkIndex(x, z)].setBiome(locx, locz, b);
	}
	public int getHeightMapData(int x, int z)
	{
		int locx = x%16;
		int locz = z%16;
		return chunks[getChunkIndex(x, z)].getHeightMapData(locx, locz);
	}
	public void setHeightMapData(int x, int z, int height)
	{
		int locx = x%16;
		int locz = z%16;
		chunks[getChunkIndex(x, z)].setHeightMapData(locx, locz, height);
	}
	public void setSkyLight(int x, int y, int z, int meta)
	{
		int locx = x%16;
		int locz = z%16;
		chunks[getChunkIndex(x, z)].setSkyLight(locx, y, locz, meta);
	}
	public byte getSkyLight(int x, int y, int z)
	{
		int locx = x%16;
		int locz = z%16;
		return chunks[getChunkIndex(x, z)].getSkyLight(locx, y, locz);
	}
	public void setBlockLight(int x, int y, int z, int meta)
	{
		int locx = x%16;
		int locz = z%16;
		chunks[getChunkIndex(x, z)].setBlockLight(locx, y, locz, meta);
	}
	public byte getBlockLight(int x, int y, int z)
	{
		int locx = x%16;
		int locz = z%16;
		return chunks[getChunkIndex(x, z)].getBlockLight(locx, y, locz);
	}
}
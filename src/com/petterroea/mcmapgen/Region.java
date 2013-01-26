package com.petterroea.mcmapgen;

public class Region {
	Chunk[] chunks;
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
}
class Chunk
{
	
}
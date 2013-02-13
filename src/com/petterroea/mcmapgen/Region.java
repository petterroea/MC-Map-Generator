package com.petterroea.mcmapgen;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import com.petterroea.mcmapgen.Chunk.Biome;

public class Region {
	public Chunk[] chunks;
	public int regionx, regionz;
	public static int SECTOR_SIZE=4096;
	public static int HEADER_SIZE=5;
	public Region(int regionx, int regionz)
	{
		this.regionx = regionx;
		this.regionz = regionz;
		chunks = new Chunk[32*32];
		for(int i = 0; i < 32*32; i++)
		{
			chunks[i] = new Chunk((i%32)+(regionx*32), (i/32)+(regionz*32));
		}
	}
	public byte[] getBytes(MapGenSettings settings)
	{
		try{
			ByteArrayOutputStream bos = new ByteArrayOutputStream(8192*16); //I know this will need expansion
			int[] header = new int[32*32];
			ByteArrayOutputStream[] streams = new ByteArrayOutputStream[32*32];
			long offset = 2;
			for(int i = 0; i < streams.length; i++)
			{
				streams[i] = chunks[i].getData(settings);
				int toHeader = 0;
				int sectorsUsed = ((streams[i].size()+HEADER_SIZE)/SECTOR_SIZE)+1;
				System.out.println("Offset: " + offset);
				toHeader = (int) (((int)offset<<8)+(sectorsUsed&0xFF));
				offset += sectorsUsed;
				header[i] = toHeader;
			}
			DataOutputStream dos = new DataOutputStream(bos);
			//Start writing to byte array
			for(int i = 0; i < header.length; i++)
			{
				dos.writeInt(header[i]);
			}
			for(int i = 0; i < 32*32; i++)
			{
				dos.writeInt(0);
			}
			//Start writing chunk data.
			for(int i = 0; i < chunks.length; i++)
			{
				int offsetVal = (header[i]>>8)*SECTOR_SIZE;
				while(dos.size()<offsetVal)
				{
					dos.writeByte(105); //69 in hex
				}
				if(dos.size()>offsetVal)
				{
					System.out.println("ERROR: We are at bigger offset then the chunk position! :(:(:(");
					System.exit(0);
				}
				dos.writeInt(streams[i].size());
				dos.writeByte((byte)2);
				dos.write(streams[i].toByteArray());
			}
			
			return bos.toByteArray();
		} catch(Exception e) {
		e.printStackTrace();	
		}
		return null;
	}
	public void generate(MapGenSettings settings) 
	{
		for(int x = 0; x < 16*32; x++)
		{
			for(int z = 0; z < 16*32; z++)
			{
				if(x+(regionx*32*16)<settings.map.w&&z+(regionz*32*16)<settings.map.h)
				{
					int height = settings.map.get(x+(regionx*32*16), z+(regionz*32*16))&0xFF;
					for(int y = 0; y < 256; y++)
					{
						if(y==height) chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, 2);
						if(y<height) chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, 1);
						if(y>height) { if(y<=settings.waterHeight) {  chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, 8); } else {chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, 0); } }
						//chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, 1);
					}
				}
				else
				{
					for(int y = 0; y < 256; y++)
					{
						//chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, 0);
						chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, 1);
					}
				}
			}
		}
	}
	/*
	 * Helpers for block placement. They take region-local coordinates and target the right chunk with the right local coordinates.
	 */
	public int getChunkIndex(int x, int z)
	{
		return (x/16)+((z/16)*32);
	}
	public void generateHeightmap()
	{
		for(int i = 0; i < 32*32; i++)
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
		chunks[chunkx+(chunkz*32)].setBlock(locx, y, locz, id);
	}
	public void setBlockMetadata(int x, int y, int z, int meta)
	{
		int chunkx = x/16;
		int chunkz = z/16;
		int locx = x%16;
		int locz = z%16;
		chunks[chunkx+(chunkz*32)].setMetadata(locx, y, locz, meta);
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
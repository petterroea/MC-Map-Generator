package com.petterroea.mcmapgen;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import net.minecraft.world.level.chunk.storage.RegionFile;

import com.petterroea.mcmapgen.Chunk.Biome;
import com.petterroea.mcmapgen.client.SingleFarmHandler;
import com.petterroea.nbt.Tag;

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
	public File getRegionFile()
	{
		return new File(SingleFarmHandler.saveFolder, "/region/" + "r." + regionx + "." + regionz + ".mca");
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
				//System.out.println("Offset: " + offset);
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
	public void tempGenerate(File regionFile, MapGenSettings settings)
	{
		RegionFile file = new RegionFile(regionFile); //Cheating^^
		for(int i = 0; i < chunks.length; i++)
		{
			DataOutputStream chunkDataOutputStream = file.getChunkDataOutputStream(i%32, i/32);
			Tag.write(chunkDataOutputStream, chunks[i].getTagCompound(settings));
			try {
				chunkDataOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void generate(MapGenSettings settings) 
	{
		for(int x = 0; x < 16*32; x++)
		{
			for(int z = 0; z < 16*32; z++)
			{
				float posScaledX = (float)(x+(regionx*32*16))/(float)(settings.mapw+1);
				float posScaledZ = (float)(z+(regionz*32*15))/(float)(settings.maph+1);
				if((x+(regionx*32*16))<settings.mapw&&(z+(regionz*32*15))<settings.maph)
				{
					int height = settings.map.get((long)(posScaledX*(float)(settings.map.w-1)), (long)(posScaledZ*(float)(settings.map.h-1)))&0xFF;
					//Thanks to hanna, here is some code to "smooth" height based on neighbour blocks.
//					if(settings.smooth)
//					{
//						int samples = 0;
//						int sampledHeight = 0;
//						//Start sampling.
//						if(x>0||regionx>0) { sampledHeight += settings.map.get((long)((float)(x-1+(regionx*32*16))/(float)(settings.mapw+1)*(float)(settings.map.w-1)), (long)((float)(z+(regionz*32*15))/(float)(settings.maph+1)*(float)(settings.map.h-1))); samples++; }
//						if(z>0||regionz>0) { sampledHeight += settings.map.get((long)((float)(x+(regionx*32*16))/(float)(settings.mapw+1)*(float)(settings.map.w-1)), (long)((float)(z-1+(regionz*32*15))/(float)(settings.maph+1)*(float)(settings.map.h-1))); samples++; }
//						if((x>0||regionx>0)&&(z>0||regionz>0)) { sampledHeight += settings.map.get((long)((float)(x-1+(regionx*32*16))/(float)(settings.mapw+1)*(float)(settings.map.w-1)), (long)((float)(z-1+(regionz*32*15))/(float)(settings.maph+1)*(float)(settings.map.h-1))); samples++; }
//						if((x>0||regionx>0)&&(z<(16*32)-1||regionz<settings.regionsz-1)) { sampledHeight += settings.map.get((long)((float)(x-1+(regionx*32*16))/(float)(settings.mapw+1)*(float)(settings.map.w-1)), (long)((float)(z+1+(regionz*32*15))/(float)(settings.maph+1)*(float)(settings.map.h-1))); samples++; }
//						if(z<(16*32)-1||regionz<settings.regionsz-1) { sampledHeight += settings.map.get((long)((float)(x+(regionx*32*16))/(float)(settings.mapw+1)*(float)(settings.map.w-1)), (long)((float)(z+1+(regionz*32*15))/(float)(settings.maph+1)*(float)(settings.map.h-1))); samples++; }
//						if((x<(16*32)-1||regionx<settings.regionsx-1)&&(z<(16*32)-1||regionz<settings.regionsz-1)) { sampledHeight += settings.map.get((long)((float)(x+1+(regionx*32*16))/(float)(settings.mapw+1)*(float)(settings.map.w-1)), (long)((float)(z+1+(regionz*32*15))/(float)(settings.maph+1)*(float)(settings.map.h-1))); samples++; }
//						if(x<(16*32)-1||regionx<settings.regionsx-1) { sampledHeight += settings.map.get((long)((float)(x+1+(regionx*32*16))/(float)(settings.mapw+1)*(float)(settings.map.w-1)), (long)((float)(z+(regionz*32*15))/(float)(settings.maph+1)*(float)(settings.map.h-1))); samples++; }
//						if((x<(16*32)-1||regionx<settings.regionsx-1)&&(z>0||regionz>0)) { sampledHeight += settings.map.get((long)((float)(x+1+(regionx*32*16))/(float)(settings.mapw+1)*(float)(settings.map.w-1)), (long)((float)(z-1+(regionz*32*15))/(float)(settings.maph+1)*(float)(settings.map.h-1))); samples++; }
//						height = (int)((float)sampledHeight/(float)samples);
//					}
					if(settings.smooth) { height = interpolate((x+(regionx*32*16)), (z+(regionz*32*15)), settings.smoothSize, height, settings); }
					int biome = settings.biomeMap.getBiome((int)(x+(regionx*32*16)), (int)(z+(regionz*32*15)));
					chunks[getChunkIndex(x, z)].setBiome(x%16, z%16, Chunk.values[biome]);
					for(int y = 0; y < 256; y++)
					{
						if(y==0) { chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, 7); }
						else if(y<=height&&y>=height-6) { chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, Chunk.getTopCoverId(biome, height-y, y<=settings.waterHeight)); }
						else if(y<height) chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, 1);
						else if(y>height) { if(y<=settings.waterHeight) {  if(y==settings.waterHeight&&biome==10){ chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, 79); } else {chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, 9); chunks[getChunkIndex(x, z)].setBiome(x%16, z%16, Biome.OCEAN);}} else {chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, 0); } }
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
		TerrainPopulator.populateRegion(this, settings);
	}
	int[] interpolationRange; //Variable to store points to take into count when interpolating
	public int interpolate(int x, int z, int size, int height, MapGenSettings settings)
	{
		if(size<1) return 69;
		if(interpolationRange==null)
		{
			//Find the size of interpolation area
			int widthVariable = 1;
			for(int i = 1; i < size; i++)
			{
				widthVariable = widthVariable+2;
			}
			//We have to generate the interpolation points
			interpolationRange = new int[widthVariable*widthVariable*2];
			for(int genX = 0; genX < widthVariable; genX++)
			{
				for(int genY = 0; genY < widthVariable; genY++)
				{
					interpolationRange[(genX+(genY*widthVariable))*2+0] = genX-(widthVariable/2);
					interpolationRange[(genX+(genY*widthVariable))*2+1] = genY-(widthVariable/2);
				}
			}
		}
		int samples = 0;
		int sampled = 0;
		for(int i = 0; i < interpolationRange.length/2; i++)
		{
			int xPos = x+interpolationRange[(i*2)+0];
			int yPos = z+interpolationRange[(i*2)+1];
			if(xPos<0||yPos<0||xPos>=settings.mapw||yPos>=settings.maph) continue;
			sampled += settings.map.get((long)(((float)(xPos)/(float)(settings.mapw+1))*(float)(settings.map.w)), (long)(((float)(yPos)/(float)(settings.maph+1))*(float)(settings.map.h))); 
			samples++; 
		}
		int toAdd = samples/3;
		sampled += (height*toAdd);
		samples += toAdd;
		
		return sampled/samples;
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
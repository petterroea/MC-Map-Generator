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
	public boolean[] placedAreas; //For more intelligent tree placement, this holds 2d collision between trees, and mabe houses in  the future.
	public boolean[] placedBoxes; //Same, but this one is the box around the entire object
	public int[] heightmap;
	public int[] biomes;
	public static int SECTOR_SIZE=4096;
	public static int HEADER_SIZE=5;
	public Region(int regionx, int regionz)
	{
		this.regionx = regionx;
		this.regionz = regionz;
		chunks = new Chunk[32*32];
		placedAreas = new boolean[16*16*32*32];
		placedBoxes = new boolean[16*16*32*32];
		heightmap = new int[16*16*32*32];
		biomes = new int[16*16*32*32];
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
		TerrainPopulator populator = new TerrainPopulator();
		for(int x = 0; x < 16*32; x++)
		{
			for(int z = 0; z < 16*32; z++)
			{
				float posScaledX = (float)(x+(regionx*32*16))/(float)(settings.mapw+1);
				float posScaledZ = (float)(z+(regionz*32*16))/(float)(settings.maph+1);
				if((x+(regionx*32*16))<settings.mapw&&(z+(regionz*32*16))<settings.maph)
				{
					int height = settings.map.get((int)(posScaledX*(float)(settings.map.w-1)), (int)(posScaledZ*(float)(settings.map.h-1)));
					if(settings.smooth) { height = interpolate((x+(regionx*32*16)), (z+(regionz*32*16)), settings.smoothSize, height, settings); }
					int biome = settings.biomeMap.getBiome((int)(x+(regionx*32*16)), (int)(z+(regionz*32*16)));
					chunks[getChunkIndex(x, z)].setBiome(x%16, z%16, Chunk.values[biome]);
					for(int y = 0; y < 256; y++)
					{
						if(y==height+1&&TerrainPopulator.shouldDoTallGrass(x, z, biome)&&y>settings.waterHeight) { TerrainPopulator.setTallGrass(this, x, y, z, biome); }
						else if(y==0) { chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, 7); }
						else if(y<=height&&y>=height-6) { chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, Chunk.getTopCoverId(biome, height-y, y<=settings.waterHeight)); }
						else if(y<height) chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, 1);
						else if(y>height) { if(y<=settings.waterHeight) {  if(y==settings.waterHeight&&biome==10){ chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, 79); } else {chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, 9); chunks[getChunkIndex(x, z)].setBiome(x%16, z%16, Biome.OCEAN);}} else {chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, 0); } }
						//chunks[getChunkIndex(x, z)].setBlock(x%16, y, z%16, 1);
					}
					heightmap[x+(z*16*32)] = height;
					biomes[x+(z*16*32)] = biome;
					//Old tree generator
					/* if(height>settings.waterHeight)
					{
						//populator.tryGenerateTree(this, x, height, z, biome); //Replacing this SHIT.
					}*/
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
		//Generate shit
		TerrainPopulator.populateRegion(this, settings);
		//Generate trees
		for(int x = 0; x < 16*32; x++)
		{
			for(int z = 0; z < 16*32; z++)
			{
				if(!placedAreas[x+(z*32*16)]&&!placedBoxes[x+(z*32*16)]&&heightmap[x+(z*16*32)]>settings.waterHeight)
				{
					if(populator.tryGenerateTree(this, x, heightmap[x+(z*16*32)], z, biomes[x+(z*16*32)]))
					{
						//TODO - index Z=16*32(512) is not acessed, because index starts at 0. Use (16*32)-1 or 511 instead.
						placedAreas[Util.Max(Util.Min(x, 511), 0)+(Util.Max(Util.Min(z, 511), 0)*16*32)] = true;
						placedBoxes[Util.Max(Util.Min(x, 511), 0)+(Util.Max(Util.Min(z, 511), 0)*16*32)] = true;
						
						placedAreas[Util.Max(Util.Min(x-1, 511), 0)+(Util.Max(Util.Min(z, 511), 0)*16*32)] = true;
						placedBoxes[Util.Max(Util.Min(x-1, 511), 0)+(Util.Max(Util.Min(z, 511), 0)*16*32)] = true;
						
						placedAreas[Util.Max(Util.Min(x+1, 511), 0)+(Util.Max(Util.Min(z, 511), 0)*16*32)] = true;
						placedBoxes[Util.Max(Util.Min(x+1, 511), 0)+(Util.Max(Util.Min(z, 511), 0)*16*32)] = true;
					
						placedAreas[Util.Max(Util.Min(x, 511), 0)+(Util.Max(Util.Min(z+1, 511), 0)*16*32)] = true;
						placedBoxes[Util.Max(Util.Min(x, 511), 0)+(Util.Max(Util.Min(z+1, 511), 0)*16*32)] = true;
						
						placedAreas[Util.Max(Util.Min(x-1, 511), 0)+(Util.Max(Util.Min(z+1, 511), 0)*16*32)] = true;
						placedBoxes[Util.Max(Util.Min(x-1, 511), 0)+(Util.Max(Util.Min(z+1, 511), 0)*16*32)] = true;
						
						placedAreas[Util.Max(Util.Min(x+1, 511), 0)+(Util.Max(Util.Min(z+1, 511), 0)*16*32)] = true;
						placedBoxes[Util.Max(Util.Min(x+1, 511), 0)+(Util.Max(Util.Min(z+1, 511), 0)*16*32)] = true;
					
						placedAreas[Util.Max(Util.Min(x, 511), 0)+(Util.Max(Util.Min(z-1, 511), 0)*16*32)] = true;
						placedBoxes[Util.Max(Util.Min(x, 511), 0)+(Util.Max(Util.Min(z-1, 511), 0)*16*32)] = true;
						
						placedAreas[Util.Max(Util.Min(x-1, 511), 0)+(Util.Max(Util.Min(z-1, 511), 0)*16*32)] = true;
						placedBoxes[Util.Max(Util.Min(x-1, 511), 0)+(Util.Max(Util.Min(z-1, 511), 0)*16*32)] = true;
						
						placedAreas[Util.Max(Util.Min(x+1, 511), 0)+(Util.Max(Util.Min(z-1, 511), 0)*16*32)] = true;
						placedBoxes[Util.Max(Util.Min(x+1, 511), 0)+(Util.Max(Util.Min(z-1, 511), 0)*16*32)] = true;
					
					}
				}
			}
		}
		populator.tryGenerateTree(this, 10, 200, 10, 1);
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
			sampled += settings.map.get((int)(((float)(xPos)/(float)(settings.mapw+1))*(float)(settings.map.w)), (int)(((float)(yPos)/(float)(settings.maph+1))*(float)(settings.map.h))); 
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
		int locx = x%16;
		int locz = z%16;
		chunks[getChunkIndex(x, z)].setBlock(locx, y, locz, id);
	}
	public void setBlockMetadata(int x, int y, int z, int meta)
	{
		int chunkx = x/16;
		int chunkz = z/16;
		int locx = x%16;
		int locz = z%16;
		chunks[getChunkIndex(x, z)].setMetadata(locx, y, locz, meta);
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
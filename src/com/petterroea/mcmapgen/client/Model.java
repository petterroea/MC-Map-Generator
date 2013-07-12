package com.petterroea.mcmapgen.client;

import java.io.DataInputStream;

import com.petterroea.mcmapgen.Region;

public class Model {
	public Model(DataInputStream in)
	{
		try {
			if(!in.readUTF().equals("SBF 1.0")) return;
			w = in.readInt();
			d = in.readInt();
			h = in.readInt();
			data = new int[w*d*h];
			metadata = new int[w*d*h];
			for(int i = 0; i < w*d*h; i++)
			{
				data[i]=(int)in.readShort();
			}
			in.readByte();
			for(int i = 0; i < w*d*h; i++)
			{
				metadata[i]=(int)in.readShort();
			}
		} catch(Exception e) {
			e.printStackTrace();
			//System.exit(0);
		}
	}
	public int w, h, d;
	public int[] data;
	public int[] metadata;
	public void build(Region reg, int x, int y, int z)
	{
		//System.out.println("###BEGIN MODEL DUMP###");
		if(x+w<16*32&&z+d<16*32&&y-h>0)
		{
			for(int ax = 0; ax < w; ax++)
			{
				for(int az = 0; az < d; az++)
				{
					for(int ay = 0; ay < h; ay++)
					{
						int index = ax+(az*w)+(ay*w*d);
						//System.out.println("Placing " + data[index] + ", meta " + metadata[index]);
						int occuBlockId = reg.getBlockId(x+ax, y+ay, z+az);
						//System.out.println("Placing id "+data[index]+". X: "+ax+", Y:"+ay+", Z:"+az);
						if(occuBlockId==0||occuBlockId==31||occuBlockId==32)
						{
							reg.setBlock(x+ax, y+ay, z+az, data[index]);
						}
						//reg.setBlock(x+ax, y+ay, z+az, data[index]);
						//System.out.println("r.setBlock(x+"+ax+", y+"+ay+", z+"+az+", "+data[index]+");");		
						//r.setBlockAndMetadata(x+ax, y+ay, z+az, data[index], metadata[index]);
					}
				}
			}
		}
	}
}
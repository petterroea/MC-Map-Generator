package com.petterroea.mcmapgen;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Datafile, used for transmission of a file from an array of bytes from the file.
 * @author petterroea
 *
 */
public class DataFile {
	DataChunk[] chunks;
	public DataFile(byte[] data)
	{
		makeChunks(data);
	}
	public DataFile(String filename)
	{
		File file = new File(filename);
		byte [] fileData = new byte[(int)file.length()];
		try{
		DataInputStream dis = new DataInputStream((new FileInputStream(file)));
		dis.readFully(fileData);
		dis.close();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		makeChunks(fileData);
	}
	public DataFile(File file)
	{
		byte [] fileData = new byte[(int)file.length()];
		try{
		DataInputStream dis = new DataInputStream((new FileInputStream(file)));
		dis.readFully(fileData);
		dis.close();
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		makeChunks(fileData);
	}
	private void makeChunks(byte[] data)
	{
		int chunksToMake = (data.length/DataChunk.CHUNK_LENGTH)+1;
		if(data.length%DataChunk.CHUNK_LENGTH==0)
		{
			chunksToMake-=1;
		}
		chunks = new DataChunk[chunksToMake];
		for(int i = 0; i < chunks.length; i++)
		{
			long byteIndex = i*DataChunk.CHUNK_LENGTH;
			long byteStopFetch = byteIndex+DataChunk.CHUNK_LENGTH;
			if(byteStopFetch>data.length)
			{
				System.out.println("OMG");
				byteStopFetch = data.length;
			}
			chunks[i] = new DataChunk(Util.subArray(data, byteIndex, byteStopFetch));
		}
	}
	public void dumpData()
	{
		System.out.println("---Datafile---");
		System.out.println("Chunks: " + chunks.length);
		System.out.println("##BEGIN CHUNK DUMP##");
		for(int i = 0; i < chunks.length; i++)
		{
			System.out.println("---Begin chunk " + i + ", MD5: " + chunks[i].getMD5() + ", Length: " + chunks[i].getDataLength() + "---");
			for(int a = 0; a < chunks[i].getDataLength(); a++)
			{
				System.out.print(Util.byteHex(chunks[i].getData()[a]) + ",");
				//System.out.print(chunks[i].getData()[a] + " ");
			}
			System.out.println("");
			System.out.println("---End chunk " + i + "---");
		}
	}
}
/**
 * 64k of bytes. We send in chunks so we can check checksums of parts of the file and ask for that chunk again if it is broken.
 */
class DataChunk
{
	private byte[] data;
	private String checksum;
	public static final int CHUNK_LENGTH = 1024*256;
	public DataChunk(byte[] data)
	{
		if(data.length>CHUNK_LENGTH)
		{
			System.out.println("Got more then " + (CHUNK_LENGTH/1024) + "k in one datachunk! Last bytes ignored.");
			byte[] newData = new byte[CHUNK_LENGTH];
			for(int i = 0; i < newData.length; i++)
			{
				newData[i]=data[i];
			}
			data=newData;
		}
		this.data=data;
		checksum = Util.getMD5(this.data);
	}
	public byte[] getData()
	{
		return data;
	}
	public String getMD5()
	{
		return checksum;
		
	}
	public int getDataLength()
	{
		return data.length;
	}
}
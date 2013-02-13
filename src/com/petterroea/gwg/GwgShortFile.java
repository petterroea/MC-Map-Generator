package com.petterroea.gwg;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GwgShortFile extends GwgFile {
	long w, h;
	short[][] data;
	private final long CHUNK_SIZE = 1024*1024*1024; //1GiB

	public GwgShortFile(long w, long h)
	{
		this.w = w;
		this.h = h;
        int chunks = (int)((w*h)/CHUNK_SIZE);
        int remainder = (int)((w*h) - ((long)chunks)*CHUNK_SIZE);
        data = new short[chunks+(remainder==0?0:1)][];
        for( int idx=chunks; --idx>=0; ) {
            data[idx] = new short[(int)CHUNK_SIZE];
        }
        if( remainder != 0 ) {
            data[chunks] = new short[remainder];
        }
	}
	public short get( long index ) {
        if( index<0 || index>=w*h ) {
            throw new IndexOutOfBoundsException("Error attempting to access data element "+index+".  Array is "+w*h+" elements long.");
        }
        int chunk = (int)(index/CHUNK_SIZE);
        int offset = (int)(index - (((long)chunk)*CHUNK_SIZE));
        return data[chunk][offset];
    }
    public void set( long index, short b ) {
        if( index<0 || index>=w*h ) {
            throw new IndexOutOfBoundsException("Error attempting to access data element "+index+".  Array is "+w*h+" elements long.");
        }
        int chunk = (int)(index/CHUNK_SIZE);
        int offset = (int)(index - (((long)chunk)*CHUNK_SIZE));
        data[chunk][offset] = b;
    }
    public short get(long x, long y)
    {
    	return get(x+(y*w));
    }
    public void set(long x, long y, short b)
    {
    	set(x+(y*w), b);
    }
	@Override
	public void save(DataOutputStream os) throws IOException {
		DataOutputStream out = new DataOutputStream(new GZIPOutputStream(os));
		out.writeLong(w);
		out.writeLong(h);
		out.writeByte(GwgFile.STORAGE_SHORT);
		for(long l = 0; l < data.length; l++)
		{
			out.writeShort(get(l));
		}
		out.close();
	}

}

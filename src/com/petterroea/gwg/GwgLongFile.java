package com.petterroea.gwg;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GwgLongFile extends GwgFile {
	long w, h;
	long[][] data;
	private final long CHUNK_SIZE = 1024*1024*1024; //1GiB

	public GwgLongFile(long w, long h)
	{
		this.w = w;
		this.h = h;
        int chunks = (int)((w*h)/CHUNK_SIZE);
        int remainder = (int)((w*h) - ((long)chunks)*CHUNK_SIZE);
        data = new long[chunks+(remainder==0?0:1)][];
        for( int idx=chunks; --idx>=0; ) {
            data[idx] = new long[(int)CHUNK_SIZE];
        }
        if( remainder != 0 ) {
            data[chunks] = new long[remainder];
        }
	}
	public long get( long index ) {
        if( index<0 || index>=w*h ) {
            throw new IndexOutOfBoundsException("Error attempting to access data element "+index+".  Array is "+w*h+" elements long.");
        }
        int chunk = (int)(index/CHUNK_SIZE);
        int offset = (int)(index - (((long)chunk)*CHUNK_SIZE));
        return data[chunk][offset];
    }
    public void set( long index, long b ) {
        if( index<0 || index>=w*h ) {
            throw new IndexOutOfBoundsException("Error attempting to access data element "+index+".  Array is "+w*h+" elements long.");
        }
        int chunk = (int)(index/CHUNK_SIZE);
        int offset = (int)(index - (((long)chunk)*CHUNK_SIZE));
        data[chunk][offset] = b;
    }
    public long get(long x, long y)
    {
    	return get(x+(y*w));
    }
    public void set(long x, long y, long b)
    {
    	set(x+(y*w), b);
    }
	@Override
	public void save(DataOutputStream os) throws IOException {
		DataOutputStream out = new DataOutputStream(new GZIPOutputStream(os));
		out.writeLong(w);
		out.writeLong(h);
		out.writeByte(GwgFile.STORAGE_LONG);
		for(long i = 0; i < w*h; i++)
		{
			out.writeLong(get(i));
		}
		out.close();
	}

}

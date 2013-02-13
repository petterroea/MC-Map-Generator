package com.petterroea.gwg;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GwgByteFile extends GwgFile {
	public long w, h;
	byte[][] data;
	private final long CHUNK_SIZE = 1024*1024*1024; //1GiB

	public GwgByteFile(long w, long h)
	{
		this.w = w;
		this.h = h;
        int chunks = (int)((w*h)/CHUNK_SIZE);
        int remainder = (int)((w*h) - ((long)chunks)*CHUNK_SIZE);
        data = new byte[chunks+(remainder==0?0:1)][];
        for( int idx=chunks; --idx>=0; ) {
            data[idx] = new byte[(int)CHUNK_SIZE];
        }
        if( remainder != 0 ) {
            data[chunks] = new byte[remainder];
        }
	}
	public byte get( long index ) {
        if( index<0 || index>=w*h ) {
            throw new IndexOutOfBoundsException("Error attempting to access data element "+index+".  Array is "+w*h+" elements long.");
        }
        int chunk = (int)(index/CHUNK_SIZE);
        int offset = (int)(index - (((long)chunk)*CHUNK_SIZE));
        return data[chunk][offset];
    }
    public void set( long index, byte b ) {
        if( index<0 || index>=w*h ) {
            throw new IndexOutOfBoundsException("Error attempting to access data element "+index+".  Array is "+w*h+" elements long.");
        }
        int chunk = (int)(index/CHUNK_SIZE);
        int offset = (int)(index - (((long)chunk)*CHUNK_SIZE));
        data[chunk][offset] = b;
    }
    public byte get(long x, long y)
    {
    	return get(x+(y*w));
    }
    public void set(long x, long y, byte b)
    {
    	set(x+(y*w), b);
    }
	@Override
	public void save(DataOutputStream os) throws IOException {
		DataOutputStream out = new DataOutputStream(new GZIPOutputStream(os));
		out.writeLong(w);
		out.writeLong(h);
		out.writeByte(GwgFile.STORAGE_BYTE);
		for(long l = 0; l < w*h; l++)
		{
			out.writeByte(get(l));
		}
		out.close();
	}
}

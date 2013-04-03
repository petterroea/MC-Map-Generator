package com.petterroea.gwg;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GwgByteFile extends GwgFile {
	public long w, h;
	byte[][] data;

	public GwgByteFile(long w, long h)
	{
		this.w = w;
		this.h = h;
		data = new byte[(int)w][(int)h];
	}
	public byte get( long index ) {
        if( index<0 || index>=w*h ) {
            throw new IndexOutOfBoundsException("Error attempting to access data element "+index+".  Array is "+w*h+" elements long.");
        }
        int x = (int) (index%w);
        int y = (int) (index/w);
        return data[x][y];
    }
    public void set( long index, byte b ) {
        if( index<0 || index>=w*h ) {
            throw new IndexOutOfBoundsException("Error attempting to access data element "+index+".  Array is "+w*h+" elements long.");
        }
        int x = (int) (index%w);
        int y = (int) (index/w);
        data[x][y] = b;
    }
    public byte get(long x, long y)
    {
    	return data[(int)x][(int)y];
    }
    public void set(long x, long y, byte b)
    {
    	data[(int)x][(int)y] = b;
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

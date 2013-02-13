/*
 * TagByteArray.java
 * 
 * 1.0
 * 
 * 07 Feb 2013
 * 
 * Public domain
 */

package com.petterroea.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * ByteArray - A Tag that contains an array of bytes.
 * @author petterroea
 *
 */
public class TagByteArray extends Tag {
	/**
	 * Data stored in this tag
	 */
	private byte[] data;
	/**
	 * Basic constructor
	 * @param name The name of the tag
	 */
	public TagByteArray(String name) {
		super(name);
	}
	/**
	 * Basic constructor
	 * @param name The name of the tag
	 * @param data Data to be stored in the tag
	 */
	public TagByteArray(String name, byte[] data)
	{
		super(name);
		this.setData(data);
	}

	@Override
	public void read(DataInputStream is) throws IOException {
		int l = is.readInt();
		data = new byte[l];
		is.readFully(data);

	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeInt(data.length);
		os.write(data);

	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return Tag.TAG_Byte_Array;
	}

	@Override
	public Tag copy() {
		byte[] newArray = new byte[data.length];
		System.arraycopy(data, 0, newArray, 0, data.length);
		return new TagByteArray(this.getName(), newArray);
	}
	/**
	 * Need the data used in the tag? Use this
	 * @return the byte array contained in this tag
	 */
	public byte[] getData() {
		return data;
	}
	/**
	 * Set the data contained in this tag
	 * @param data the data to be stored
	 */
	public void setData(byte[] data) {
		this.data = data;
	}
	@Override
	public boolean equals(Object o)
	{
		if(super.equals(o))
		{
			TagByteArray b = (TagByteArray)o;
			if (b.getData()==null&&this.getData()!=null) return false;
			if (b.getData()!=null&&this.getData()==null) return false;
			if (b.getData()==null&&this.getData()==null) return true;
			if (b.getData()!=null&&this.getData()!=null&&b.getData().equals(this.getData())) return true;
			return false;
		}
		return false;
	}
	public void print(int indices)
	{
		System.out.println(getSpacing(indices) + "TAG_Byte_Array('" + this.getName() + "'): [" + this.data.length + " bytes]");
	}
}
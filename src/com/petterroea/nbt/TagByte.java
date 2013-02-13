/*
 * TagByte.java
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
 * TagByte - a Tag that contains a byte.
 * @author petterroea
 *
 */
public class TagByte extends Tag {
	/**
	 * Tag data
	 */
	private byte data = 0;
	/**
	 * Basic constructor with name
	 * @param name Name of the tag
	 */
	public TagByte(String name)
	{
		super(name);
	}
	/**
	 * Basc constructor with name and data
	 * @param name Name of the tag
	 * @param data Data to be used in the tag
	 */
	public TagByte(String name, byte data)
	{
		super(name);
		this.data = data;
	}
	/**
	 * Name says it all
	 * @return the byte stored in this tag
	 */
	public byte getData()
	{
		return data;
	}
	/**
	 * Sets the data stored in this tag
	 * @param data the data to be stored
	 */
	public void setData(byte data)
	{
		this.data=data;
	}
	@Override
	public void read(DataInputStream is) throws IOException {
		data = is.readByte();
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeByte(data);
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return Tag.TAG_Byte;
	}
	@Override
	public Tag copy() {
		// TODO Auto-generated method stub
		return new TagByte(this.getName(), data);
	}
	@Override
	public boolean equals(Object o)
	{
		if(super.equals(o))
		{
			TagByte b = (TagByte)o;
			if (b.getData()!=this.data ) return false;
			return true;
		}
		return false;
	}
	public void print(int indices)
	{
		System.out.println(getSpacing(indices) + "TAG_Byte('" + this.getName() + "'): " + this.data);
	}
}

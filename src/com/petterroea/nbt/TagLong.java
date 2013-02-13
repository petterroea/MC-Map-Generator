/*
 * TagLong.java
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

public class TagLong extends Tag {
	private long data;
	public TagLong(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	public TagLong(String name, long data)
	{
		super(name);
		this.data = data;
	}
	
	@Override
	public void read(DataInputStream is) throws IOException {
		data = is.readLong();
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeLong(data);
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return Tag.TAG_Long;
	}

	@Override
	public Tag copy() {
		// TODO Auto-generated method stub
		return new TagLong(this.getName(), data);
	}
	public long getData()
	{
		return data;
	}
	public void setData(long data)
	{
		this.data = data;
	}
	@Override
	public boolean equals(Object o)
	{
		if(super.equals(o))
		{
			TagLong b = (TagLong)o;
			return (b.getData()==this.getData());
		}
		return false;
	}
	public void print(int indices)
	{
		System.out.println(getSpacing(indices) + "TAG_Long('" + this.getName() + "'): " + this.data);
	}

}

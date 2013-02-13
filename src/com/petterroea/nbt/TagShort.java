/*
 * TagShort.java
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

public class TagShort extends Tag {
	private short data = 0;
	public TagShort(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	public TagShort(String name, short s)
	{
		super(name);
		setData(s);
	}

	@Override
	public void read(DataInputStream is) throws IOException {
		// TODO Auto-generated method stub
		data = is.readShort();
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		// TODO Auto-generated method stub
		os.writeShort(data);
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return Tag.TAG_Short;
	}

	@Override
	public Tag copy() {
		// TODO Auto-generated method stub
		return new TagShort(this.getName(), data);
	}
	public short getData() {
		return data;
	}
	public void setData(short data) {
		this.data = data;
	}
	@Override
	public boolean equals(Object o)
	{
		if(super.equals(o))
		{
			TagShort b = (TagShort)o;
			return (b.getData()==this.getData());
		}
		return false;
	}
	public void print(int indices)
	{
		System.out.println(getSpacing(indices) + "TAG_Short('" + this.getName() + "'): " + this.data);
	}

}

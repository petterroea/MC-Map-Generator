/*
 * TagInt.java
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
 * A tag that has an int as payload
 * @author petterroea
 *
 */
public class TagInt extends Tag {
	private int data;
	/**
	 * A basic constructor that lets you set name of the tag
	 * @param name the name you want your tag to be named after.
	 */
	public TagInt(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	public TagInt(String name, int data)
	{
		super(name);
		this.data = data;
	}

	@Override
	public void read(DataInputStream is) throws IOException {
		data = is.readInt();
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeInt(data);
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return Tag.TAG_Int;
	}

	@Override
	public Tag copy() {
		// TODO Auto-generated method stub
		return new TagInt(this.getName(), data);
	}
	public int getData()
	{
		return data;
	}
	public void setData(int data)
	{
		this.data = data;
	}
	@Override
	public boolean equals(Object o)
	{
		if(super.equals(o))
		{
			TagInt b = (TagInt)o;
			return (b.getData()==this.getData());
		}
		return false;
	}
	public void print(int indices)
	{
		System.out.println(getSpacing(indices) + "TAG_Int('" + this.getName() + "'): " + this.data);
	}

}

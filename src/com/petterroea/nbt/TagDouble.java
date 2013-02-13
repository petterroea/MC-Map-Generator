/*
 * TagDouble.java
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
 * TagDouble - A tag that contains a double(decimal number).
 * @author petterroea
 *
 */
public class TagDouble extends Tag {
	private double data;
	/**
	 * Simple constructor
	 * @param name the name of the tag
	 */
	public TagDouble(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Simple constructor with name and data
	 * @param name name for the tag
	 * @param data data to be stored in the tag
	 */
	public TagDouble(String name, double data)
	{
		super(name);
		this.setData(data);
	}

	@Override
	public void read(DataInputStream is) throws IOException {
		data = is.readDouble();
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeDouble(data);
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return Tag.TAG_Double;
	}

	@Override
	public Tag copy() {
		// TODO Auto-generated method stub
		return new TagDouble(this.getName(), data);
	}
	/**
	 * Gives to you the data of this tag
	 * @return the double stored in the tag
	 */
	public double getData() {
		return data;
	}
	/**
	 * Sets what double this tag is storing
	 * @param data the double to store.
	 */
	public void setData(double data) {
		this.data = data;
	}
	@Override
	public boolean equals(Object o)
	{
		if(super.equals(o))
		{
			TagDouble b = (TagDouble)o;
			return (b.getData()==this.getData());
		}
		return false;
	}
	public void print(int indices)
	{
		System.out.println(getSpacing(indices) + "TAG_Double('" + this.getName() + "'): " + this.data);
	}

}

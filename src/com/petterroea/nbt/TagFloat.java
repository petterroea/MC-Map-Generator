/*
 * TagFloat.java
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
 * TagFloat - A tag that contains a single float value
 * @author petterroea
 *
 */
public class TagFloat extends Tag {
	private float data;
	/**
	 * Simple constructor with name
	 * @param name the name that this tag will be named with
	 */
	public TagFloat(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	/**
	 * Simple constructor with name and data
	 * @param name name for the tag
	 * @param data the data to be stored in the tag
	 */
	public TagFloat(String name, float data)
	{
		super(name);
		this.data = data;
	}

	@Override
	public void read(DataInputStream is) throws IOException {
		data = is.readFloat();
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeFloat(data);
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return Tag.TAG_Float;
	}

	@Override
	public Tag copy() {
		// TODO Auto-generated method stub
		return new TagFloat(this.getName(), data);
	}
	/**
	 * Gives you the data of this tag
	 * @return the float stored in this tag
	 */
	public float getData()
	{
		return data;
	}
	/**
	 * Sets the data stored in this tag.
	 * @param data the data to be put in the tag.
	 */
	public void setData(float data)
	{
		this.data = data;
	}
	@Override
	public boolean equals(Object o)
	{
		if(super.equals(o))
		{
			TagFloat b = (TagFloat)o;
			return (b.getData()==this.getData());
		}
		return false;
	}
	public void print(int indices)
	{
		System.out.println(getSpacing(indices) + "TAG_Float('" + this.getName() + "'): " + this.data);
	}

}
/*
 * TagIntArray.java
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

public class TagIntArray extends Tag {
	int[] data;
	public TagIntArray(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		data = new int[1];
	}
	public TagIntArray(String name, int[] data)
	{
		super(name);
		this.data = data;
	}

	@Override
	public void read(DataInputStream is) throws IOException {
		int l = is.readInt();
		data = new int[l];
		for(int i = 0; i < l; i++)
		{
			data[i]=is.readInt();
		}
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeInt(data.length);
		for(int i = 0; i < data.length; i++)
		{
			os.writeInt(data[i]);
		}
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return Tag.TAG_Int_Array;
	}

	@Override
	public Tag copy() {
		// TODO Auto-generated method stub
		int[] newData = new int[data.length];
		System.arraycopy(data, 0, newData, 0, data.length);
		return new TagIntArray(this.getName(), newData);
	}
	@Override
	public boolean equals(Object o)
	{
		if(super.equals(o))
		{
			TagIntArray b = (TagIntArray)o;
			return (b.data.equals(this.data));
		}
		return false;
	}
	public void print(int indices)
	{
		System.out.println(getSpacing(indices) + "TAG_Int_Array('" + this.getName() + "'): [" + this.data.length + " integers]");
	}

}

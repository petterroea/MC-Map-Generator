/*
 * TagString.java
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

public class TagString extends Tag {
	private String data;
	public TagString(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	public TagString(String name, String data)
	{
		super(name);
		this.data = data;
	}

	@Override
	public void read(DataInputStream is) throws IOException {
		data = is.readUTF();
	}

	@Override
	public void write(DataOutputStream os) throws IOException {
		os.writeUTF(data);
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return Tag.TAG_String;
	}

	@Override
	public Tag copy() {
		// TODO Auto-generated method stub
		return new TagString(this.getName(), data);
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	@Override
	public boolean equals(Object o)
	{
		if(super.equals(o))
		{
			TagString b = (TagString)o;
			return (b.getData().equals(this.getData()));
		}
		return false;
	}
	public void print(int indices)
	{
		System.out.println(getSpacing(indices) + "TAG_String('" + this.getName() + "'): '" + this.data + "'");
	}

}

/*
 * TagCompound.java
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
import java.util.HashMap;

public class TagCompound extends Tag {
	/**
	 * The map in which the tags are stored in
	 */
	private HashMap<String, Tag> map = new HashMap<String, Tag>();
	/**
	 * Simple constructor
	 * @param name the name of the tag
	 */
	public TagCompound(String name) {
		super(name);
	}
	/**
	 * Constructor for a compound without name
	 */
	public TagCompound()
	{
		super("");
	}

	@Override
	public void read(DataInputStream is) throws IOException {
		map.clear();
		Tag t = Tag.readNamedTag(is);
		while(t.getId()!=Tag.TAG_End)
		{
			map.put(t.getName(), t);
			t = Tag.readNamedTag(is);
		}
	}
	@Override
	public void write(DataOutputStream os) throws IOException {
		for(Tag t : map.values())
		{
			os.writeByte(t.getId());
			os.writeUTF(t.getName());
			t.write(os);
		}
		os.writeByte(Tag.TAG_End);
	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return Tag.TAG_Compound;
	}

	@Override
	public Tag copy() {
		TagCompound comp = new TagCompound(this.getName());
		for(String key : map.keySet())
		{
			comp.put(key, map.get(key).copy());
		}
		return comp;
	}
	public void put(Tag t)
	{
		map.put(t.getName(), t);
	}
	public void put(String name, Tag t)
	{
		map.put(name, t);
	}
	public Tag get(String key)
	{
		return map.get(key);
	}
	public boolean hasKey(String key)
	{
		return map.containsKey(key);
	}
	@Override
	public boolean equals(Object o)
	{
		if(super.equals(o))
		{
			TagCompound t = (TagCompound)o;
			return t.map.equals(this.map);
		}
		return false;
	}
	public void print(int indices)
	{
		if(this.getName().equals("")||this.getName()==null)
		{
			System.out.println(getSpacing(indices) + "TAG_Compound(None): " + map.size() + " entries");
		}
		else
		{
			System.out.println(getSpacing(indices) + "TAG_Compound('" + this.getName() + "'): " + map.size() + " entries");
		}
		System.out.println(getSpacing(indices)+"{");
		for(Tag t : map.values())
		{
			t.print(indices+4);
		}
		System.out.println(getSpacing(indices)+"}");
	}
}
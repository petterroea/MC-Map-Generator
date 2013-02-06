package com.petterroea.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class Tag {
	public static final byte TAG_End = 0;
    public static final byte TAG_Byte = 1;
    public static final byte TAG_Short = 2;
    public static final byte TAG_Int = 3;
    public static final byte TAG_Long = 4;
    public static final byte TAG_Float = 5;
    public static final byte TAG_Double = 6;
    public static final byte TAG_Byte_Array = 7;
    public static final byte TAG_String = 8;
    public static final byte TAG_List = 9;
    public static final byte TAG_Compound = 10;
    public static final byte TAG_Int_Array = 11;

	private String name;
	abstract void write(DataOutput out) throws IOException;
	abstract void read(DataInput in) throws IOException;
	public abstract String toString();
	public abstract byte getId();
	public Tag(String name)
	{
		if(name==null) 
		{
			this.name="";
		} 
		else 
		{
			this.name = name;
		}
	}
	public String getName()
	{
		if(name==null) return "";
		return name;
	}
	public void setName(String name)
	{
		if(name==null) this.name="";
		this.name=name;
	}
	@Override
	public boolean equals(Object obj)
	{
		if(obj==null || !(obj instanceof Tag)) return false;
		Tag t = (Tag) obj;
		if(t.getId()!=this.getId()) return false;
		if(name==null&&t.name != null || name != null && t.name == null) return false;
		if(name != null && !name.equals(t.name)) return false;
		return true;
	}
}
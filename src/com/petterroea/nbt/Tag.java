/*
 * Tag.java
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.InflaterOutputStream;

/**
 * Tag - base file for a Tag. Note that this class is not used for a tag that is inside a file. The extending classes do that.
 * @author petterroea
 *
 */
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
    
    /**
     * boolean stating if the file "level.dat" is compressed
     */
    public static final boolean FILE_LEVEL_DAT = true;
    /**
     * boolean stating if the file "player.dat" is compressed
     */
	public static final boolean FILE_PLAYER_DAT = true;
	/**
     * boolean stating if the file "idcounts.dat" is compressed
     */
	public static final boolean FILE_IDCOUNTS_DAT = false;
	/**
     * boolean stating if the file "villages.dat" is compressed
     */
	public static final boolean FILE_VILLAGES_DAT = true;
	//public static final boolean FILE_MAP_X_
	/**
     * boolean stating if the file "servers.dat" is compressed
     */
	public static final boolean FILE_SERVERS_DAT = false;
	/**
     * boolean stating if the file "scoreboard.dat" is compressed
     */
	public static final boolean FILE_SCOREBOARD_DAT = true;
	
	private String name;
	/**
	 * Called when we want to read to a tag
	 * @param is The data input stream used
	 * @throws IOException If something goes wrong
	 */
	public abstract void read(DataInputStream is) throws IOException;
	/**
	 * Called when we want to write to a tag(To the hard drive)
	 * @param os The data output stream used
	 * @throws IOException
	 */
	public abstract void write(DataOutputStream os) throws IOException;
	/**
	 * 
	 * @return The byte ID representing this tag type(See the TAG_* constants)
	 */
	public abstract byte getId();
	/**
	 * Copies the tag
	 * @return a copy with identical content to the tag
	 */
	public abstract Tag copy();
	/**
	 * Basic constructor with name
	 * @param name the name of the tag.
	 */
	public Tag(String name)
	{
		this.name = name;
	}
	/**
	 * Gives you the name of the tag.
	 * @return the tag name
	 */
	public String getName()
	{
		if(name==null) return "";
		return name;
	}
	/**
	 * Sets the name of the tag
	 * @param name the name you want to set the tag to
	 */
	public void setName(String name)
	{
		if(name==null) { this.name=""; return; }
		this.name=name;
	}
	/**
	 * Used for printing. Ignore
	 * @param indices
	 * @return As many spaces as the int <indices>
	 */
	protected String getSpacing(int indices)
	{
		String s = "";
		for(int i = 0; i < indices; i++)
		{
			s=s+" ";
		}
		return s;
	}
	/**
	 * Prints the tag to stdout
	 * @param indices Amount of spaces before the text
	 */
	public void print(int indices)
	{
		System.out.println(getSpacing(indices) + "(ERROR: The tag of id " + getId() + " does not have a print() function!)");
	}
	@Override
	public boolean equals(Object o)
	{
		if(o==null) return false;
		if(!(o instanceof Tag)) return false;
		Tag t = (Tag)o;
		if(t.getName()==null&&this.getName()!=null) return false;
		if(t.getName()!=null&&this.getName()==null) return false;
		if(!t.getName().equals(this.getName())) return false;
		if(t.getId()!=this.getId()) return false;
		return true;
	}
	/**
	 * Reads the next tag from the DataInputStream
	 * @param dis the input stream
	 * @return The tag read.
	 */
	public static Tag readNamedTag(DataInputStream dis)
	{
		try {
			byte b = dis.readByte();
			Tag t = null;
			if(b==TAG_End) { t = new TagEnd(); return t; }
			String name = dis.readUTF();
			if(b==TAG_Byte) t = new TagByte(name);
			if(b==TAG_Short) t = new TagShort(name);
			if(b==TAG_Int) t = new TagInt(name);
			if(b==TAG_Long) t = new TagLong(name);
			if(b==TAG_Float) t = new TagFloat(name);
			if(b==TAG_Double) t = new TagDouble(name);
			if(b==TAG_Byte_Array) t = new TagByteArray(name);
			if(b==TAG_String) t = new TagString(name);
			if(b==TAG_List) t = new TagList(name);
			if(b==TAG_Compound) t = new TagCompound(name);
			if(b==TAG_Int_Array) t = new TagIntArray(name);
			t.read(dis);
			return t;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	/**
	 * Use with caution, does not load data
	 * @param type The id of the tag we want a object of
	 * @param name The name of the tag we want a object of
	 * @return A tag of correct type according to "type", or null if the type is not of a tag.
	 */
	public static Tag getNewTag(byte type, String name) {
		if(type==TAG_End) return new TagEnd();
		if(type==TAG_Byte) return new TagByte(name);
		if(type==TAG_Short) return new TagShort(name);
		if(type==TAG_Int) return new TagInt(name);
		if(type==TAG_Long) return new TagLong(name);
		if(type==TAG_Float) return new TagFloat(name);
		if(type==TAG_Double) return new TagDouble(name);
		if(type==TAG_Byte_Array) return new TagByteArray(name);
		if(type==TAG_String) return new TagString(name);
		if(type==TAG_List) return new TagList<Tag>(name);
		if(type==TAG_Compound) return new TagCompound(name);
		if(type==TAG_Int_Array) return new TagIntArray(name);
 		return null;
	}
	/**
	 * Reads a compressed tag compound from the stream
	 * @param dis the DataInputStream
	 * @return a TagCompound, or null if the next tag in the stream is not a TagCompound
	 */
	public static TagCompound readGzipped(DataInputStream dis)
	{
		try {
			GZIPInputStream stream = new GZIPInputStream(dis);
			DataInputStream str = new DataInputStream(stream);
			byte type = str.readByte();
			if(type!=TAG_Compound) return null;
			String name = str.readUTF();
			TagCompound compound = new TagCompound(name);
			compound.read(str);
			return compound;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Reads a inflated tag compound from the stream
	 * @param dis the DataInputStream
	 * @return a TagCompound, or null if the next tag in the stream is not a TagCompound
	 */
	public static TagCompound readInflated(DataInputStream dis)
	{
		try {
			DataInputStream str = new DataInputStream(new InflaterInputStream(dis));
			byte type = str.readByte();
			if(type!=TAG_Compound) return null;
			String name = str.readUTF();
			TagCompound compound = new TagCompound(name);
			compound.read(str);
			return compound;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * Writes the tagCompound to a DataOutputStream, with zlib-compression
	 * @param os The output stream to write to
	 * @param tag The tag to be written
	 */
	public static void writeDeflated(DataOutputStream os, TagCompound tag)
	{
		try {
			DataOutputStream out = new DataOutputStream(new DeflaterOutputStream(os));
			out.writeByte((byte)TAG_Compound);
			out.writeUTF(tag.getName());
			tag.write(out);
			out.close();
		} catch(Exception e) { 
			e.printStackTrace();
		}
	}
	/**
	 * Writes the tagCompound to a DataOutputStream with GZip compression
	 * @param os The output stream
	 * @param tag The tag to be written
	 */
	public static void writeGzipped(DataOutputStream os, TagCompound tag)
	{
		try {
			DataOutputStream out = new DataOutputStream(new GZIPOutputStream(os));
			out.writeByte((byte)TAG_Compound);
			out.writeUTF(tag.getName());
			tag.write(out);
		} catch(Exception e) { 
			e.printStackTrace();
		}
	}
	/**
	 * Writes the tagCompound to a DataOutputStream.
	 * @param os the DataOutputStream to write to
	 * @param tag The tag to write
	 */
	public static void write(DataOutputStream os, TagCompound tag)
	{
		try {
			os.writeByte((byte)TAG_Compound);
			os.writeUTF(tag.getName());
			tag.write(os);
		} catch(Exception e) { 
			e.printStackTrace();
		}
	}
	/**
	 * Reads a file from the hard drive to a tag compound
	 * @param f the File to read from
	 * @param compressed true if the file is compressed. See the constants FILE_*.
	 * @return The root TagCompound from the file "f"
	 * @throws IOException if the file does not exist
	 */
	public static TagCompound readFile(File f, boolean compressed) throws IOException
	{
		if(!f.exists()) throw new IOException("The file specified cannot be found");
		DataInputStream is = null;
		if(compressed)
		{
			is = new DataInputStream(new GZIPInputStream(new FileInputStream(f)));
		}
		else
		{
			is = new DataInputStream(new FileInputStream(f));
		}
		byte type = is.readByte();
		if(type != TAG_Compound) return null;
		String name = is.readUTF();
		TagCompound tag = new TagCompound(name);
		tag.read(is);
		return tag;
	}
	/**
	 * Gives you the tag type name from the ID byte
	 * @param type the ID for the tag you want the name from
	 * @return The name of type of tag related to the ID type
	 */
	public static String getTagName(byte type) {
		if(type==TAG_End) return "tag_end";
		if(type==TAG_Byte) return "tag_byte";
		if(type==TAG_Short) return "tag_short";
		if(type==TAG_Int) return "tag_int";
		if(type==TAG_Long) return "tag_long";
		if(type==TAG_Float) return "tag_float";
		if(type==TAG_Double) return "tag_double";
		if(type==TAG_Byte_Array) return "tag_byte_array";
		if(type==TAG_String) return "tag_string";
		if(type==TAG_List) return "tag_list";
		if(type==TAG_Compound) return "tag_compound";
		if(type==TAG_Int_Array) return "tag_int_array";
		return "UNKNOWN";
	}
}

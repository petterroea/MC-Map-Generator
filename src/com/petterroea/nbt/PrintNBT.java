/*
 * PrintNBT.java
 * 
 * 1.0
 * 
 * 07 Feb 2013
 * 
 * Public domain
 */

package com.petterroea.nbt;

import java.io.File;
import java.io.IOException;

/**
 * Prints an NBT file to the console. Utility program ;)
 * Ignore it completely :)
 * @author petterroea
 *
 */
public class PrintNBT {

	public static void main(String[] args) {
		if(args.length!=1&&args.length!=2) { System.out.println("<NBT file> (-compressed)"); return;}
		File f = new File(args[0]);
		if(!f.exists()) { System.out.println("Nbt file does not exist!"); }
		boolean compressed = (args.length==2)&&args[1].equals("-compressed");
		try {
			TagCompound tag = Tag.readFile(f, compressed);
			tag.print(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

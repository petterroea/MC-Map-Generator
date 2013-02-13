/*
 * TagEnd.java
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
/**
 * This is just a placeholder. It doesn't really do anything special.
 * @author petterroea
 *
 */
public class TagEnd extends Tag {
	/**
	 * Constructor
	 */
	public TagEnd() {
		super(null);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void read(DataInputStream is) {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(DataOutputStream os) {
		// TODO Auto-generated method stub

	}

	@Override
	public byte getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Tag copy() {
		// TODO Auto-generated method stub
		return null;
	}

}

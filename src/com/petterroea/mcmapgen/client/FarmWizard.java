package com.petterroea.mcmapgen.client;

import java.io.IOException;

import com.petterroea.mcmapgen.McMapGen;

public class FarmWizard {

	public static void init() {
		System.out.println("Do you want to connect to a server?");
		String reply = "";
		try {
			reply = McMapGen.br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FarmHandler handler = null;
		if(reply.equalsIgnoreCase("y"))
		{
			handler = new MultiFarmHandler();
		}
		else
		{
			handler = new SingleFarmHandler();
		}
		handler.setup();
	}
}

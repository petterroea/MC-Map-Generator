package com.petterroea.mcmapgen;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import com.petterroea.mcmapgen.client.FarmWizard;
import com.petterroea.mcmapgen.server.ServerWizard;

public class McMapGen {
	public static BufferedReader br;
	public final static String VERSION = "1.1.0";
	public static void main(String[] args) {
		br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("===McMapGen " + VERSION + "===");
		System.out.println("Made by petterroea");
		//Add your name on this line if you do a pull request. That way, your name will be in the credits if you get accepted ;)
		System.out.println(""); 
		System.out.println("Released under GPL 3 licence found here: http://www.gnu.org/licenses/gpl.html");
		System.out.println("Go download it from http://github.com/petterroea/mcmapgen");
		System.out.println("If you paid for this software, you shouldnt. It is free, and someone is trying to make money of you and my free, open source program. I use GPL, tho, so i cant do anything about it.");
		System.out.println("");
		System.out.println("Startup wizard:");
		System.out.println("If you want to host a server(For farming the generation), type 1, press enter");
		System.out.println("If you want to run the generation only on your pc, type 2, press enter");
		System.out.println("Anything else closes the program");
		System.out.print("Choice: ");
		String in = "";
		try {
			in = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		if(in.equals("1"))
		{
			ServerWizard.init();
		}
		else if(in.equals("2"))
		{
			FarmWizard.init();
		}
		else
		{
			System.out.println("I'm sorry, but i don't understand what you wrote :(");
		}
	}

}
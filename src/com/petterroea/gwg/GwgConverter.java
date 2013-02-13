package com.petterroea.gwg;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GwgConverter {
	public static void main(String[] args)
	{
		if(args.length<1) displayHelpAndExit();
		if(args[0].equals("-raw"))
		{
			if(args.length != 5) { System.out.println("Invalid number of arguments"); displayHelpAndExit();}
			if(!isInt(args[3])||!isInt(args[4])) { System.out.println("Width or height is not a number!"); displayHelpAndExit();}
			File from = new File(args[1]);
			if(!from.exists()) { System.out.println("From does not exist!"); displayHelpAndExit();}
			File to = new File(args[2]);
			int w = Integer.parseInt(args[3]);
			int h = Integer.parseInt(args[4]);
			System.out.println("Width: " + w + ", height: " + h);
			fromRaw(from, to, w, h);
			return;
		}
		if(args.length<3) displayHelpAndExit();
		File f = new File(args[0]);
		if(!f.exists())
		{
			System.out.println("The specified file does not exist!");
			displayHelpAndExit();
		}
		boolean r = false;
		boolean g = false;
		boolean b = false;
		for(int i = 2; i < args.length; i++)
		{
			if(args[i].equalsIgnoreCase("-r")) r = true;
			if(args[i].equalsIgnoreCase("-g")) g = true;
			if(args[i].equalsIgnoreCase("-b")) b = true;
		}
		if(!r&&!g&&!b) displayHelpAndExit();
		if(f.getAbsolutePath().endsWith(".gwg")) convertToHeightmap(f, new File(args[1]), r, g, b);
		if(f.getAbsolutePath().endsWith(".png")) convertToGwg(f, new File(args[1]), r, g, b);
	}
	public static void fromRaw(File from, File to, int w, int h)
	{
		System.out.println("Converting raw to Gwg...");
		System.out.println(from.length() + " bytes to convert...");
		GwgByteFile f = new GwgByteFile(w, h);
		try {
			DataInputStream is = new DataInputStream(new FileInputStream(from));
			long startTime = System.currentTimeMillis();
			System.out.println("Starting...");
			long len = (long)w*(long)h;
			for(long l = 0; l <(long)w*(long)h; l++)
			{
				if(l%(1024L*1024L)==0L)
				{
					System.out.println("Converted " + ((l/(long)1024)/1024) + " Mb in " + ((System.currentTimeMillis()-startTime)/1000) + " seconds.");
				}
				f.set(l, is.readByte());
			}
			System.out.println("Saving...");
			f.save(new DataOutputStream(new FileOutputStream(to)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void convertToGwg(File from, File to, boolean r, boolean g, boolean b)
	{
		try {
			int colorChannels = 0;
			if(r) colorChannels++;
			if(g) colorChannels++;
			if(b) colorChannels++;
			BufferedImage img = ImageIO.read(from);
			GwgByteFile file = new GwgByteFile(img.getWidth(), img.getHeight());
			for(int i = 0; i < img.getWidth()*img.getHeight(); i++)
			{
				int color = 0;
				Color c = new Color(img.getRGB(i%img.getWidth(), i/img.getWidth()));
				if(r) color += c.getRed();
				if(g) color += c.getGreen();
				if(b) color += c.getBlue();
				color = color/colorChannels;
				file.set(i, (byte)color);
			}
			file.save(new DataOutputStream(new FileOutputStream(to)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void convertToHeightmap(File from, File to, boolean r, boolean g, boolean b)
	{
		try {
			GwgFile f = GwgFile.load(from);
			if(f instanceof GwgByteFile)
			{
				GwgByteFile bf = (GwgByteFile)f;
				BufferedImage img = new BufferedImage((int)bf.w, (int)bf.h, BufferedImage.TYPE_INT_RGB);
				for(int i = 0; i < bf.w*bf.h; i++)
				{
					int red = 0; if(r) red = bf.get(i)&0xFF;
					int green = 0; if(g) green = bf.get(i)&0xFF;
					int blue = 0; if(b) blue = bf.get(i)&0xFF;
					Color c = new Color(red, green, blue);
					img.setRGB((int)(i%bf.w), (int)(i/bf.w), c.getRGB());
				}
				ImageIO.write(img, "PNG", to);
			}
			else
			{
				System.out.println("This converter does not accept the file yet");
			}	
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static void displayHelpAndExit()
	{
		System.out.println("GwG Converter");
		System.out.println("");
		System.out.println(" [file] [to] (-r -g -b)");
		System.out.println("OR");
		System.out.println(" -raw [file] [to] [width] [height] <- Converts raw data to gwg.");
		System.out.println("");
		System.out.println("If the file is Gwg, it will convert to heightmap");
		System.out.println("If the file is not Gwg, it will try to convert to GwgByte");
		System.out.println("This only happens with PNG files.");
		System.out.println("");
		System.out.println("-r, -g, -b tells the program what channels to read/write to/from. Atleast one is needed. All three for reading all three channels");
		System.out.println("");
		System.out.println("Raw converter: specify source and destination file, then widt and height of the raw data ;)");
		System.exit(0);
	}
	public static boolean isInt(String s)
	{
		try {
			int i = Integer.parseInt(s);
			return true;
		} catch(Exception e) {
		}
		return false;
	}
}

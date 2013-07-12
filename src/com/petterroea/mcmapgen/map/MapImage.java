package com.petterroea.mcmapgen.map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.petterroea.util.GraphicsUtils;
import com.petterroea.util.MathUtils;

public class MapImage extends Map {
	private BufferedImage image;
	public MapImage(File file) {
		if(!file.getAbsolutePath().endsWith(".png")) throw new RuntimeException("Image file must be .png");
		try {
			System.out.println("Loading map...");
			image = ImageIO.read(file);
			w = image.getWidth();
			h = image.getHeight();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int get(int x, int y) {
		if(x<0||x>w||y<0||y>h) throw new RuntimeException("Coordinates outside of bounds");
		return MathUtils.average(GraphicsUtils.getRgb(image.getRGB(x, y)));
	}
	@Override
	public void set(int x, int y, int value) {
		if(x<0||x>w||y<0||y>h) throw new RuntimeException("Coordinates outside of bounds");// TODO Auto-generated method stub
		if(value<0||value>255) throw new RuntimeException("Value must be in the range 0-255");
		image.setRGB(x, y, GraphicsUtils.setRgb(0, value, value, value));
	}

}

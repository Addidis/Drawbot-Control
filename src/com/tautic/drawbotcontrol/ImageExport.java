/* Copyright (c) 2011 Colin Faulkingham
MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files 
(the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, 
publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, 
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

*/
// Modifications by Jayson Tautic 2011
package com.tautic.drawbotcontrol;
import java.awt.image.*;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.*;

public class ImageExport {
//	private static final long serialVersionUID = 1L;

	//public static void main(String[] args) {
	public static DrawBotImage ConvertToDBI(String fileName) {	
		try{
			BufferedImage img;
			DrawBotImage dbi = new DrawBotImage();
			
			dbi.version = 1; //File Version
			img = ImageIO.read(new File( fileName ));
			int w = img.getWidth();
			int h = img.getHeight();
			int[] imageData = new int[w*h];
			System.out.print("W:"+w+"xH:"+h);
			dbi.columns = w;
			dbi.rows = h;
			
			// default dbi properties- these are starting points and can be modified by the user before saving.
			dbi.drawingDelay = 2;
			dbi.drawingSpeed = 95;
			dbi.paperHeight = 2300;
			dbi.paperWidth = 1600;										
			
			for (int i=0; i<h; i++){
				//imageData.add(Byte.valueOf(front)); //Required?	
				System.out.println();
				for (int j=0; j<w; j++){   
					int pixel = img.getRGB(j, i);
					//int alpha = (pixel >> 24) & 0xff;
					int red = (pixel >> 16) & 0xff;
					int green = (pixel >> 8) & 0xff;
					int blue = (pixel) & 0xff;
					int med = (red+green+blue) / 3;
					int out = med / 26;
   					//imageData.add(out);
					//x + (y * dbi.columns)
					imageData[i + (j * w)] = out;
   					System.out.print(out);
					}	 			
    			} 
			dbi.imageData = imageData;
			return dbi;
    		}
    		catch(IOException e){System.out.println(e);}
			return null;
    	}
    	 
    }


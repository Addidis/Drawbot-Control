/*
 * Copyright (c) 2011 Jayson Tautic
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE 
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.tautic.drawbotcontrol;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DrawBotControl {

	public static DrawBotImage openDrawing(String fileName) {
		FileInputStream fs = null;
		ObjectInputStream obj = null;
		DrawBotImage dbi = null;
		try {
			fs = new FileInputStream(fileName);
			obj = new ObjectInputStream(fs);
			dbi = (DrawBotImage) obj.readObject();
			obj.close();
			return dbi;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} 	
	}

	public static void saveDrawing(String fileName, DrawBotImage dbi) {
		FileOutputStream fs = null;
		ObjectOutputStream obj = null;
		try {
			fs = new FileOutputStream(fileName);
			obj = new ObjectOutputStream(fs);
			obj.writeObject(dbi);
			obj.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}

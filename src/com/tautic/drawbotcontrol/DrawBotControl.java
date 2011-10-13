package com.tautic.drawbotcontrol;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

package com.tautic.drawbotcontrol;

import java.io.Serializable;
import java.util.ArrayList;

public class DrawBotImage implements Serializable {
	private static final long serialVersionUID = -4263997665563097690L;
	public char version;
	public int columns;
	public int rows;
	public int paperWidth;
	public int paperHeight;
	public char drawingSpeed;
	public char drawingDelay; 
	public ArrayList<Byte> imageData; //Contains image pixel data.
	
		public void setColumns(char MSB, char LSB) {
			columns = MSB >> 8 | LSB;
		}
		public void setRows(char MSB, char LSB) {
			rows = MSB >> 8 | LSB;
		}
		public void setPaperWidth(char MSB, char LSB) {
			paperWidth = MSB >> 8 | LSB;
		}
		public void setPaperHeight(char MSB, char LSB) {
			paperHeight = MSB >> 8 | LSB; 
		}
	
}

/*
[0x44][0x42][0x49][Version]
[ColumnsMSB][ColumnsLSB]
[RowsMSB][RowsLSB]
[PaperWidthMSB][PaperWidthLSB]
[PaperHeightMSB][PaperHeightLSB]
[DrawingSpeed]
[DrawingDelay]
[Future][Future]
*/
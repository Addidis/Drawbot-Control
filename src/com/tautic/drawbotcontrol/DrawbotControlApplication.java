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


import javax.imageio.ImageTypeSpecifier;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import gnu.io.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Composite;
import com.tautic.drawbotcontrol.DrawBotControl;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.PaintEvent;
public class DrawbotControlApplication implements net.Network_iface {

	protected Shell shell;
	private Text textPages;
	private Text textCharsPerPage;
	private String portName;
	private String portBaudRate = "9600";
	private String fileName; //Name of the opened drawing file
	static SerialPort serialPort;
	private static net.Network network;
	private Text textPaperWidth;
	private Text textPaperHeight;
	private static DrawBotImage dbi;

	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			DrawbotControlApplication window = new DrawbotControlApplication();
			network = new net.Network(0, new DrawbotControlApplication(), 255);
			window.open();		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		shell.addListener(SWT.Dispose, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (network.isConnected()) network.disconnect(); //If our port is open, close it.	
			}
		});
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
			
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {	
				
		shell = new Shell();
		shell.setSize(623, 729);
		shell.setText("Drawbot Control - v2.0");
		shell.setLayout(null);			
		
		final Canvas canvas = new Canvas(shell, SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				// set up the palette we'll be using for rendering previews. We're using a rough 10 color grayscale representation.
				Device device = Display.getCurrent ();
				Color c1 = new Color (device, 0, 0, 0);
				Color c2 = new Color (device, 25,25,25);
				Color c3 = new Color (device, 50, 50, 50);
				Color c4 = new Color (device, 75, 75, 75);
				Color c5 = new Color (device, 100, 100, 100);
				Color c6 = new Color (device, 125, 125, 125);
				Color c7 = new Color (device, 150, 150, 150);
				Color c8 = new Color (device, 175, 175, 175);
				Color c9 = new Color (device, 200, 200, 200);
				Color c10 = new Color (device, 255, 255, 255);
				Color[] palette = new Color[]{c1, c2, c3, c4, c5, c6, c7, c8, c9, c10};
				//Drawing code to render preview
				if (dbi != null) {
					for (int x = 1; x < dbi.rows; x++) {
						System.out.println();
						for (int y = 1; y < dbi.columns; y++) {
							int b = 0x00;
							b = dbi.imageData[x + (y * dbi.columns)]; 
							System.out.print(b);
							e.gc.setForeground(palette[b]); //Sets the foreground color based on the byte value.
							 
							if (b > 0) e.gc.drawPoint(y,x); 	
						}
					}
				}
				//Dispose of the color resources after use
				c1.dispose();
				c2.dispose();
				c3.dispose();
				c4.dispose();
				c5.dispose();
				c6.dispose();
				c7.dispose();
				c8.dispose();
				c9.dispose();
				c10.dispose();
			}
		});
		canvas.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		canvas.setBounds(382, 27, 200, 200);			
		
		Group grpSerialPortSelect = new Group(shell, SWT.NONE);
		grpSerialPortSelect.setForeground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND));
		grpSerialPortSelect.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		grpSerialPortSelect.setText("Serial Port Select");
		grpSerialPortSelect.setBounds(10, 10, 348, 57);
		
		final Combo comboPorts = new Combo(grpSerialPortSelect, SWT.NONE);
		comboPorts.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				portName = comboPorts.getText(); //set variable to selected port name.
			}
		});
		comboPorts.setBounds(86, 10, 248, 22);
		
		Label lblPort = new Label(grpSerialPortSelect, SWT.NONE);
		lblPort.setText("Port:");
		lblPort.setBounds(10, 14, 59, 14);
		
		Group grpImageFileSettings = new Group(shell, SWT.NONE);
		grpImageFileSettings.setForeground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND));
		grpImageFileSettings.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		grpImageFileSettings.setText("Image File Settings");
		grpImageFileSettings.setBounds(10, 407, 348, 254);
		
		Label lblCharsPerPage = new Label(grpImageFileSettings, SWT.NONE);
		lblCharsPerPage.setBounds(20, 224, 87, 18);
		lblCharsPerPage.setText("Bytes Per Page:");
		
		textCharsPerPage = new Text(grpImageFileSettings, SWT.BORDER);
		textCharsPerPage.setBounds(113, 221, 64, 19);
		textCharsPerPage.setText("128");
		
		Label lblPenType = new Label(grpImageFileSettings, SWT.NONE);
		lblPenType.setBounds(20, 191, 55, 15);
		lblPenType.setText("Pen Type:");
		
		Spinner spinnerPenType = new Spinner(grpImageFileSettings, SWT.BORDER);
		spinnerPenType.setEnabled(false);
		spinnerPenType.setBounds(111, 184, 47, 22);
		
		Label lblDrawingDelay1 = new Label(grpImageFileSettings, SWT.NONE);
		lblDrawingDelay1.setBounds(20, 146, 88, 14);
		lblDrawingDelay1.setText("Drawing Delay:");
		
		final Scale scaleDrawingDelay = new Scale(grpImageFileSettings, SWT.NONE);
		scaleDrawingDelay.setEnabled(false);
		scaleDrawingDelay.setBounds(113, 135, 192, 42);
		scaleDrawingDelay.setMaximum(127);
		scaleDrawingDelay.setMinimum(1);
		scaleDrawingDelay.setSelection(64);
		
		Label lblDrawingDelay = new Label(grpImageFileSettings, SWT.NONE);
		lblDrawingDelay.setEnabled(false);
		lblDrawingDelay.setBounds(311, 146, 33, 19);
		lblDrawingDelay.setText("64");
		lblDrawingDelay.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		
		final Label lblDrawingSpeed = new Label(grpImageFileSettings, SWT.NONE);
		lblDrawingSpeed.setBounds(311, 98, 33, 19);
		lblDrawingSpeed.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		lblDrawingSpeed.setText("64");
		
		final Scale scaleDrawingSpeed = new Scale(grpImageFileSettings, SWT.NONE);
		scaleDrawingSpeed.setBounds(113, 87, 192, 42);
		scaleDrawingSpeed.addMouseMoveListener(new MouseMoveListener() {						
			@Override
			public void mouseMove(MouseEvent arg0) {
				lblDrawingSpeed.setText(Integer.toString(scaleDrawingSpeed.getSelection()));				
			}
		});
		scaleDrawingSpeed.setMaximum(127);
		scaleDrawingSpeed.setMinimum(1);
		scaleDrawingSpeed.setSelection(64);
		
		Label lblCaddySpeed = new Label(grpImageFileSettings, SWT.NONE);
		lblCaddySpeed.setBounds(20, 98, 88, 14);
		lblCaddySpeed.setText("Drawing Speed:");
		
		Button btnCaddySet = new Button(grpImageFileSettings, SWT.NONE);
		btnCaddySet.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		btnCaddySet.setBounds(277, 217, 61, 28);
		btnCaddySet.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String scaleAmount = String.valueOf((char)scaleDrawingSpeed.getSelection());
				sendCommand("]" + scaleAmount);
				System.out.println(scaleAmount);
			}
		});
		btnCaddySet.setText("Set");
		
		Label lblPaperWidth = new Label(grpImageFileSettings, SWT.NONE);
		lblPaperWidth.setText("Paper Width:");
		lblPaperWidth.setBounds(20, 30, 87, 18);
		
		textPaperWidth = new Text(grpImageFileSettings, SWT.BORDER);
		textPaperWidth.setEnabled(false);
		textPaperWidth.setText("18");
		textPaperWidth.setBounds(113, 27, 64, 19);
		
		Label lblPaperHeight = new Label(grpImageFileSettings, SWT.NONE);
		lblPaperHeight.setText("Paper Height:");
		lblPaperHeight.setBounds(20, 57, 87, 18);
		
		textPaperHeight = new Text(grpImageFileSettings, SWT.BORDER);
		textPaperHeight.setEnabled(false);
		textPaperHeight.setText("24");
		textPaperHeight.setBounds(113, 54, 64, 19);
		
		Group grpTrimStepper = new Group(shell, SWT.NONE);
		grpTrimStepper.setForeground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND));
		grpTrimStepper.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		grpTrimStepper.setText("Trim");
		grpTrimStepper.setBounds(10, 68, 348, 333);
		
		TabFolder tabFolder_1 = new TabFolder(grpTrimStepper, SWT.NONE);
		tabFolder_1.setBounds(10, 24, 328, 299);
		
		TabItem tbtmSteppersTab = new TabItem(tabFolder_1, SWT.NONE);
		tbtmSteppersTab.setText("Steppers");
		
		Composite composite_2 = new Composite(tabFolder_1, SWT.NONE);
		composite_2.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		tbtmSteppersTab.setControl(composite_2);
		
		Label lblLeftStepper = new Label(composite_2, SWT.NONE);
		lblLeftStepper.setBounds(76, 15, 190, 14);
		lblLeftStepper.setAlignment(SWT.CENTER);
		lblLeftStepper.setText("Left Stepper");
		
		Label lblFine = new Label(composite_2, SWT.NONE);
		lblFine.setBounds(9, 60, 58, 14);
		lblFine.setText("Fine:");
		
		Button btnLeftUpFine = new Button(composite_2, SWT.NONE);
		btnLeftUpFine.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		btnLeftUpFine.setBounds(76, 43, 94, 28);
		btnLeftUpFine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				sendCommand("a"); //Up Left Fine
			}
		});
		btnLeftUpFine.setText("UP");
		
		Button btnRightUpFine = new Button(composite_2, SWT.NONE);
		btnRightUpFine.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		btnRightUpFine.setBounds(76, 75, 94, 28);
		btnRightUpFine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendCommand("d");
			}
		});
		btnRightUpFine.setText("UP");
		
		Button btnLeftDownFine = new Button(composite_2, SWT.NONE);
		btnLeftDownFine.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		btnLeftDownFine.setBounds(176, 43, 94, 28);
		btnLeftDownFine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendCommand("s");
			}
		});
		btnLeftDownFine.setText("DOWN");
		
		Button btnRightDownFine = new Button(composite_2, SWT.NONE);
		btnRightDownFine.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		btnRightDownFine.setBounds(176, 75, 94, 28);
		btnRightDownFine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendCommand("f");
			}
		});
		btnRightDownFine.setText("DOWN");
		
		Label lblRightStepper = new Label(composite_2, SWT.NONE);
		lblRightStepper.setBounds(77, 117, 190, 14);
		lblRightStepper.setText("Right Stepper");
		lblRightStepper.setAlignment(SWT.CENTER);
		
		Label label = new Label(composite_2, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(0, 136, 330, 2);
		
		Label label_1 = new Label(composite_2, SWT.NONE);
		label_1.setBounds(75, 141, 190, 14);
		label_1.setText("Left Stepper");
		label_1.setAlignment(SWT.CENTER);
		
		Label lblCoarse = new Label(composite_2, SWT.NONE);
		lblCoarse.setBounds(10, 179, 47, 14);
		lblCoarse.setText("Coarse:");
		
		Button btnLeftUpCoarse = new Button(composite_2, SWT.NONE);
		btnLeftUpCoarse.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		btnLeftUpCoarse.setBounds(75, 167, 94, 28);
		btnLeftUpCoarse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendCommand("q");
			}
		});
		btnLeftUpCoarse.setText("UP");
		
		Button btnRightUpCoarse = new Button(composite_2, SWT.NONE);
		btnRightUpCoarse.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		btnRightUpCoarse.setBounds(75, 198, 94, 28);
		btnRightUpCoarse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendCommand("e");
			}
		});
		btnRightUpCoarse.setText("UP");
		
		Button btnLeftDownCoarse = new Button(composite_2, SWT.NONE);
		btnLeftDownCoarse.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		btnLeftDownCoarse.setBounds(175, 167, 94, 28);
		btnLeftDownCoarse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendCommand("w");
			}
		});
		btnLeftDownCoarse.setText("DOWN");
		
		Button btnRightDownCoarse = new Button(composite_2, SWT.NONE);
		btnRightDownCoarse.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		btnRightDownCoarse.setBounds(175, 198, 94, 28);
		btnRightDownCoarse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendCommand("r");
			}
		});
		btnRightDownCoarse.setText("DOWN");
		
		Label label_2 = new Label(composite_2, SWT.NONE);
		label_2.setBounds(74, 238, 190, 14);
		label_2.setText("Right Stepper");
		label_2.setAlignment(SWT.CENTER);
		
		TabItem tbtmCaddyTab = new TabItem(tabFolder_1, SWT.NONE);
		tbtmCaddyTab.setText("Caddy");
		
		Composite composite_3 = new Composite(tabFolder_1, SWT.NONE);
		composite_3.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		tbtmCaddyTab.setControl(composite_3);
		
		Button btnUp = new Button(composite_3, SWT.NONE);
		btnUp.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		btnUp.setBounds(130, 85, 61, 28);
		btnUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendCommand("g");
			}
		});
		btnUp.setText("UP");
		
		Button btnLeft = new Button(composite_3, SWT.NONE);
		btnLeft.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		btnLeft.setBounds(44, 120, 61, 28);
		btnLeft.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendCommand("j");
			}
		});
		btnLeft.setText("LEFT");
		
		Button btnDown = new Button(composite_3, SWT.NONE);
		btnDown.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		btnDown.setBounds(130, 154, 61, 28);
		btnDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendCommand("h");
			}
		});
		btnDown.setText("DOWN");
		
		Button btnRight = new Button(composite_3, SWT.NONE);
		btnRight.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		btnRight.setBounds(211, 120, 61, 28);
		btnRight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendCommand("k");
			}
		});
		btnRight.setText("RIGHT");
		
		Button btnStart = new Button(shell, SWT.NONE);
		btnStart.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendCommand("p");
			}
		});
		btnStart.setBounds(376, 267, 68, 28);
		btnStart.setText("Start");
		
		Button btnPause = new Button(shell, SWT.NONE);
		btnPause.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		btnPause.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendCommand("p");
			}
		});
		btnPause.setBounds(450, 267, 68, 28);
		btnPause.setText("Pause");
		
		Button btnResume = new Button(shell, SWT.NONE);
		btnResume.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		btnResume.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendCommand("a");
			}
		});
		btnResume.setBounds(524, 267, 68, 28);
		btnResume.setText("Resume");
		
		Button btnUpload = new Button(shell, SWT.NONE);
		btnUpload.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		btnUpload.setBounds(394, 234, 75, 25);
		btnUpload.setText("Upload");
		
		Button btnDownload = new Button(shell, SWT.NONE);
		btnDownload.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		btnDownload.setEnabled(false);
		btnDownload.setBounds(475, 234, 94, 25);
		btnDownload.setText("Download");
		
		Label lblPages = new Label(shell, SWT.NONE);
		lblPages.setBounds(435, 634, 59, 14);
		lblPages.setText("Pages:");
		
		textPages = new Text(shell, SWT.BORDER);
		textPages.setEnabled(false);
		textPages.setBounds(518, 631, 64, 19);
		textPages.setText("128");
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("F&ile");
		
		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);
		
		MenuItem mntmOpenImage = new MenuItem(menu_1, SWT.NONE);
		mntmOpenImage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Open Drawing File");
				String[] filterExtensions = {"*.jpg"};
				fd.setFilterExtensions(filterExtensions);
				fileName = fd.open();
				System.out.println(fileName);
				 
				if( fileName != "" && fileName != null) 
				{
					dbi = new DrawBotImage();
					dbi = ImageExport.ConvertToDBI(fileName);
				}							
				canvas.redraw(); //Refresh the canvas so it renders our preview image
				}			
		});
		mntmOpenImage.setText("Open Image");
		
		new MenuItem(menu_1, SWT.SEPARATOR);
		
		MenuItem mntmopenDrawing = new MenuItem(menu_1, SWT.NONE);
		mntmopenDrawing.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//Open Drawing menu item
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Open Drawing File");
				String[] filterExtensions = {"*.dbi", "*.*"};
				fd.setFilterExtensions(filterExtensions);
				fileName = fd.open();
				System.out.println(fileName);
				 
				if( fileName != "" && fileName != null) dbi = DrawBotControl.openDrawing(fileName);
				
				//Update controls with new values from loaded file
				textPaperWidth.setText(String.valueOf(dbi.paperWidth));
				textPaperHeight.setText(String.valueOf(dbi.paperHeight));
				scaleDrawingDelay.setSelection(Integer.valueOf(dbi.drawingDelay));
				
				canvas.redraw();
			}
		});
		mntmopenDrawing.setText("Open Drawing");
		
		MenuItem mntmSaveDrawing = new MenuItem(menu_1, SWT.NONE);
		mntmSaveDrawing.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (dbi != null) {
					FileDialog fd = new FileDialog(shell, SWT.SAVE);
					fd.setText("Save Drawing File");
					String[] filterExtensions = {"*.dbi"};
					fd.setFilterExtensions(filterExtensions);
					fileName = fd.open();
					
					if (fileName != "" && fileName != null) DrawBotControl.saveDrawing(fileName, dbi);
				}
			}
		});
		mntmSaveDrawing.setText("Save Drawing");
		
		new MenuItem(menu_1, SWT.SEPARATOR);
		
		MenuItem mntmExit = new MenuItem(menu_1, SWT.NONE);
		mntmExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose(); // exit the application
			}
		});
		mntmExit.setText("E&xit");
		
		MenuItem mntmTools = new MenuItem(menu, SWT.CASCADE);
		mntmTools.setText("T&ools");
		
		Menu menu_2 = new Menu(mntmTools);
		mntmTools.setMenu(menu_2);
		
		MenuItem mntmPort = new MenuItem(menu_2, SWT.CASCADE);
		mntmPort.setText("Port");
		
		Menu menu_3 = new Menu(mntmPort);
		mntmPort.setMenu(menu_3);
		
		MenuItem mntmBaudRate = new MenuItem(menu_2, SWT.CASCADE);
		mntmBaudRate.setText("Baud Rate");
		
		Menu menu_4 = new Menu(mntmBaudRate);
		mntmBaudRate.setMenu(menu_4);
		
		MenuItem baud9600 = new MenuItem(menu_4, SWT.CHECK);
		baud9600.setSelection(true);
		baud9600.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				portBaudRate = "9600";
			}
		});
		baud9600.setText("9600");
		
		MenuItem baud38400 = new MenuItem(menu_4, SWT.CHECK);
		baud38400.setText("38400");
		
		MenuItem baud115200 = new MenuItem(menu_4, SWT.CHECK);
		baud115200.setText("115200");
		
		new MenuItem(menu_2, SWT.SEPARATOR);
		
		MenuItem mntmClearMemory = new MenuItem(menu_2, SWT.NONE);
		mntmClearMemory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendCommand("m");
			}
		});
		mntmClearMemory.setText("Clear Memory");
		
		//Get available ports on system
		@SuppressWarnings("unchecked")
		java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
		while ( portEnum.hasMoreElements())
		{
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			if (portIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) comboPorts.add(portIdentifier.getName());
		}
		
	}

	
	@Override
	public void writeLog(int id, String text) {
		// TODO Auto-generated method stub
		System.out.println(text);
		
	}

	@Override
	public void parseInput(int id, int numBytes, int[] message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void networkDisconnected(int id) {
		// TODO Auto-generated method stub
		
	}
	
	public void sendCommand(String cmd){		
		try {
			if (portName != null && portName != "") {
					if (!network.isConnected()) network.connect(portName, Integer.parseInt(portBaudRate)); //open the selected port if not already open
					network.writeSerial(cmd); //Send to port							
				}
			}
		catch (Exception e)
			{
				System.out.println(e.getMessage());
			}		
	}
}

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

import net.Network;

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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class DrawbotControlApplication implements net.Network_iface {

	protected Shell shell;
	private Text textPages;
	private Text textCharsPerPage;
	private String portName;
	private String portBaudRate = "9600";
	static SerialPort serialPort;
	private static net.Network network;

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
		shell.setSize(612, 590);
		shell.setText("Drawbot Control - v2.0");
		shell.setLayout(null);
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmFile = new MenuItem(menu, SWT.NONE);
		mntmFile.setText("File");
		
		Canvas canvas = new Canvas(shell, SWT.NONE);
		canvas.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GRAY));
		canvas.setBounds(382, 27, 200, 200);
		
		Group grpSerialPortSelect = new Group(shell, SWT.NONE);
		grpSerialPortSelect.setForeground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND));
		grpSerialPortSelect.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		grpSerialPortSelect.setText("Serial Port Select");
		grpSerialPortSelect.setBounds(10, 10, 348, 110);
		
		final Combo comboPorts = new Combo(grpSerialPortSelect, SWT.NONE);
		comboPorts.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				portName = comboPorts.getText(); //set variable to selected port name.
			}
		});
		comboPorts.setBounds(86, 24, 248, 22);
		
		final Combo comboBaudRate = new Combo(grpSerialPortSelect, SWT.NONE);
		comboBaudRate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				portBaudRate = comboBaudRate.getText(); //set variable to selected baud rate
			}
		});
		comboBaudRate.setItems(new String[] {"9600", "38400", "115200"});
		comboBaudRate.setBounds(86, 52, 89, 22);
		comboBaudRate.select(0);
		
		Label lblBaudRate = new Label(grpSerialPortSelect, SWT.NONE);
		lblBaudRate.setBounds(10, 56, 70, 14);
		lblBaudRate.setText("Baud Rate:");
		
		Label lblPort = new Label(grpSerialPortSelect, SWT.NONE);
		lblPort.setText("Port:");
		lblPort.setBounds(10, 28, 59, 14);
		
		Group grpImageFileSettings = new Group(shell, SWT.NONE);
		grpImageFileSettings.setForeground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND));
		grpImageFileSettings.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		grpImageFileSettings.setText("Image File Settings");
		grpImageFileSettings.setBounds(10, 126, 348, 110);
		
		textPages = new Text(grpImageFileSettings, SWT.BORDER);
		textPages.setText("128");
		textPages.setBounds(116, 21, 64, 19);
		
		textCharsPerPage = new Text(grpImageFileSettings, SWT.BORDER);
		textCharsPerPage.setText("128");
		textCharsPerPage.setBounds(116, 50, 64, 19);
		
		Label lblPages = new Label(grpImageFileSettings, SWT.NONE);
		lblPages.setBounds(10, 24, 59, 14);
		lblPages.setText("Pages:");
		
		Label lblCharsPerPage = new Label(grpImageFileSettings, SWT.NONE);
		lblCharsPerPage.setBounds(10, 53, 96, 14);
		lblCharsPerPage.setText("Chars Per Page:");
		
		Group grpTrimStepper = new Group(shell, SWT.NONE);
		grpTrimStepper.setForeground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND));
		grpTrimStepper.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		grpTrimStepper.setText("Trim");
		grpTrimStepper.setBounds(10, 242, 293, 231);
		
		Label label = new Label(grpTrimStepper, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(8, 120, 275, 2);
		
		Label lblLeftStepper = new Label(grpTrimStepper, SWT.NONE);
		lblLeftStepper.setAlignment(SWT.CENTER);
		lblLeftStepper.setBounds(67, 16, 190, 14);
		lblLeftStepper.setText("Left Stepper");
		
		Button btnLeftUpFine = new Button(grpTrimStepper, SWT.NONE);
		btnLeftUpFine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				sendCommand("a"); //Up Left Fine
			}
		});
		btnLeftUpFine.setBounds(67, 36, 94, 28);
		btnLeftUpFine.setText("UP");
		
		Button btnLeftDownFine = new Button(grpTrimStepper, SWT.NONE);
		btnLeftDownFine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendCommand("s");
			}
		});
		btnLeftDownFine.setBounds(167, 36, 94, 28);
		btnLeftDownFine.setText("DOWN");
		
		Label lblFine = new Label(grpTrimStepper, SWT.NONE);
		lblFine.setText("Fine:");
		lblFine.setBounds(10, 53, 108, 14);
		
		Label lblCoarse = new Label(grpTrimStepper, SWT.NONE);
		lblCoarse.setText("Coarse:");
		lblCoarse.setBounds(12, 156, 47, 14);
		
		Label label_1 = new Label(grpTrimStepper, SWT.NONE);
		label_1.setBounds(70, 125, 190, 14);
		label_1.setText("Left Stepper");
		label_1.setAlignment(SWT.CENTER);
		
		Label label_2 = new Label(grpTrimStepper, SWT.NONE);
		label_2.setBounds(69, 205, 190, 14);
		label_2.setText("Right Stepper");
		label_2.setAlignment(SWT.CENTER);
		
		Button btnNewButton_4 = new Button(grpTrimStepper, SWT.NONE);
		btnNewButton_4.setBounds(70, 144, 94, 28);
		btnNewButton_4.setText("UP");
		
		Button btnNewButton_5 = new Button(grpTrimStepper, SWT.NONE);
		btnNewButton_5.setBounds(170, 144, 94, 28);
		btnNewButton_5.setText("DOWN");
		
		Button btnNewButton_6 = new Button(grpTrimStepper, SWT.NONE);
		btnNewButton_6.setBounds(70, 175, 94, 28);
		btnNewButton_6.setText("UP");
		
		Button btnNewButton_7 = new Button(grpTrimStepper, SWT.NONE);
		btnNewButton_7.setBounds(170, 175, 94, 28);
		btnNewButton_7.setText("DOWN");
		
		Button btnRightUpFine = new Button(grpTrimStepper, SWT.NONE);
		btnRightUpFine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendCommand("d");
			}
		});
		btnRightUpFine.setBounds(67, 68, 94, 28);
		btnRightUpFine.setText("UP");
				
		Button btnRightDownFine = new Button(grpTrimStepper, SWT.NONE);
		btnRightDownFine.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				sendCommand("f");
			}
		});
		btnRightDownFine.setBounds(167, 68, 94, 28);
		btnRightDownFine.setText("DOWN");
		
		Label lblRightStepper = new Label(grpTrimStepper, SWT.NONE);
		lblRightStepper.setBounds(69, 101, 190, 14);
		lblRightStepper.setText("Right Stepper");
		lblRightStepper.setAlignment(SWT.CENTER);
		
		Group grpCaddyControl = new Group(shell, SWT.NONE);
		grpCaddyControl.setForeground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND));
		grpCaddyControl.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		grpCaddyControl.setText("Caddy Control");
		grpCaddyControl.setBounds(309, 242, 293, 231);
		
		final Label lblSpeed = new Label(grpCaddyControl, SWT.NONE);
		lblSpeed.setBounds(184, 164, 33, 19);
		lblSpeed.setFont(SWTResourceManager.getFont("Lucida Grande", 16, SWT.NORMAL));
		lblSpeed.setText("64");
		
		final Scale scale = new Scale(grpCaddyControl, SWT.NONE);
		scale.setBounds(8, 153, 170, 42);
		scale.addMouseMoveListener(new MouseMoveListener() {						
			@Override
			public void mouseMove(MouseEvent arg0) {
				lblSpeed.setText(Integer.toString(scale.getSelection()));				
			}
		});
		scale.setMaximum(127);
		scale.setMinimum(1);
		scale.setSelection(64);
		
		Button btnSet = new Button(grpCaddyControl, SWT.NONE);
		btnSet.setBounds(216, 158, 61, 28);
		btnSet.setText("Set");
		
		Label lblCaddySpeed = new Label(grpCaddyControl, SWT.NONE);
		lblCaddySpeed.setBounds(59, 122, 78, 14);
		lblCaddySpeed.setText("Caddy Speed");
		
		Button btnUp = new Button(grpCaddyControl, SWT.NONE);
		btnUp.setBounds(113, 32, 61, 28);
		btnUp.setText("UP");
		
		Button btnDown = new Button(grpCaddyControl, SWT.NONE);
		btnDown.setBounds(113, 82, 61, 28);
		btnDown.setText("DOWN");
		
		Button btnLeft = new Button(grpCaddyControl, SWT.NONE);
		btnLeft.setBounds(24, 57, 61, 28);
		btnLeft.setText("LEFT");
		
		Button btnRight = new Button(grpCaddyControl, SWT.NONE);
		btnRight.setBounds(200, 57, 61, 28);
		btnRight.setText("RIGHT");
		grpCaddyControl.setTabList(new Control[]{btnUp, btnLeft, btnRight, btnDown, scale, btnSet});
		
		Button btnStart = new Button(shell, SWT.NONE);
		btnStart.setBounds(168, 493, 94, 28);
		btnStart.setText("Start");
		
		Button btnPause = new Button(shell, SWT.NONE);
		btnPause.setBounds(263, 493, 94, 28);
		btnPause.setText("Pause");
		
		Button btnResume = new Button(shell, SWT.NONE);
		btnResume.setBounds(359, 493, 94, 28);
		btnResume.setText("Resume");
		
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
					network.connect(portName, Integer.parseInt(portBaudRate)); //open the selected port
					network.writeSerial(cmd); //Send to port		
					network.disconnect(); //close the port
				}
			}
		catch (Exception e)
			{
				System.out.println(e.getMessage());
			}		
	}
}

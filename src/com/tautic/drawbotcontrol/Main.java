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

public class Main {

	protected Shell shell;
	private Text textPages;
	private Text textCharsPerPage;
	private String portName;
	private String portBaudRate = "9600";
	static SerialPort serialPort;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Main window = new Main();
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
		shell.setSize(612, 543);
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
		grpTrimStepper.setBounds(10, 242, 293, 200);
		
		Label label = new Label(grpTrimStepper, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setBounds(6, 90, 275, 2);
		
		Label lblLeftStepper = new Label(grpTrimStepper, SWT.NONE);
		lblLeftStepper.setAlignment(SWT.CENTER);
		lblLeftStepper.setBounds(67, 9, 190, 14);
		lblLeftStepper.setText("Left Stepper");
		
		Button btnNewButton = new Button(grpTrimStepper, SWT.NONE);
		btnNewButton.setBounds(67, 26, 94, 28);
		btnNewButton.setText("UP");
		
		Button btnNewButton_1 = new Button(grpTrimStepper, SWT.NONE);
		btnNewButton_1.setBounds(67, 48, 94, 28);
		btnNewButton_1.setText("UP");
		
		Button btnNewButton_2 = new Button(grpTrimStepper, SWT.NONE);
		btnNewButton_2.setBounds(167, 26, 94, 28);
		btnNewButton_2.setText("DOWN");
		
		Button btnNewButton_3 = new Button(grpTrimStepper, SWT.NONE);
		btnNewButton_3.setBounds(167, 48, 94, 28);
		btnNewButton_3.setText("DOWN");
		
		Label lblFine = new Label(grpTrimStepper, SWT.NONE);
		lblFine.setText("Fine:");
		lblFine.setBounds(10, 43, 108, 14);
		
		Label lblCoarse = new Label(grpTrimStepper, SWT.NONE);
		lblCoarse.setText("Coarse:");
		lblCoarse.setBounds(10, 129, 108, 14);
		
		Label lblRightStepper = new Label(grpTrimStepper, SWT.NONE);
		lblRightStepper.setText("Right Stepper");
		lblRightStepper.setAlignment(SWT.CENTER);
		lblRightStepper.setBounds(69, 73, 190, 14);
		
		Label label_1 = new Label(grpTrimStepper, SWT.NONE);
		label_1.setBounds(68, 94, 190, 14);
		label_1.setText("Left Stepper");
		label_1.setAlignment(SWT.CENTER);
		
		Label label_2 = new Label(grpTrimStepper, SWT.NONE);
		label_2.setBounds(70, 158, 190, 14);
		label_2.setText("Right Stepper");
		label_2.setAlignment(SWT.CENTER);
		
		Button btnNewButton_4 = new Button(grpTrimStepper, SWT.NONE);
		btnNewButton_4.setBounds(67, 110, 94, 28);
		btnNewButton_4.setText("UP");
		
		Button btnNewButton_6 = new Button(grpTrimStepper, SWT.NONE);
		btnNewButton_6.setBounds(67, 133, 94, 28);
		btnNewButton_6.setText("UP");
		
		Button btnNewButton_7 = new Button(grpTrimStepper, SWT.NONE);
		btnNewButton_7.setBounds(167, 133, 94, 28);
		btnNewButton_7.setText("DOWN");
		
		Button btnNewButton_5 = new Button(grpTrimStepper, SWT.NONE);
		btnNewButton_5.setBounds(167, 110, 94, 28);
		btnNewButton_5.setText("DOWN");
		grpTrimStepper.setTabList(new Control[]{btnNewButton, btnNewButton_2, btnNewButton_1, btnNewButton_3, btnNewButton_4, btnNewButton_5, btnNewButton_6, btnNewButton_7});
		
		Group grpCaddyControl = new Group(shell, SWT.NONE);
		grpCaddyControl.setForeground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND));
		grpCaddyControl.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.NORMAL));
		grpCaddyControl.setText("Caddy Control");
		grpCaddyControl.setBounds(309, 242, 293, 200);
		
		final Label lblSpeed = new Label(grpCaddyControl, SWT.NONE);
		lblSpeed.setBounds(186, 142, 33, 19);
		lblSpeed.setFont(SWTResourceManager.getFont("Lucida Grande", 16, SWT.NORMAL));
		lblSpeed.setText("64");
		
		final Scale scale = new Scale(grpCaddyControl, SWT.NONE);
		scale.setBounds(10, 131, 170, 42);
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
		btnSet.setBounds(218, 136, 61, 28);
		btnSet.setText("Set");
		
		Label lblCaddySpeed = new Label(grpCaddyControl, SWT.NONE);
		lblCaddySpeed.setBounds(59, 122, 78, 14);
		lblCaddySpeed.setText("Caddy Speed");
		
		Button btnUp = new Button(grpCaddyControl, SWT.NONE);
		btnUp.setBounds(100, 20, 94, 28);
		btnUp.setText("UP");
		
		Button btnDown = new Button(grpCaddyControl, SWT.NONE);
		btnDown.setBounds(100, 70, 94, 28);
		btnDown.setText("DOWN");
		
		Button btnLeft = new Button(grpCaddyControl, SWT.NONE);
		btnLeft.setBounds(11, 46, 94, 28);
		btnLeft.setText("LEFT");
		
		Button btnRight = new Button(grpCaddyControl, SWT.NONE);
		btnRight.setBounds(187, 46, 94, 28);
		btnRight.setText("RIGHT");
		grpCaddyControl.setTabList(new Control[]{btnUp, btnLeft, btnRight, btnDown, scale, btnSet});
		
		Button btnStart = new Button(shell, SWT.NONE);
		btnStart.setBounds(169, 483, 94, 28);
		btnStart.setText("Start");
		
		Button btnPause = new Button(shell, SWT.NONE);
		btnPause.setBounds(264, 483, 94, 28);
		btnPause.setText("Pause");
		
		Button btnResume = new Button(shell, SWT.NONE);
		btnResume.setBounds(360, 483, 94, 28);
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
}

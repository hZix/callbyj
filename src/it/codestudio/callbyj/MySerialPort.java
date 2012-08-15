/*
 *
 * CallByJ
 * Copyright (C)2012-2012, Sandro Salari
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package it.codestudio.callbyj;

import gnu.io.CommPortIdentifier;
import gnu.io.NativeResourceException;
import gnu.io.PortInUseException;
import gnu.io.RXTXCommDriver;
import gnu.io.RXTXPort;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TooManyListenersException;

// TODO: Auto-generated Javadoc
/**
 * * The Class MySerialPort.
 *
 * @author  Xandros ( sandro.salari AT gmail.com )
 * @author Last changed by: Xandros
 * @version 15-ago-2012
 * @since 1.0
 */
public class MySerialPort {
	
	/**
	 * Gets the available serial ports.
	 *
	 * @return the available serial ports
	 */
	public static Set<String> getAvailableSerialPorts() {
		Set<String> available=new HashSet<String>();
		try{
			RXTXCommDriver d = new RXTXCommDriver();
			Set<String> av=d.getPortIdentifiers();
			ArrayList<String> strs = new ArrayList<String>();
			for(String s:av) {
				strs.add(0, s);
			}
			for(String s:strs) {
				available.add(s);
			}
		}catch( UnsatisfiedLinkError e){
			e.printStackTrace();
			throw new NativeResourceException(e.getMessage());
		}  

		return available;
	}
	
	/** The serial. */
	private RXTXPort serial;
	
	/** The port. */
	private String port=null;
	
	/** The connected. */
	private boolean connected=false;
	
	/** The baud. */
	private int baud = 115200;
	
	/**
	 * Instantiates a new my serial port.
	 *
	 * @param port the port
	 * @param baud the baud
	 */
	public MySerialPort(String port, int baud) {
		setPort(port);
		setBaud(baud);
	}
	
	/**
	 * Adds the event listener.
	 *
	 * @param lsnr the lsnr
	 * @throws TooManyListenersException the too many listeners exception
	 */
	public void addEventListener(SerialPortEventListener lsnr) throws TooManyListenersException{
		serial.addEventListener(lsnr);
	}
	
	/**
	 * Connect.
	 *
	 * @return true, if successful
	 */
	public boolean connect() {
		if(isConnected()) {
			System.err.println(port + " is already connected.");
			return true;
		}

		try 
		{
			RXTXPort comm = null;
			CommPortIdentifier ident = null;
			if((System.getProperty("os.name").toLowerCase().indexOf("linux")!=-1)){
				if (port.contains("rfcomm")||port.contains("ttyUSB") ||port.contains("ttyS")|| port.contains("ACM") || port.contains("Neuron_Robotics")||port.contains("NR")||port.contains("FTDI")||port.contains("ftdi")){
					System.setProperty("gnu.io.rxtx.SerialPorts", port);
				}
			}
			ident = CommPortIdentifier.getPortIdentifier(port);

			try{
				comm = ident.open("NRSerialPort", 2000);
			}catch (PortInUseException e) {
				System.err.println("This is a bug, passed the ownership test above: " + e.getMessage());
				return false;
			}

			if ( !(comm instanceof RXTXPort) ) {
				throw new UnsupportedCommOperationException("Non-serial connections are unsupported.");
			}

			serial = comm;
			serial.enableReceiveTimeout(100);
			serial.setSerialPortParams(getBaud(), SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);                              
			setConnected(true);
		}catch(NativeResourceException e){
			throw new NativeResourceException(e.getMessage());
		}catch (Exception e) {
			System.err.println("Failed to connect on port: "+port+" exception: ");
			e.printStackTrace();
			setConnected(false);
		}

		if(isConnected()) {
			serial.notifyOnDataAvailable(true);
		}
		return isConnected();   
	}
	
	/**
	 * Disconnect.
	 */
	public void disconnect() {
		try{
			try{
				getInputStream().close();
				getOutputStream().close();
				serial.close();
			}catch(Exception e){
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			serial = null;
			setConnected(false);
		} catch(UnsatisfiedLinkError e) {
			throw new NativeResourceException(e.getMessage());
		}
	}


	/**
	 * Gets the baud.
	 *
	 * @return the baud
	 */
	public int getBaud() {
		return baud;
	}
	
	/**
	 * Gets the input stream.
	 *
	 * @return the input stream
	 */
	public InputStream getInputStream()
	{
		return serial.getInputStream();
	}


	/**
	 * Gets the output stream.
	 *
	 * @return the output stream
	 */
	public OutputStream getOutputStream()
	{
		return serial.getOutputStream();
	}
	
	/**
	 * Checks if is connected.
	 *
	 * @return true, if is connected
	 */
	public boolean isConnected(){
		return connected;
	}
	
	/**
	 * Notify on data available.
	 *
	 * @param b the b
	 */
	public void notifyOnDataAvailable(boolean b) {
		serial.notifyOnDataAvailable(b);
	}
	
	/**
	 * Sets the baud.
	 *
	 * @param baud the new baud
	 */
	public void setBaud(int baud) {
		switch(baud){
		case   2400:
		case   4800:
		case   9600:
		case  14400:
		case  19200:
		case  28800:
		case  38400:
		case  57600:
		case  76800:
		case 115200:
		case 230400:
			this.baud = baud;
			return;
		default:
			throw new RuntimeException("Invalid baudrate! "+baud);
		}
	}
	
	/**
	 * Sets the connected.
	 *
	 * @param connected the new connected
	 */
	public void setConnected(boolean connected) {
		if(this.connected == connected)
			return;
		this.connected = connected;
	}
	
	/**
	 * Sets the port.
	 *
	 * @param port the new port
	 */
	private void setPort(String port) {
		this.port = port;
	}
}


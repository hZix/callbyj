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

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.DataInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * * The Class SerialCommandReader.
 *
 * @author  Xandros ( sandro.salari AT gmail.com )
 * @author Last changed by: Xandros
 * @version 15-ago-2012
 * @since 1.0
 */
public class SerialCommandReader implements SerialPortEventListener{

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(SerialCommandReader.class);

	/** The in. */
	private InputStream in;

	/** The manager. */
	private ComManager manager;

	/** The buffer. */
	private byte[] buffer = new byte[1024];

	/**
	 * Instantiates a new serial command reader.
	 *
	 * @param in the in
	 * @param manager the manager
	 * @throws Exception the exception
	 */
	public SerialCommandReader ( DataInputStream in , ComManager manager) throws Exception {
		this.in = in;
		this.manager = manager;
	}

	/* (non-Javadoc)
	 * @see gnu.io.SerialPortEventListener#serialEvent(gnu.io.SerialPortEvent)
	 */
	@Override
	public void serialEvent(SerialPortEvent arg0) {
		int data;
		try	{
			int len = 0;
			while ( ( data = in.read()) > -1 ){
				buffer[len++] = (byte) data;
				if ( data == '\n' || data == '\r') {
					break;
				}				
			}
			String commandResponse = new String(buffer,0,len);
			if(!commandResponse.trim().isEmpty()){
				manager.processResponse(commandResponse.trim());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}  

	}

}

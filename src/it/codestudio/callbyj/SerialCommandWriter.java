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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * * The Class SerialCommandWriter.
 *
 * @author  Xandros ( sandro.salari AT gmail.com )
 * @author Last changed by: Xandros
 * @version 15-ago-2012
 * @since 1.0
 */
public class SerialCommandWriter implements Runnable 
{
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(SerialCommandWriter.class);
	
	/** The running. */
	private volatile boolean running = true;
	
	/** The out. */
	OutputStream out;
	
	/** The in. */
	DataInputStream in;

	/**
	 * Instantiates a new serial command writer.
	 *
	 * @param out the out
	 * @param input the input
	 * @throws Exception the exception
	 */
	public SerialCommandWriter ( DataOutputStream out , InputStream input) throws Exception {
		this.out = out;
		this.in = new DataInputStream(input);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run ()
	{
		try{ 
			int c = 0;
			while (running && ( c = this.in.read()) > -1 ){				
					this.out.write(c);		
					synchronized (this) {
						wait(10);
					}
			} 
			this.in.close();
			logger.info("SerialCommandWriter finished");
		}catch ( Exception e ){
			logger.error(e.getMessage(),e);
		}       
	}

	/**
	 * Terminate.
	 */
	public void terminate(){
		running = false;
	}
}


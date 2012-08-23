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

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * * The Class Utils.
 *
 * @author  Xandros ( sandro.salari AT gmail.com )
 * @author Last changed by: Xandros
 * @version 15-ago-2012
 * @since 1.0
 */
public class Utils {
	
	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(Utils.class);

	/**
	 * Fitler null string.
	 *
	 * @param array the array
	 * @return the string[]
	 */
	public static String[] fitlerNullString(String[] array) {
		List<String> list = new ArrayList<String>();
	    for(String c : array) {
	       if(c != null && c.length() > 0) {
	          list.add(c);
	       }
	    }				    
	    return list.toArray(new String[list.size()]);
	}
	
	/**
	 * Gain samle.
	 *
	 * @param bytes the bytes
	 * @return the byte[]
	 */
	public static byte[] gainSample(byte[] bytes, double factor) {
		short[] out = new short[bytes.length/2];
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		bb.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < out.length; i++) {
			out[i] = (short) (bb.getShort() * factor);
		}
		ByteBuffer a = ByteBuffer.allocate(bytes.length);
		a.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < out.length; i++) {
			a.putShort(out[i]);
		}
		return a.array();
	}
	
	/**
	 * Generate random array.
	 *
	 * @return the byte[]
	 */
	public static byte[] generateRandomArray() {
		int size = 20000;
		byte[] byteArray = new byte[size];
		for (int i = 0; i < size; i++) {
			byteArray[i] = (byte) (Math.random() * 127f);
		}

		return byteArray;
	}
	
	/**
	 * Gets the all threads.
	 *
	 * @return the all threads
	 */
	private static Thread[] getAllThreads( ) {
	    final ThreadGroup root = getRootThreadGroup( );
	    final ThreadMXBean thbean = ManagementFactory.getThreadMXBean( );
	    int nAlloc = thbean.getThreadCount( );
	    int n = 0;
	    Thread[] threads;
	    do {
	        nAlloc *= 2;
	        threads = new Thread[ nAlloc ];
	        n = root.enumerate( threads, true );
	    } while ( n == nAlloc );
	    return java.util.Arrays.copyOf( threads, n );
	}
	
	/**
	 * Gets the root thread group.
	 *
	 * @return the root thread group
	 */
	private static ThreadGroup getRootThreadGroup( ) {
	    ThreadGroup tg = Thread.currentThread( ).getThreadGroup( );
	    ThreadGroup ptg;
	    while ( (ptg = tg.getParent( )) != null )
	        tg = ptg;
	    return tg;
	}
	
	/**
	 * Gets the thread.
	 *
	 * @param name the name
	 * @return the thread
	 */
	public static Thread getThread( final String name ) {
	    if ( name == null )
	        throw new NullPointerException( "Null name" );
	    final Thread[] threads = getAllThreads( );
	    for ( Thread thread : threads )
	        if ( thread.getName( ).equals( name ) )
	            return thread;
	    return null;
	}
}

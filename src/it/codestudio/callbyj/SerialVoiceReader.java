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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.SourceDataLine;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * * The Class SerialVoiceReader.
 *
 * @author  Xandros ( sandro.salari AT gmail.com )
 * @author Last changed by: Xandros
 * @version 15-ago-2012
 * @since 1.0
 */
public class SerialVoiceReader implements Runnable{

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(SerialVoiceReader.class);

	/** The running. */
	private volatile boolean running = true;

	/** The in. */
	private DataInputStream in;

	/** The af. */
	private AudioFormat af;

	/** The samples per frame. */
	private int samplesPerFrame = 160;

	/** The audio buffer size. */
	private int audioBufferSize = samplesPerFrame * 2 * 3; //60ms delay

	/**
	 * Instantiates a new serial voice reader.
	 *
	 * @param in the in
	 * @param af the af
	 */
	public SerialVoiceReader ( DataInputStream in,  AudioFormat af){
		this.in = in;
		this.af = af;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run (){
		try
		{
			Info infos = new Info(SourceDataLine.class, af);
			SourceDataLine dataLine  = (SourceDataLine) AudioSystem.getLine(infos);
			dataLine.open(dataLine.getFormat(),audioBufferSize);						
			dataLine.start();			
			while (running){	
				int av = dataLine.available();
				if(av >= 0){
					byte[] buffer = new byte[av];
					int bytesReadFromSerial = this.in.read(buffer,0,av);
					if(bytesReadFromSerial > -1){
						AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(buffer),af,av/2);
						byte[] audioBuffer = new byte[av];
						int bytesToWriteToAudioLine = 0;
						while (bytesToWriteToAudioLine != -1) { 
							bytesToWriteToAudioLine = audioInputStream.read(audioBuffer, 0, 2);
							if (bytesToWriteToAudioLine >= 0){
								dataLine.write(audioBuffer, 0, bytesToWriteToAudioLine);
							}
						}
						audioInputStream.close();
						audioInputStream = null; 
					}
				}
			}
			dataLine.drain();
			dataLine.flush();
			dataLine.stop();
			dataLine.close();
			dataLine = null;
			logger.debug("Terminated");
		}
		catch ( Exception e )
		{
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
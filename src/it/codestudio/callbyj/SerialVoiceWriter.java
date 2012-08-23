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
import java.io.DataOutputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.TargetDataLine;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * * The Class SerialVoiceWriter.
 *
 * @author  Xandros ( sandro.salari AT gmail.com )
 * @author Last changed by: Xandros
 * @version 15-ago-2012
 * @since 1.0
 */
public class SerialVoiceWriter implements Runnable 
{

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(SerialVoiceWriter.class);

	/** The play message. */
	private boolean playMessage = false;

	/** The running. */
	private volatile boolean running = true;

	/** The out. */
	DataOutputStream out;

	/** The af. */
	AudioFormat af;

	/** The samples per frame. */
	private int samplesPerFrame = 160;

	/** The audio buffer size. */
	private int audioBufferSize = samplesPerFrame * 3 * 2; //60ms delay

	/**
	 * Instantiates a new serial voice writer.
	 *
	 * @param out the out
	 * @param af the af
	 * @param playMessage the play message
	 */
	public SerialVoiceWriter ( DataOutputStream out, AudioFormat af, Boolean playMessage)
	{
		this.out = out;
		this.af = af;
		this.playMessage = playMessage;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run ()
	{	
		try
		{   
			String testFileName = "ThisIsATest.raw";
			if(playMessage){
				InputStream file = this.getClass().getResourceAsStream(testFileName);
				while (running){
					if(file != null){
						int offset = 0;
						byte[] buffer = new byte[320];
						if((offset = file.read(buffer,offset,320)) > 0){
							AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(buffer),af,320);	
							byte[] buffer2 = new byte[320];
							while ( (audioInputStream.read(buffer2)) > 0 ){
								this.out.write(buffer2);
							} 
							audioInputStream.close();
							audioInputStream = null; 
						}else{
							file.close();
							file = this.getClass().getResourceAsStream(testFileName);
						}
					}
					Thread.sleep(20);
				}
			}else{
				Info infos = new Info(TargetDataLine.class, af);
				TargetDataLine dataLine  = (TargetDataLine) AudioSystem.getLine(infos);
				dataLine.open(dataLine.getFormat(),audioBufferSize);
				dataLine.start();	
				Thread.sleep(60);
				while (running){
					int av = dataLine.available();
					if(av > 0){
						//Fill buffer until byte are available on dataLine (sometime due to line delay not all byte are available on first read)
						byte[] audioBuffer = new byte[av];
						int offset = 0;
						int numRead = 0;
						while (offset < audioBuffer.length && (numRead = dataLine.read(audioBuffer, offset, audioBuffer.length - offset)) >= 0) {
							offset += numRead;
						}
						this.out.write(audioBuffer,0,offset);
					}
				}
				dataLine.drain();
				dataLine.stop();
				dataLine.close();
				dataLine = null;
			}

		}
		catch (Exception e )
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

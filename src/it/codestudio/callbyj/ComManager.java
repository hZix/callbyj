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
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * * The Class ComManager.
 *
 * @author  Xandros ( sandro.salari AT gmail.com )
 * @author Last changed by: Xandros
 * @version 15-ago-2012
 * @since 1.0
 */
public class ComManager
{

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(ComManager.class);

	/**
	 * Gets the single instance of ComManager.
	 *
	 * @param commandPortName the command port name
	 * @param streamPortName the stream port name
	 * @param playMessage the play message
	 * @return single instance of ComManager
	 */
	public static ComManager getInstance(String commandPortName, String streamPortName, Boolean playMessage){
		try{
			if(ComManager.instance == null){
				ComManager.instance = new ComManager();
				ComManager.instance.setCommandPortName(commandPortName);
				ComManager.instance.setStreamPortNmae(streamPortName);
				ComManager.instance.setPlayMessage(playMessage);
				//ComManager.instance.startCall();
			}
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		return ComManager.instance;
	}

	/** The voice stream comm port. */
	private MySerialPort voiceStreamCommPort;

	/** The Constant af. */
	private static final AudioFormat af = new  AudioFormat(Encoding.PCM_SIGNED , 8000, 16, 1, 2, 8000, false);

	/** The stream port nmae. */
	private String streamPortNmae;

	/** The command port name. */
	private String commandPortName;

	/** The play message. */
	private Boolean playMessage;

	/** The buffer. */
	private final int BUFFER = 2048;

	/** The write pout. */
	private PipedOutputStream writePout;

	/** The write pin. */
	private PipedInputStream writePin;

	/** The read pout. */
	private PipedOutputStream readPout;

	/** The read pin. */
	private PipedInputStream readPin;

	/** The read dis. */
	private DataInputStream readDis;

	/** The write dos. */
	private DataOutputStream writeDos;

	/** The incoming call. */
	private Boolean incomingCall = false;

	/** The pin ok. */
	private Boolean pinOk = false;

	/** The instance. */
	private static ComManager instance;

	/** The prev req command. */
	private ATCommands prevReqCommand;

	/** The pin. */
	private String pin;

	/** The serial voice reader. */
	private SerialVoiceReader serialVoiceReader;

	/** The serial voice writer. */
	private SerialVoiceWriter serialVoiceWriter;

	/** The serial command reader. */
	private SerialCommandReader serialCommandReader;

	/** The serial command writer. */
	private SerialCommandWriter serialCommandWriter;

	/** The modem com port. */
	private MySerialPort modemComPort;

	/** The voice input stream. */
	private DataInputStream voiceInputStream;

	/** The voice output stream. */
	private DataOutputStream voiceOutputStream;

	/** The command input stream. */
	private DataInputStream commandInputStream;

	/** The command output stream. */
	private DataOutputStream commandOutputStream;

	/**
	 * Instantiates a new com manager.
	 *
	 * @throws Exception the exception
	 */
	private ComManager() throws Exception
	{
		super();
		writePin = new PipedInputStream(BUFFER);
		writePout = new PipedOutputStream(writePin);
		writeDos = new DataOutputStream(writePout);

		readPin = new PipedInputStream(BUFFER);
		readPout = new PipedOutputStream(readPin);	
		readDis = new DataInputStream(readPin);
	}

	/**
	 * End call.
	 *
	 * @throws Exception the exception
	 */
	private void endCall () throws Exception {
		this.incomingCall = false;
		if(serialVoiceReader != null){
			serialVoiceReader.terminate();
			serialVoiceReader = null;
		}
		if(serialVoiceWriter != null){
			serialVoiceWriter.terminate();
			serialVoiceWriter = null;
		}
		if(voiceInputStream != null){
			voiceInputStream.close();
		}
		if(voiceOutputStream != null){
			voiceOutputStream.close();
		}
		if(voiceStreamCommPort != null){
			voiceStreamCommPort.disconnect();
			voiceStreamCommPort = null;
		}
		//Thread jsed = Utils.getThread("Java Sound Event Dispatcher");

	}

	/**
	 * Inits the command.
	 *
	 * @throws Exception the exception
	 */
	private void initCommand() throws Exception{
		this.sendCommandToModem(ATCommands.ENABLE_HANGUP);
		this.sendCommandToModem(ATCommands.ENABLE_CALL_INFO);
		this.sendCommandToModem(ATCommands.ENABLE_RING_INFO);
		this.sendCommandToModem(ATCommands.ATTENTION);
	}

	/**
	 * Insert pin.
	 *
	 * @param pin the pin
	 * @throws Exception the exception
	 */
	public void insertPin(String pin) throws Exception{
		this.pin = pin;
		this.sendCommandToModem(ATCommands.CHECK_PIN);
	}

	/**
	 * Process response.
	 *
	 * @param response the response
	 * @throws Exception the exception
	 */
	public void processResponse(String response) throws Exception{

		logger.info("Received data from serial port: " + response.trim());

		if(response.equals(ATCommandReponses.CALL_ESTABLISHED.getModemResponseString())){
			//Start communication now only for incoming call, call started by this client start after number composition to hear ring sequence
			if(this.incomingCall){
				this.startCall();	
				this.sendCommandToModem(ATCommands.OPEN_VOICE_STREAM);	
			}
		}
		if(response.contains(ATCommandReponses.INCOMING_CALL.getModemResponseString())){
			this.incomingCall = true;
		}
		if(response.contains(ATCommandReponses.INCOMING_CALL_INFO.getModemResponseString())){
			String incomingCallNumbe = response.substring(response.indexOf(":"), response.indexOf(","));
			logger.info("Incoming call from number " + incomingCallNumbe);
		}
		if(response.contains(ATCommandReponses.CALL_ENDED.getModemResponseString())){
			logger.info("Call ended");
			this.endCall();
		}		
		if(response.contains(ATCommandReponses.PIN_NEEDED.getModemResponseString())){
			logger.info("PIN is needed");
			if(this.pin != null && !this.pin.isEmpty()){
				this.sendCommandToModem(ATCommands.INSERT_PIN,this.pin);
				this.sendCommandToModem(ATCommands.CHECK_PIN);
			}
		}

		if(response.equals(ATCommandReponses.PIN_CORRECT.getModemResponseString())){
			logger.info("Pin is valid");	
			this.pinOk = true;
			this.pin = "";
			initCommand();		
		}
		if(response.equals(ATCommandReponses.ERROR)){
			logger.info("Modem error");	
		}

	}

	/**
	 * Respond to incoming call.
	 *
	 * @throws Exception the exception
	 */
	public void respondToIncomingCall() throws Exception{
		if(this.incomingCall){
			this.sendCommandToModem(ATCommands.RESPOND);
		}
	}

	/**
	 * Send command to modem.
	 *
	 * @param command the command
	 * @throws Exception the exception
	 */
	public void sendCommandToModem(ATCommands command) throws Exception{
		sendCommandToModem(command, "");
	}

	/**
	 * Send command to modem.
	 *
	 * @param command the command
	 * @param parameter the parameter
	 * @throws Exception the exception
	 */
	public void sendCommandToModem(ATCommands command, String parameter) throws Exception{
		prevReqCommand = command;
		String commandComp = command.getModemCommandString().replaceAll("<par>", parameter);
		logger.info("Send command to serial port: " + commandComp);
		writeDos.write((commandComp+"\r\n").getBytes());
		writeDos.flush();	
		//If command is start call open voice stream now to hear ring sequence
		if(command.equals(ATCommands.CALL)){
			this.startCall();	
			this.sendCommandToModem(ATCommands.OPEN_VOICE_STREAM);			
		}
	}

	/**
	 * Sets the command port name.
	 *
	 * @param commandPortName the new command port name
	 */
	public void setCommandPortName(String commandPortName) {
		this.commandPortName = commandPortName;
	}

	/**
	 * Sets the play message.
	 *
	 * @param playMessage the new play message
	 */
	public void setPlayMessage(Boolean playMessage) {
		this.playMessage = playMessage;
	}

	/**
	 * Sets the stream port nmae.
	 *
	 * @param streamPortNmae the new stream port nmae
	 */
	public void setStreamPortNmae(String streamPortNmae) {
		this.streamPortNmae = streamPortNmae;
	}

	/**
	 * Start call.
	 *
	 * @throws Exception the exception
	 */
	private void startCall () throws Exception
	{
		logger.info("Try open serial port with ID="+this.streamPortNmae);
		voiceStreamCommPort = new MySerialPort(this.streamPortNmae, 230400); 
		voiceStreamCommPort.connect();
		voiceInputStream =  new DataInputStream(voiceStreamCommPort.getInputStream());
		voiceOutputStream = new DataOutputStream(voiceStreamCommPort.getOutputStream());
		serialVoiceReader = new SerialVoiceReader(voiceInputStream,af);
		serialVoiceWriter = new SerialVoiceWriter(voiceOutputStream,af,playMessage);
		(new Thread(serialVoiceReader,"VoiceReader")).start();
		(new Thread(serialVoiceWriter,"VoiceWriter")).start();

	}

	/**
	 * Start modem command manager.
	 *
	 * @throws Exception the exception
	 */
	public void startModemCommandManager()  throws Exception{
		modemComPort = new MySerialPort(this.commandPortName, 9600); 
		modemComPort.connect();
		modemComPort.notifyOnDataAvailable(true);

		commandInputStream =  new DataInputStream(modemComPort.getInputStream());		
		serialCommandReader = new SerialCommandReader(commandInputStream,this);
		modemComPort.addEventListener(serialCommandReader);
		
		commandOutputStream =  new DataOutputStream(modemComPort.getOutputStream());		
		serialCommandWriter = new SerialCommandWriter(commandOutputStream,writePin);
		
		
		Thread t = new Thread(serialCommandWriter,"SerialCommandWriter");
		t.start();

		this.sendCommandToModem(ATCommands.DISABLE_ECHO);
		this.sendCommandToModem(ATCommands.DISABLE_DIAGNOSTIC);	
		this.sendCommandToModem(ATCommands.END_CALL);
		this.sendCommandToModem(ATCommands.CHECK_PIN);
	}

	/**
	 * Terminate.
	 */
	public void terminate(){
		try{
			endCall();
			if(serialCommandWriter != null){
				serialCommandWriter.terminate();
				serialCommandWriter = null;
			}
			if(writeDos != null){
				writeDos.close();
			}
			if(readDis != null){
				readDis.close();
			}
			if(modemComPort != null){
				modemComPort.disconnect();
				modemComPort = null;
			}
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

}

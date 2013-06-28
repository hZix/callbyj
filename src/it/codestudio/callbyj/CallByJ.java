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

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * * The Class CallByJ.
 *
 * @author  Xandros ( sandro.salari AT gmail.com )
 * @author Last changed by: Xandros
 * @version 15-ago-2012
 * @since 1.0
 */
public class CallByJ {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(CallByJ.class);

	/** The options. */
	private static Options options = new Options();

	/** The parser. */
	private static CommandLineParser parser = new GnuParser();		

	/** The com manager. */
	private static ComManager comManager;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main ( String[] args )
	{
		options.addOption( "aCom", "audio_com", true, "Specify serial COM port for audio streaming (3G APPLICATION ...)" );
		options.addOption( "cCom", "command_com", true, "Specify serial COM port for modem AT command (PC UI INTERFACE ...)" );
		options.addOption( "p", "play_message", false, "Play recorded message instead to respond with audio from mic" );
		options.addOption("h", "help", false, "Print help for this application");
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String audioCOM = "";
			String commandCOM = "";
			Boolean playMessage = false;
			args = Utils.fitlerNullString(args);
			CommandLine cmd = parser.parse( options, args );			
			if (cmd.hasOption("aCom") ) {
				audioCOM = cmd.getOptionValue("aCom");;
			}
			if (cmd.hasOption("cCom") ) {
				commandCOM = cmd.getOptionValue("cCom");;	
			}
			if (cmd.hasOption("p") ) {
				playMessage = true;
			}
			if(audioCOM != null && commandCOM !=null && !audioCOM.isEmpty() && !commandCOM.isEmpty()){
				comManager = ComManager.getInstance(commandCOM,audioCOM,playMessage);
			}else{
				HelpFormatter f = new HelpFormatter();
				f.printHelp("\r Exaple: CallByJ -aCom COM11 -cCom COM10 \r OptionsTip", options);
				return;
			}
			options = new Options();
			options.addOption("h", "help", false, "Print help for this application");
			options.addOption( "p", "pin", true, "Specify pin of device, if present" );
			options.addOption( "c", "call", true, "Start call to specified number" );
			options.addOption( "e", "end", false, "End all active call" );
			options.addOption( "t", "terminate", false, "Terminate application" );
			options.addOption( "r", "respond", false, "Respond to incoming call" );
			comManager.startModemCommandManager();						
			while(true){
				try{
					String[] commands = {br.readLine()};
					commands = Utils.fitlerNullString(commands);
					cmd = parser.parse( options, commands );
					if (cmd.hasOption('h') ) {
						HelpFormatter f = new HelpFormatter();
						f.printHelp("OptionsTip", options);
					}
					if (cmd.hasOption('p') ) {
						String devicePin = cmd.getOptionValue("p");
						comManager.insertPin(devicePin);		
					}
					if (cmd.hasOption('c') ) {
						String numberToCall = cmd.getOptionValue("c");
						comManager.sendCommandToModem(ATCommands.CALL,numberToCall);
					}
					if (cmd.hasOption('r') ) {
						comManager.respondToIncomingCall();
					}
					if (cmd.hasOption('e') ) {
						comManager.sendCommandToModem(ATCommands.END_CALL);
					}
					if (cmd.hasOption('t') ) {
						comManager.sendCommandToModem(ATCommands.END_CALL);
						comManager.terminate();
						System.out.println("CallByJ closed!");
						break;
						//System.exit(0);
					}
				}catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}
		}
		catch ( Exception e )
		{
			logger.error(e.getMessage(),e);
		}
		finally{
			if(comManager != null)
				comManager.terminate();
		}
	}

}

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

// TODO: Auto-generated Javadoc
/**
 * * The Enum ATCommands.
 *
 * @author  Xandros ( sandro.salari AT gmail.com )
 * @author Last changed by: Xandros
 * @version 15-ago-2012
 * @since 1.0
 */
public enum ATCommands {
	
	/** The disable echo. */
	DISABLE_ECHO("ATE0"),
	
	/** The disable diagnostic. */
	DISABLE_DIAGNOSTIC("AT^CURC=0"),
	
	/** The attention. */
	ATTENTION("AT"),
	
	/** The enable hangup. */
	ENABLE_HANGUP("AT+CVHU=0"),
	
	/** The enable ring info. */
	ENABLE_RING_INFO("AT+CRC=1"),
	
	/** The enable call info. */
	ENABLE_CALL_INFO("AT+CLIP=1"),
	
	/** The check pin. */
	CHECK_PIN("AT+CPIN?"),
	
	/** The insert pin. */
	INSERT_PIN("AT+CPIN=<par>"),
	
	/** The end call. */
	END_CALL("ATH"),
	
	/** The call. */
	CALL("ATDT<par>;"),
	
	/** The open voice stream. */
	OPEN_VOICE_STREAM("AT^DDSETEX=2"),
	
	/** The respond. */
	RESPOND("ATA");
		

	/** The modem command string. */
	private String modemCommandString;

	/**
	 * Instantiates a new aT commands.
	 *
	 * @param modemCommandString the modem command string
	 */
	ATCommands(String modemCommandString){
		this.modemCommandString = modemCommandString;
	}

	/**
	 * Gets the modem command string.
	 *
	 * @return the modem command string
	 */
	public String getModemCommandString() {
		return modemCommandString;
	}
}

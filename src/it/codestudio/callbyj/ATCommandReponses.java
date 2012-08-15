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
 * * The Enum ATCommandReponses.
 *
 * @author  Xandros ( sandro.salari AT gmail.com )
 * @author Last changed by: Xandros
 * @version 15-ago-2012
 * @since 1.0
 */
public enum ATCommandReponses {
	
	/** The pin needed. */
	PIN_NEEDED("+CPIN: SIM PIN"),
	
	/** The ok. */
	OK("OK"),
	
	/** The pin correct. */
	PIN_CORRECT("+CPIN: READY"),
	
	/** The error. */
	ERROR("ERROR"),
	
	/** The call established. */
	CALL_ESTABLISHED("^CONN:1,0"),
	
	/** The incoming call. */
	INCOMING_CALL("CRING:VOICE"),
	
	/** The incoming call info. */
	INCOMING_CALL_INFO("CLIP"),
	
	/** The call ended. */
	CALL_ENDED("CEND");
	

	/** The modem respone string. */
	private String modemResponeString;

	/**
	 * Instantiates a new aT command reponses.
	 *
	 * @param modemCommandString the modem command string
	 */
	ATCommandReponses(String modemCommandString){
		this.modemResponeString = modemCommandString;
	}

	/**
	 * Gets the modem response string.
	 *
	 * @return the modem response string
	 */
	public String getModemResponseString() {
		return modemResponeString;
	}
}

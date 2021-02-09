package cps.tenios.reseauEphemere.interfaces;

import java.io.Serializable;

/**
 * 
 * @author teill
 *
 */
public interface MessageI {
	/**
	 * 
	 * @return
	 */
	AddressI getAddress();
	/**
	 * 
	 * @return
	 */
	Serializable getContent();
	/**
	 * 
	 * @return
	 */
	boolean stillAlive();
	/**
	 * 
	 */
	void decrementsGops();
}

package cps.tenios.reseauEphemere.interfaces;

/**
 * 
 * @author alexc
 *
 */
public interface ConnectionInfoI {

	/**
	 * 
	 * @return
	 */
	NodeAddressI getAddress();
	
	/**
	 * 
	 * @return
	 */
	String getCommunicationInboundPortUri();
	
	/**
	 * 
	 * @return
	 */
	String getRountingInboundPortURI();
}

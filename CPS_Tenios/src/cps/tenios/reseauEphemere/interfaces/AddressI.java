package cps.tenios.reseauEphemere.interfaces;

/**
 * 
 * @author alexc
 *
 */
public interface AddressI {
	
	/**
	 * 
	 * @return
	 */
	boolean isNodeAddress();
	
	/**
	 * 
	 * @return
	 */
	boolean isNetworkAddress();
	
	/**
	 * 
	 * @param a
	 * @return
	 */
	boolean equals(AddressI a);
}

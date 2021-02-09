package cps.tenios.reseauEphemere.interfaces;

/**
 * 
 * @author teill
 *
 */
public interface RouteInfoI {
	/**
	 * 
	 * @return
	 */
	AddressI getDestination();
	/**
	 * 
	 * @return
	 */
	int getNumberOfHops();
}

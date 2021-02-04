package cps.tenios.interfaces;

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

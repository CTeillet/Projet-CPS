package cps.tenios.reseauEphemere.interfaces;

import java.util.Set;

/**
 * 
 * @author teill
 *
 */
public interface RoutingCI {
	/**
	 * 
	 * @param neighbour
	 * @param routes
	 */
	void updateRouting(NodeAddressI neighbour, Set<RouteInfoI> routes) throws Exception;
	/**
	 * 
	 * @param neighbour
	 * @param numberOfHops
	 */
	void udateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception;
	
}

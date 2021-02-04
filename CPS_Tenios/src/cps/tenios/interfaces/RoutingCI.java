package cps.tenios.interfaces;

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
	void updateRouting(NodeAddressI neighbour, Set<RouteInfoI> routes);
	/**
	 * 
	 * @param neighbour
	 * @param numberOfHops
	 */
	void udateAccessPoint(NodeAddressI neighbour, int numberOfHops);
	
}

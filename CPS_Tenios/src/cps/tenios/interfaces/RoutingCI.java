package cps.tenios.interfaces;

import java.util.Set;

public interface RoutingCI {
	void updateRouting(NodeAddressI neighbour, Set<RouteInfoI> routes);
	void udateAccessPoint(NodeAddressI neighbour, int numberOfHops);
	
}

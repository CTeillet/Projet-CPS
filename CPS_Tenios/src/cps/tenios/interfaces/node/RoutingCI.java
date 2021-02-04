package cps.tenios.interfaces.node;

import java.util.Set;

import cps.tenios.interfaces.gestionnaireReseau.NodeAddressI;

public interface RoutingCI {
	void updateRouting(NodeAddressI neighbour, Set<RouteInfoI> routes);
	void udateAccessPoint(NodeAddressI neighbour, int numberOfHops);
	
}

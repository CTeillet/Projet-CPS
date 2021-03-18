package cps.tenios.reseauEphemere;

import java.util.Set;

import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.RouteInfoI;
import cps.tenios.reseauEphemere.interfaces.RoutingCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class RoutingConnector extends AbstractConnector implements RoutingCI {

	@Override
	public void updateRouting(NodeAddressI neighbour, Set<RouteInfoI> routes) throws Exception {
		((RoutingCI)this.offering).updateRouting(neighbour, routes);

	}

	@Override
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception {
		((RoutingCI)this.offering).updateAccessPoint(neighbour, numberOfHops);
	}

}

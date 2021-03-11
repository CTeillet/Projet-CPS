package cps.tenios.reseauEphemere.node;

import java.util.Set;

import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.RouteInfoI;
import cps.tenios.reseauEphemere.interfaces.RoutingCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class RoutingNodeOutboundPort extends AbstractOutboundPort implements RoutingCI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4539783156684585745L;

	public RoutingNodeOutboundPort(ComponentI owner) throws Exception {
		super(RoutingCI.class, owner);
		assert owner instanceof Node;
	}
	
	public RoutingNodeOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RoutingCI.class, owner);
	}

	@Override
	public void updateRouting(NodeAddressI neighbour, Set<RouteInfoI> routes) throws Exception {
		((RoutingCI)this.getConnector()).updateRouting(neighbour, routes);
	}

	@Override
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception {
		((RoutingCI)this.getConnector()).updateAccessPoint(neighbour, numberOfHops);
	}

}
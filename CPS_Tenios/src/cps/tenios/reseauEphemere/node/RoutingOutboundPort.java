package cps.tenios.reseauEphemere.node;

import java.util.Set;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.RouteInfoI;
import cps.tenios.reseauEphemere.interfaces.RoutingCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * 
 * @author Tenios
 *
 */
public class RoutingOutboundPort extends AbstractOutboundPort implements RoutingCI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4539783156684585745L;

	public RoutingOutboundPort(ComponentI owner) throws Exception {
		super(RoutingCI.class, owner);
		assert owner instanceof Node;
	}
	
	public RoutingOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RoutingCI.class, owner);
	}

	@Override
	public void updateRouting(AddressI neighbour, Set<RouteInfoI> routes) throws Exception {
		((RoutingCI)this.getConnector()).updateRouting(neighbour, routes);
	}

	@Override
	public void updateAccessPoint(AddressI neighbour, int numberOfHops) throws Exception {
		((RoutingCI)this.getConnector()).updateAccessPoint(neighbour, numberOfHops);
	}

}

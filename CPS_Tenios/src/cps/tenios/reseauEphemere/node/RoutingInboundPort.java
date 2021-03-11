package cps.tenios.reseauEphemere.node;

import java.util.Set;

import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import cps.tenios.reseauEphemere.interfaces.RouteInfoI;
import cps.tenios.reseauEphemere.interfaces.RoutingCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RoutingInboundPort extends AbstractInboundPort implements RoutingCI {

	/**
	 * 
	 */
	private static final long serialVersionUID = -675568004521898505L;

	public RoutingInboundPort(ComponentI owner) throws Exception {
		super(RoutingCI.class, owner);
		assert owner instanceof Routing;
	}
	
	public RoutingInboundPort(String uri, ComponentI owner)  throws Exception{
		super(uri, RegistrationCI.class, owner);
		assert owner instanceof Routing;
	}

	@Override
	public void updateRouting(NodeAddressI neighbour, Set<RouteInfoI> routes) throws Exception {
		this.getOwner().handleRequest( r-> { ((Routing)r).updateRouting(neighbour, routes); return null;});
		
	}

	@Override
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception {
		
		this.getOwner().handleRequest( r-> {((Routing)r).updateAccessPoint(neighbour, numberOfHops); return null;});
		
	}

}
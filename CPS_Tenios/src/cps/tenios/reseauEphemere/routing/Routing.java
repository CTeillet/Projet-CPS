package cps.tenios.reseauEphemere.routing;

import java.util.Set;

import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.RouteInfoI;
import cps.tenios.reseauEphemere.interfaces.RoutingCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;

@OfferedInterfaces(offered = {RoutingCI.class})
public class Routing extends AbstractComponent {
	
	private RoutingInboundPort routingInboundPort;
	
	protected Routing() {
		super(0, 1);
	}
	
	void updateRouting(NodeAddressI neighbour, Set<RouteInfoI> routes) throws Exception{
		
	}
	
	void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception{
		
	}
	
	

}

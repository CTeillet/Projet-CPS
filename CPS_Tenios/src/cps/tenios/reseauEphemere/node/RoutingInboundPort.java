package cps.tenios.reseauEphemere.node;

import java.util.Set;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.RouteInfoI;
import cps.tenios.reseauEphemere.interfaces.RoutingCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
 * Port d'entee pour les noeud de routage 
 * @author Tenios
 *
 */
public class RoutingInboundPort extends AbstractInboundPort implements RoutingCI {

	/**
	 * 
	 */
	private static final long serialVersionUID = -675568004521898505L;

	public RoutingInboundPort(ComponentI owner) throws Exception {
		super(RoutingCI.class, owner);
		assert owner instanceof RoutingCI;
	}

	public RoutingInboundPort(String uri, ComponentI owner)  throws Exception{
		super(uri, RoutingCI.class, owner);
		//assert (owner instanceof RoutingNode) || (owner instanceof AccessPointNode);
		//assert owner instanceof Node;
	}

	@Override
	public void updateRouting(AddressI neighbour, Set<RouteInfoI> routes) throws Exception {
		this.getOwner().runTask( r ->{
			try {
				((Router2Test) r).updateRouting(neighbour, routes);
			}catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void updateAccessPoint(AddressI neighbour, int numberOfHops) throws Exception {
		this.getOwner().runTask( r ->{
			try {
				((Router2Test) r).updateAccessPoint(neighbour, numberOfHops);
			}catch (Exception e) {
				e.printStackTrace();
			}
		});		
	}

}

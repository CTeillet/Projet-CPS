package cps.tenios.reseauEphemere.node;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class NodeInboundPort extends AbstractInboundPort implements CommunicationCI {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3226720899115114653L;

	public NodeInboundPort(ComponentI owner) throws Exception {
		super(CommunicationCI.class, owner);
		assert owner instanceof Node;
	}
	
	public NodeInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, CommunicationCI.class, owner);
		assert owner instanceof Node;
	}

	@Override
	public void connect(NodeAddressI address, String communicationInboundPortURI) throws Exception {
		this.getOwner().handleRequest( c -> {((Node)c).connect(address, communicationInboundPortURI); return null;} );

	}

	@Override
	public String connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI) throws Exception {
		return this.getOwner().handleRequest( c -> ((Node)c).connectRouting(address, communicationInboundPortURI, routingInboundPortURI));


	}

	@Override
	public void transmitMessage(MessageI m) throws Exception {
		this.getOwner().handleRequest( c -> {((Node)c).transmitMessage(m); return null;} );

	}

	@Override
	public boolean hasRouteFor(AddressI address)  throws Exception{
		return this.getOwner().handleRequest( c -> ((Node)c).hasRouteFor(address));
	}

	@Override
	public void ping()  throws Exception{
		this.getOwner().handleRequest( c -> {((Node)c).ping(); return null;} );

	}

}

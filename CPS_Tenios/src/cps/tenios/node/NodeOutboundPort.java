package cps.tenios.node;

import cps.tenios.interfaces.AddressI;
import cps.tenios.interfaces.CommunicationCI;
import cps.tenios.interfaces.MessageI;
import cps.tenios.interfaces.NodeAddressI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class NodeOutboundPort extends AbstractOutboundPort implements CommunicationCI {
	
	public NodeOutboundPort(Class<? extends RequiredCI> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
		// TODO Auto-generated constructor stub
	}

	public NodeOutboundPort(String uri, Class<? extends RequiredCI> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
		// TODO Auto-generated constructor stub
	}
	
	

	@Override
	public void connect(NodeAddressI address, String communicationInboundPortURI) {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI) {
		// TODO Auto-generated method stub

	}

	@Override
	public void transmitMessage(MessageI m) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasRouteFor(AddressI address) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void ping() {
		// TODO Auto-generated method stub

	}

}

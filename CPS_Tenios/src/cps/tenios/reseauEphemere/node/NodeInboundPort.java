package cps.tenios.reseauEphemere.node;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class NodeInboundPort extends AbstractInboundPort implements CommunicationCI {
	
	public NodeInboundPort(Class<? extends OfferedCI> implementedInterface, ComponentI owner, String pluginURI,
			String executorServiceURI) throws Exception {
		super(implementedInterface, owner, pluginURI, executorServiceURI);
		// TODO Auto-generated constructor stub
	}

	public NodeInboundPort(Class<? extends OfferedCI> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
		// TODO Auto-generated constructor stub
	}

	public NodeInboundPort(String uri, Class<? extends OfferedCI> implementedInterface, ComponentI owner,
			String pluginURI, String executorServiceURI) throws Exception {
		super(uri, implementedInterface, owner, pluginURI, executorServiceURI);
		// TODO Auto-generated constructor stub
	}

	public NodeInboundPort(String uri, Class<? extends OfferedCI> implementedInterface, ComponentI owner)
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

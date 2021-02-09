package cps.tenios.reseauEphemere.node;

import java.util.Set;

import cps.tenios.reseauEphemere.interfaces.ConnectionInfoI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.PositionI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class NodeRegistrationOutboundPort extends AbstractOutboundPort implements RegistrationCI {



	public NodeRegistrationOutboundPort(Class<? extends RequiredCI> implementedInterface, ComponentI owner)
			throws Exception {
		super(implementedInterface, owner);
		// TODO Auto-generated constructor stub
	}

	public NodeRegistrationOutboundPort(String uri, Class<? extends RequiredCI> implementedInterface, ComponentI owner)
			throws Exception {
		super(uri, implementedInterface, owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Set<ConnectionInfoI> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ConnectionInfoI> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ConnectionInfoI> registerRoutingNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void unregister(NodeAddressI address) {
		// TODO Auto-generated method stub

	}

}

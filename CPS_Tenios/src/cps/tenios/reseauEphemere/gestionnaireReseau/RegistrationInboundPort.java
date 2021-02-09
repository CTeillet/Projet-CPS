package cps.tenios.reseauEphemere.gestionnaireReseau;

import java.util.Set;

import cps.tenios.reseauEphemere.interfaces.ConnectionInfoI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.PositionI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RegistrationInboundPort extends AbstractInboundPort implements RegistrationCI {

	public RegistrationInboundPort(Class<? extends OfferedCI> implementedInterface, ComponentI owner, String pluginURI,
			String executorServiceURI) throws Exception {
		super(implementedInterface, owner, pluginURI, executorServiceURI);
		// TODO Auto-generated constructor stub
	}

	public RegistrationInboundPort(Class<? extends OfferedCI> implementedInterface, ComponentI owner) throws Exception {
		super(implementedInterface, owner);
		// TODO Auto-generated constructor stub
	}

	public RegistrationInboundPort(String uri, Class<? extends OfferedCI> implementedInterface, ComponentI owner,
			String pluginURI, String executorServiceURI) throws Exception {
		super(uri, implementedInterface, owner, pluginURI, executorServiceURI);
		// TODO Auto-generated constructor stub
	}

	public RegistrationInboundPort(String uri, Class<? extends OfferedCI> implementedInterface, ComponentI owner)
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

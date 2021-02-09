package cps.tenios.reseauEphemere;

import java.util.Set;

import cps.tenios.interfaces.ConnectionInfoI;
import cps.tenios.interfaces.NodeAddressI;
import cps.tenios.interfaces.PositionI;
import cps.tenios.interfaces.RegistrationCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class RegistrationConnector extends AbstractConnector implements RegistrationCI {

	public RegistrationConnector() {
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

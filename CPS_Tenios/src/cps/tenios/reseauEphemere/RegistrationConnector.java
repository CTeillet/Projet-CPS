package cps.tenios.reseauEphemere;

import java.util.Set;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.PositionI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * Connecteur entre un composant de RegistrationCI et CommunicationCI
 * @author Tenios
 *
 */
public class RegistrationConnector extends AbstractConnector implements RegistrationCI {

	@Override
	public Set<ConnectionInfo> registerTerminalNode(AddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) throws Exception {
		return ((RegistrationCI)this.offering).registerTerminalNode(address, communicationInboundPortURI, initialPosition, initialRange);
	}

	@Override
	public Set<ConnectionInfo> registerAccessPoint(AddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		return ((RegistrationCI)this.offering).registerAccessPoint(address, communicationInboundPortURI, initialPosition, initialRange, routingInboundPortURI);
	}

	@Override
	public Set<ConnectionInfo> registerRoutingNode(AddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		return ((RegistrationCI)this.offering).registerRoutingNode(address, communicationInboundPortURI, initialPosition, initialRange, routingInboundPortURI);
	}

	@Override
	public void unregister(AddressI address) throws Exception {
		((RegistrationCI)this.offering).unregister(address);
	}

}

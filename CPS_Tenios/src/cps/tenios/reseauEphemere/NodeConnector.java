package cps.tenios.reseauEphemere;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class NodeConnector extends AbstractConnector implements CommunicationCI {

	public NodeConnector() {
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

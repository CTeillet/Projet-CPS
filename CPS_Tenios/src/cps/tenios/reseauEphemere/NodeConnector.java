package cps.tenios.reseauEphemere;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * Connecteur entre 2 composants de CommunicationCI
 * @author Tenios
 *
 */
public class NodeConnector extends AbstractConnector implements CommunicationCI {

	@Override
	public void connect(NodeAddressI address, String communicationInboundPortURI)  throws Exception{
		((CommunicationCI)this.offering).connect(address, communicationInboundPortURI);

	}

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI,
			String routingInboundPortURI)  throws Exception{
		((CommunicationCI)this.offering).connectRouting(address, communicationInboundPortURI,
														routingInboundPortURI);

	}

	@Override
	public void transmitMessage(MessageI m)  throws Exception{
		((CommunicationCI)this.offering).transmitMessage(m);

	}

	@Override
	public boolean hasRouteFor(AddressI address)  throws Exception{
		return ((CommunicationCI)this.offering).hasRouteFor(address);
	}

	@Override
	public void ping()  throws Exception{
		((CommunicationCI)this.offering).ping();

	}

	@Override
	public AddressI getAddress() throws Exception {
		return ((CommunicationCI)this.offering).getAddress();
	}

}

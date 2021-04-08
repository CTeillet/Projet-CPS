package cps.tenios.reseauEphemere;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * Connecteur entre 2 composants de CommunicationCI
 * @author Tenios
 *
 */
public class NodeConnector extends AbstractConnector implements CommunicationCI {

	@Override
	public void connect(AddressI address, String communicationInboundPortURI)  throws Exception{
		((CommunicationCI)this.offering).connect(address, communicationInboundPortURI);

	}

	@Override
	public void connectRouting(AddressI address, String communicationInboundPortURI,
			String routingInboundPortURI)  throws Exception{
		((CommunicationCI)this.offering).connectRouting(address, communicationInboundPortURI,
														routingInboundPortURI);

	}

	@Override
	public void transmitMessage(MessageI m)  throws Exception{
		((CommunicationCI)this.offering).transmitMessage(m);

	}

	@Override
	public int hasRouteFor(AddressI address)  throws Exception{
		return ((CommunicationCI)this.offering).hasRouteFor(address);
	}

	@Override
	public void ping()  throws Exception{
		((CommunicationCI)this.offering).ping();

	}

}

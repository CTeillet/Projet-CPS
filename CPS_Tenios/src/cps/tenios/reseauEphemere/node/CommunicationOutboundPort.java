package cps.tenios.reseauEphemere.node;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class CommunicationOutboundPort extends AbstractOutboundPort implements CommunicationCI {
	
	/**
	 * Verison de s�rie de la classe
	 */
	private static final long serialVersionUID = 3254624094913909113L;

	/**
	 * Constructeur prenant en argument le propri�taire de la classe
	 * @param owner propri�taire du port
	 * @throws Exception s'il y a un probl�me
	 */
	public CommunicationOutboundPort(ComponentI owner) throws Exception {
		super(CommunicationCI.class, owner);
	}
	
	/**
	 * Constructeur prenant en arguments le propri�taire ainsi que le port voulue pour le port
	 * @param uri voulue pour le port
	 * @param owner propri�taire du port 
	 * @throws Exception s'il y a un probleme
	 */
	public CommunicationOutboundPort(String uri,ComponentI owner)
			throws Exception {
		super(uri, CommunicationCI.class, owner);
		// TODO Auto-generated constructor stub
	}
	
	

	@Override
	public void connect(AddressI address, String communicationInboundPortURI) throws Exception {
		((CommunicationCI)this.getConnector()).connect(address, communicationInboundPortURI);

	}

	@Override
	public void connectRouting(AddressI address, String communicationInboundPortURI, String routingInboundPortURI) throws Exception  {
		((CommunicationCI)this.getConnector()).connectRouting(address, communicationInboundPortURI, routingInboundPortURI);
	}

	@Override
	public void transmitMessage(MessageI m) throws Exception {
		((CommunicationCI)this.getConnector()).transmitMessage(m);

	}

	@Override
	public int hasRouteFor(AddressI address) throws Exception {
		return ((CommunicationCI)this.getConnector()).hasRouteFor(address);
	}

	@Override
	public void ping() throws Exception {
		((CommunicationCI)this.getConnector()).ping();

	}


}

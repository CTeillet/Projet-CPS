package cps.tenios.reseauEphemere.node;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class NodeOutboundPort extends AbstractOutboundPort implements CommunicationCI {
	
	/**
	 * Verison de série de la classe
	 */
	private static final long serialVersionUID = 3254624094913909113L;

	/**
	 * Constructeur prenant en argument le propriétaire de la classe
	 * @param owner propriétaire du port
	 * @throws Exception s'il y a un problème
	 */
	public NodeOutboundPort(ComponentI owner) throws Exception {
		super(CommunicationCI.class, owner);
	}
	
	/**
	 * Constructeur prenant en arguments le propriétaire ainsi que le port voulue pour le port
	 * @param uri voulue pour le port
	 * @param owner propriétaire du port 
	 * @throws Exception s'il y a un probleme
	 */
	public NodeOutboundPort(String uri,ComponentI owner)
			throws Exception {
		super(uri, CommunicationCI.class, owner);
		// TODO Auto-generated constructor stub
	}
	
	

	@Override
	public void connect(NodeAddressI address, String communicationInboundPortURI) throws Exception {
		((CommunicationCI)this.getConnector()).connect(address, communicationInboundPortURI);

	}

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI) throws Exception  {
		((CommunicationCI)this.getConnector()).connectRouting(address, communicationInboundPortURI, routingInboundPortURI);
	}

	@Override
	public void transmitMessage(MessageI m) throws Exception {
		((CommunicationCI)this.getConnector()).transmitMessage(m);

	}

	@Override
	public boolean hasRouteFor(AddressI address) throws Exception {
		return ((CommunicationCI)this.getConnector()).hasRouteFor(address);
	}

	@Override
	public void ping() throws Exception {
		((CommunicationCI)this.getConnector()).ping();

	}

}

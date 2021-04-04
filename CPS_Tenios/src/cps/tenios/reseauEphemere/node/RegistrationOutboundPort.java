package cps.tenios.reseauEphemere.node;

import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.PositionI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * Classe repr�snetant un port sortant vers le gestionnaire r�seau
 * @author Tenios
 *
 */
public class RegistrationOutboundPort extends AbstractOutboundPort implements RegistrationCI {

	/**
	 * Verison de s�rie de la classe
	 */
	private static final long serialVersionUID = -4716039516988394345L;

	/**
	 * Cosntructeur du port prenant seulement le propri�taire
	 * @param owner propri�taire du port
	 * @throws Exception s'il y a un  probleme
	 */
	public RegistrationOutboundPort(ComponentI owner)
			throws Exception {
		super(RegistrationCI.class, owner);
	}

	/**
	 * Constructeur ou l'on peut sp�cifier l'URI de du port, ainsi que le propri�taire
	 * @param uri URI voulue pour le port
	 * @param owner propri�taire du port
	 * @throws Exception s'il y a un probleme
	 */
	public RegistrationOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RegistrationCI.class, owner);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) throws Exception {
		return ((RegistrationCI)this.getConnector()).registerTerminalNode(address, communicationInboundPortURI, initialPosition, initialRange);
	}

	@Override
	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		// TODO Auto-generated method stub
		return ((RegistrationCI)this.getConnector()).registerAccessPoint(address, communicationInboundPortURI, initialPosition, initialRange, routingInboundPortURI);
	}

	@Override
	public Set<ConnectionInfo> registerRoutingNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		// TODO Auto-generated method stub
		return ((RegistrationCI)this.getConnector()).registerRoutingNode(address, communicationInboundPortURI, initialPosition, initialRange, routingInboundPortURI);
	}

	@Override
	public void unregister(NodeAddressI address) throws Exception {
		((RegistrationCI)this.getConnector()).unregister(address);

	}

}

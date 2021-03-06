package cps.tenios.reseauEphemere.node;

import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.interfaces.AddressI;
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
	 * Version de s�rie de la classe
	 */
	private static final long serialVersionUID = -4716039516988394345L;

	/**
	 * Constructeur du port prenant seulement le propri�taire
	 * @param owner propri�taire du port
	 * @throws Exception s'il y a un  probleme
	 */
	public RegistrationOutboundPort(ComponentI owner) throws Exception {
		super(RegistrationCI.class, owner);
	}

	/**
	 * Constructeur ou l'on peut sp�cifier l'URI de du port, ainsi que le propri�taire
	 * @param uri URI voulue pour le port
	 * @param owner propri�taire du port
	 * @throws Exception s'il y a un probleme
	 */
	public RegistrationOutboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RegistrationCI.class, owner);
	}

	@Override
	public Set<ConnectionInfo> registerTerminalNode(AddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) throws Exception {
		try {
			return ((RegistrationCI)this.getConnector()).registerTerminalNode(address, communicationInboundPortURI, initialPosition, initialRange);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public Set<ConnectionInfo> registerAccessPoint(AddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		return ((RegistrationCI)this.getConnector()).registerAccessPoint(address, communicationInboundPortURI, initialPosition, initialRange, routingInboundPortURI);
	}

	@Override
	public Set<ConnectionInfo> registerRoutingNode(AddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		return ((RegistrationCI)this.getConnector()).registerRoutingNode(address, communicationInboundPortURI, initialPosition, initialRange, routingInboundPortURI);
	}

	@Override
	public void unregister(AddressI address) throws Exception {
		((RegistrationCI)this.getConnector()).unregister(address);

	}

}

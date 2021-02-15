package cps.tenios.reseauEphemere.gestionnaireReseau;

import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.PositionI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RegistrationInboundPort 
extends AbstractInboundPort 
implements RegistrationCI {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5946152123825267062L;

	public RegistrationInboundPort(ComponentI owner) throws Exception {
		super(RegistrationCI.class,owner);
		// verif owner est du bon type
		assert owner instanceof GestionnaireReseau;
	}
	
	public RegistrationInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RegistrationCI.class, owner);
		// verif owner est du bon type
		assert owner instanceof GestionnaireReseau;
	}
	
	@Override
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) throws Exception {
		return this.getOwner().handleRequest(
				register -> ((GestionnaireReseau)register).registerTerminalNode(address, communicationInboundPortURI, initialPosition, initialRange));

	}

	@Override
	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		return this.getOwner().handleRequest(
				register -> ((GestionnaireReseau)register).registerAccessPoint(address, communicationInboundPortURI, initialPosition, initialRange, routingInboundPortURI));
	}

	@Override
	public Set<ConnectionInfo> registerRoutingNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		return this.getOwner().handleRequest(
				register -> ((GestionnaireReseau)register).registerRoutingNode(address, communicationInboundPortURI, initialPosition, initialRange, routingInboundPortURI));
	}

	@Override
	public void unregister(NodeAddressI address) throws Exception {
		this.getOwner().handleRequest( register -> 
						{((GestionnaireReseau)register).unregister(address);
						  return null;
		});
	}

}

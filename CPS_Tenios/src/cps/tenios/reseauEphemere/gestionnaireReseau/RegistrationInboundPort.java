package cps.tenios.reseauEphemere.gestionnaireReseau;

import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.PositionI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RegistrationInboundPort extends AbstractInboundPort implements RegistrationCI {

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
		System.out.println("AAAAA");
		System.out.println(this.getOwner());
		try {
			return this.getOwner().handleRequest(
				register -> ((RegistrationCI)register).registerTerminalNode(address, communicationInboundPortURI, initialPosition, initialRange));
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			return null;
		}
	}

	@Override
	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		return this.getOwner().handleRequest(
				register -> ((RegistrationCI)register).registerAccessPoint(address, communicationInboundPortURI, initialPosition, initialRange, routingInboundPortURI));
	}

	@Override
	public Set<ConnectionInfo> registerRoutingNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		// TODO a verfier
		return this.getOwner().handleRequest(
				register -> ((RegistrationCI)register).registerRoutingNode(address, communicationInboundPortURI, initialPosition, initialRange, routingInboundPortURI));
	}

	@Override
	public void unregister(NodeAddressI address) throws Exception {
		((RegistrationCI)this.getOwner()).unregister(address);
		this.getOwner().handleRequest( register -> 
						{((RegistrationCI)register).unregister(address);
						  return null;
		});
	}

}

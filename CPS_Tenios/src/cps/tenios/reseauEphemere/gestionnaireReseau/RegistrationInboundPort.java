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
	private static final long serialVersionUID = 1L;
	
	public RegistrationInboundPort(ComponentI owner) throws Exception {
		super(RegistrationCI.class, owner);
		// verif owner est du bon type
		assert owner instanceof RegistrationCI;
	}
	
	public RegistrationInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RegistrationCI.class, owner);
		// verif owner est du bon type
		assert owner instanceof RegistrationCI;
	}
	
/*
 * Pour plugin TODO : a voir si necessaire
 * 
	public RegistrationInboundPort(Class<? extends OfferedCI> implementedInterface, ComponentI owner, String pluginURI,
			String executorServiceURI) throws Exception {
		super(implementedInterface, owner, pluginURI, executorServiceURI);
		// TODO Auto-generated constructor stub
	}

	public RegistrationInboundPort(String uri, Class<? extends OfferedCI> implementedInterface, ComponentI owner,
			String pluginURI, String executorServiceURI) throws Exception {
		super(uri, implementedInterface, owner, pluginURI, executorServiceURI);
		// TODO Auto-generated constructor stub
	}
*/

	@Override
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) throws Exception {
		// TODO a verfier
		return this.getOwner().handleRequest(
				register -> ((RegistrationCI)register).registerTerminalNode(address, communicationInboundPortURI, initialPosition, initialRange));
	}

	@Override
	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception {
		// TODO a verfier
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

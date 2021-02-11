package cps.tenios.reseauEphemere.gestionnaireReseau;

import java.util.HashSet;
import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.Position;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.PositionI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@OfferedInterfaces(offered = {RegistrationCI.class})
public class GestionnaireReseau extends AbstractComponent {
	
	public static final String  INBOUNDPORT_URI = "registrationInboundPort-uri";
	private RegistrationInboundPort registrationInboundPort;

	private Set<ConnectionInfo> table;
	
	protected GestionnaireReseau() throws Exception {
		super(1, 0);
		this.registrationInboundPort = new RegistrationInboundPort(INBOUNDPORT_URI, this);
		this.registrationInboundPort.publishPort();
		table = new HashSet<ConnectionInfo>();
	}
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.registrationInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
	
	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange){
		ConnectionInfo c = new ConnectionInfo(address, communicationInboundPortURI, false, "", initialPosition);
		Set<ConnectionInfo> res = new HashSet<ConnectionInfo>();
		table.stream()
		.filter( x -> x.getPosition().distance(initialPosition)<=initialRange)
		.forEach(x-> res.add(x));
		table.add(c);
		return res;	
	}
	
	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI){
		Set<ConnectionInfo> res = new HashSet<ConnectionInfo>();
		ConnectionInfo c = new ConnectionInfo(address, communicationInboundPortURI, true, routingInboundPortURI, initialPosition);
		table.stream()
		.filter( x -> x.getPosition().distance(initialPosition)<=initialRange)
		.forEach(x-> res.add(x));
		table.add(c);
		return res;
	}
	
	public Set<ConnectionInfo> registerRoutingNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI){
		Set<ConnectionInfo> res = new HashSet<ConnectionInfo>();
		ConnectionInfo c = new ConnectionInfo(address, communicationInboundPortURI, true, routingInboundPortURI, initialPosition);
		table.stream()
		.filter( x -> x.getPosition().distance(initialPosition)<=initialRange)
		.forEach(x-> res.add(x));
		table.add(c);
		return res;
	}
}

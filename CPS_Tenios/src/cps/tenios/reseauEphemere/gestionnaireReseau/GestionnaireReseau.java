package cps.tenios.reseauEphemere.gestionnaireReseau;

import java.util.HashSet;
import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
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
	//String test = registrationInboundPort.get

	private Set<ConnectionInfo> tableNoeudTerminal;
	private Set<ConnectionInfo> tableNoeudAccess;
	private Set<ConnectionInfo> tableNoeudRouting;
	
	protected GestionnaireReseau() throws Exception {
		super(1, 0);
		this.registrationInboundPort = new RegistrationInboundPort(INBOUNDPORT_URI, this);
		this.registrationInboundPort.publishPort();
		tableNoeudTerminal = new HashSet<ConnectionInfo>();
		tableNoeudAccess = new HashSet<ConnectionInfo>();
		tableNoeudRouting = new HashSet<ConnectionInfo>();
	}
	

	public Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange){
		ConnectionInfo c = new ConnectionInfo(address, communicationInboundPortURI, false, "", initialPosition);
		Set<ConnectionInfo> res = new HashSet<ConnectionInfo>();
		tableNoeudAccess.stream()
		.filter( x -> x.getPosition().distance(initialPosition)<=initialRange)
		.forEach(x-> res.add(x));
		tableNoeudRouting.stream()
		.filter( x -> x.getPosition().distance(initialPosition)<=initialRange)
		.forEach(x-> res.add(x));
		tableNoeudTerminal.stream()
		.filter( x -> x.getPosition().distance(initialPosition)<=initialRange)
		.forEach(x-> res.add(x));
		tableNoeudTerminal.add(c);
		return res;	
	}
	
	public Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI){
		Set<ConnectionInfo> res = new HashSet<ConnectionInfo>();
		ConnectionInfo c = new ConnectionInfo(address, communicationInboundPortURI, true, routingInboundPortURI, initialPosition);
		res.addAll(tableNoeudAccess);
		tableNoeudAccess.add(c);
		return res;
	}
	
	public Set<ConnectionInfo> registerRoutingNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI){
		Set<ConnectionInfo> res = new HashSet<ConnectionInfo>();
		ConnectionInfo c = new ConnectionInfo(address, communicationInboundPortURI, true, routingInboundPortURI, initialPosition);
		tableNoeudRouting.stream()
		.filter( x -> x.getPosition().distance(initialPosition)<=initialRange)
		.forEach(x-> res.add(x));
		tableNoeudRouting.add(c);
		return res;
	}
	
	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		
	}
	
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.registrationInboundPort.unpublishPort();
			super.shutdown();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
	
	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(INBOUNDPORT_URI);
		super.finalise();
	}
}

package cps.tenios.reseauEphemere.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.NodeAddress;
import cps.tenios.reseauEphemere.Position;
import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.PositionI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;


public abstract class  Node extends AbstractComponent{
	private static int cmp = 0;
	//uri
	public final String INBOUNDPORT_URI;
	public final String REGISTRATION_URI;
	//inbound port
	protected NodeInboundPort nodeInboundPort;
	// besoin d'un port par noeud rattacher 
	protected List<NodeOutboundPort> nodesOutboundPort;
	
	protected NodeAddressI addr;
	protected PositionI pos;
	//port vers le gestionnaire reseau
	protected NodeRegistrationOutboundPort registrationOutboundPort;
	
	protected Set<ConnectionInfo> voisin;

	protected Node(String uri) throws Exception {
		super(0, 1);
		REGISTRATION_URI = uri;
		INBOUNDPORT_URI = AbstractPort.generatePortURI();
		
		nodeInboundPort = new NodeInboundPort(INBOUNDPORT_URI,this);
		nodesOutboundPort = new ArrayList<NodeOutboundPort>();
		registrationOutboundPort = new NodeRegistrationOutboundPort(REGISTRATION_URI, this);
		registrationOutboundPort.publishPort();
		nodeInboundPort.publishPort();
		this.toggleLogging();
		this.toggleTracing();
		
		addr = new NodeAddress(cmp++);
		Random r =new Random();
		pos = new Position(r.nextInt(150), r.nextInt(150));
	}
	
	@Override
	public abstract void execute() throws Exception;

	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(REGISTRATION_URI);
		for(NodeOutboundPort np : nodesOutboundPort) this.doPortDisconnection(np.getPortURI());
		//for(NodeInboundPort np : nodesInboundPort) this.doPortDisconnection(np.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.registrationOutboundPort.unpublishPort();
			this.nodeInboundPort.unpublishPort();
			for(NodeOutboundPort np : nodesOutboundPort) np.unpublishPort();
			//for(NodeInboundPort np : nodesInboundPort)np.unpublishPort();
			
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
	
	public abstract void connect (NodeAddressI address, String communicationInboundPortURI) throws Exception;
	
	public abstract String connectRouting (NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI) throws Exception;
	
	public abstract void transmitMessage(MessageI m) throws Exception;
	
	public abstract boolean hasRouteFor(AddressI address) throws Exception;
	
	public abstract void ping() throws Exception;
}

package cps.tenios.reseauEphemere.node;

import java.util.List;

import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@OfferedInterfaces(offered = {CommunicationCI.class})
@RequiredInterfaces(required = {CommunicationCI.class, RegistrationCI.class})
public class Node extends AbstractComponent{
	
	public static final String REGISTRATION_URI = "registrationOutboundPort-uri";
	// TODO a verifier besoin d'un port par noeud rattacher 
	protected List<NodeOutboundPort> nodesOutboundPort;
	protected List<NodeInboundPort> nodesInboundPort;
	//port vers le gestionnaire reseau
	protected NodeRegistrationOutboundPort registrationOutboundPort;

	protected Node() throws Exception {
		super(0, 1);
		registrationOutboundPort = new NodeRegistrationOutboundPort(REGISTRATION_URI, this);
		registrationOutboundPort.publishPort();
		// TODO initialiser nodes out/inbound port
	}
	
	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		// TODO : cf c1 1:46:00
		// TODO this.logMessage("...");
	}

	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(REGISTRATION_URI);
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.registrationOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
	
	

}

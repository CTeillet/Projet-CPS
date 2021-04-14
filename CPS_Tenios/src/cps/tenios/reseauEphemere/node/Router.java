package cps.tenios.reseauEphemere.node;

import java.util.HashMap;
import java.util.Map;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import fr.sorbonne_u.components.AbstractPort;

public class Router extends Node {
	
	/**
	 * Table de routage
	 */
	protected Map<AddressI, Chemin> routingTable;
	/**
	 * URI du port de Routage entrant
	 */
	protected final String ROUTING_INBOUNDPORT_URI;
	/**
	 * Port de routage entrants
	 */
	protected RoutingInboundPort routInbound;
	

	public Router(String uri, int i, int j, double r) throws Exception {
		super(uri, i, j, r);
		routingTable = new HashMap<AddressI, Chemin>();
		ROUTING_INBOUNDPORT_URI = AbstractPort.generatePortURI();
		routInbound = new RoutingInboundPort(ROUTING_INBOUNDPORT_URI, this);
		routInbound.publishPort();
	}

	public Router(String uri) throws Exception {
		super(uri);
		routingTable = new HashMap<AddressI, Chemin>();
		ROUTING_INBOUNDPORT_URI = AbstractPort.generatePortURI();
		routInbound = new RoutingInboundPort(ROUTING_INBOUNDPORT_URI, this);
		routInbound.publishPort();
	}

	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void ping() throws Exception {
		// TODO Auto-generated method stub

	}

}

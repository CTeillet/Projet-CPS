package cps.tenios.reseauEphemere;

import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.PositionI;

public class ConnectionInfo {
	private NodeAddressI address;
	private String communicationInboundURI;
	private boolean routing;
	private String routingInboundPortURI;
	private PositionI pos;
	
	
	
	public ConnectionInfo(NodeAddressI address, String communicationInboundURI, boolean routing,
			String routingInboundPortURI, PositionI pos) {
		this.address = address;
		this.communicationInboundURI = communicationInboundURI;
		this.routing = routing;
		this.routingInboundPortURI = routingInboundPortURI;
		this.pos = pos;
	}
	public NodeAddressI getAddress() {
		return address;
	}
	public String getCommunicationInboundURI() {
		return communicationInboundURI;
	}
	public boolean isRouting() {
		return routing;
	}
	public String getRoutingInboundPortURI() {
		return routingInboundPortURI;
	}
	
	public PositionI getPosition() {
		return pos;
	}
	
	

}

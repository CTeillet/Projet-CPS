package cps.tenios.reseauEphemere.node;

import cps.tenios.reseauEphemere.interfaces.NodeAddressI;

public class InfoRoutNode {

	private NodeAddressI label;
	private CommunicationOutboundPort node;
	private RoutingOutboundPort rout;

	public InfoRoutNode(NodeAddressI label, CommunicationOutboundPort node, RoutingOutboundPort rout) {
		super();
		this.label = label;
		this.node = node;
		this.rout = rout;
	}

	public NodeAddressI getAdress() {
		return label;
	}
	
	public CommunicationOutboundPort getNode() {
		return node;
	}
	
	public void setLabel(NodeAddressI label) {
		this.label = label;
	}
	
	public void setNode(CommunicationOutboundPort node) {
		this.node = node;
	}

	public RoutingOutboundPort getRout() {
		return rout;
	}

	public void setRout(RoutingOutboundPort rout) {
		this.rout = rout;
	}

}

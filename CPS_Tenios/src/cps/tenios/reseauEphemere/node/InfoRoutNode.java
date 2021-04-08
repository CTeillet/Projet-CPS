package cps.tenios.reseauEphemere.node;

import cps.tenios.reseauEphemere.interfaces.AddressI;

public class InfoRoutNode {

	private AddressI label;
	private CommunicationOutboundPort node;
	private RoutingOutboundPort rout;

	public InfoRoutNode(AddressI label, CommunicationOutboundPort node, RoutingOutboundPort rout) {
		super();
		this.label = label;
		this.node = node;
		this.rout = rout;
	}

	public AddressI getAdress() {
		return label;
	}
	
	public CommunicationOutboundPort getNode() {
		return node;
	}
	
	public void setLabel(AddressI label) {
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

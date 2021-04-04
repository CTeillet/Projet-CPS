package cps.tenios.reseauEphemere.node;

import cps.tenios.reseauEphemere.interfaces.NodeAddressI;

public class InfoTerminalN {

	private NodeAddressI addr;
	private CommunicationOutboundPort node;
	
	public InfoTerminalN(NodeAddressI addr, CommunicationOutboundPort node) {
		this.addr = addr;
		this.node = node;
	}


	public NodeAddressI getAddress() {
		return addr;
	}
	
	public CommunicationOutboundPort getNode() {
		return node;
	}
	
	public void setAddress(NodeAddressI addr) {
		this.addr = addr;
	}
	
	public void setNode(CommunicationOutboundPort node) {
		this.node = node;
	}

}

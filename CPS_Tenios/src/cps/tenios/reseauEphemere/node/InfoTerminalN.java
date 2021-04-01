package cps.tenios.reseauEphemere.node;

import cps.tenios.reseauEphemere.interfaces.NodeAddressI;

public class InfoTerminalN {

	private NodeAddressI addr;
	private NodeOutboundPort node;
	
	public InfoTerminalN(NodeAddressI addr, NodeOutboundPort node) {
		this.addr = addr;
		this.node = node;
	}


	public NodeAddressI getAddress() {
		return addr;
	}
	
	public NodeOutboundPort getNode() {
		return node;
	}
	
	public void setAddress(NodeAddressI addr) {
		this.addr = addr;
	}
	
	public void setNode(NodeOutboundPort node) {
		this.node = node;
	}

}

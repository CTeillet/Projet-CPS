package cps.tenios.reseauEphemere.node;

import cps.tenios.reseauEphemere.interfaces.AddressI;

public class InfoTerminalN {

	private AddressI addr;
	private CommunicationOutboundPort node;
	
	public InfoTerminalN(AddressI addr, CommunicationOutboundPort node) {
		this.addr = addr;
		this.node = node;
	}


	public AddressI getAddress() {
		return addr;
	}
	
	public CommunicationOutboundPort getNode() {
		return node;
	}
	
	public void setAddress(AddressI addr) {
		this.addr = addr;
	}
	
	public void setNode(CommunicationOutboundPort node) {
		this.node = node;
	}

}

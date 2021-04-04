package cps.tenios.reseauEphemere.node;

public class Chemin {

	
	private CommunicationOutboundPort next;
	private int numberOfHops;
	
	public Chemin(CommunicationOutboundPort next, int numberOfHops) {
		this.next = next;
		this.numberOfHops = numberOfHops;
	}

	public CommunicationOutboundPort getNext() {
		return next;
	}

	public void setNext(CommunicationOutboundPort next) {
		this.next = next;
	}

	public int getNumberOfHops() {
		return numberOfHops;
	}

	public void setNumberOfHops(int numberOfHops) {
		this.numberOfHops = numberOfHops;
	}
	
	
	
}

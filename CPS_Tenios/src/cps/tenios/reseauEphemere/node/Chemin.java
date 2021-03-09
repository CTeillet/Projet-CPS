package cps.tenios.reseauEphemere.node;

public class Chemin {

	
	private NodeOutboundPort next;
	private int numberOfHops;
	
	public Chemin(NodeOutboundPort next, int numberOfHops) {
		this.next = next;
		this.numberOfHops = numberOfHops;
	}

	public NodeOutboundPort getNext() {
		return next;
	}

	public void setNext(NodeOutboundPort next) {
		this.next = next;
	}

	public int getNumberOfHops() {
		return numberOfHops;
	}

	public void setNumberOfHops(int numberOfHops) {
		this.numberOfHops = numberOfHops;
	}
	
	
	
}

package cps.tenios.reseauEphemere.node;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.RouteInfoI;

public class RouteInfo implements RouteInfoI {
	
	private AddressI destination;
	private int numberOfHops;
	
	public RouteInfo(AddressI destination, int numberOfHops) {
		super();
		this.destination = destination;
		this.numberOfHops = numberOfHops;
	}

	@Override
	public AddressI getDestination() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfHops() {
		// TODO Auto-generated method stub
		return 0;
	}

}
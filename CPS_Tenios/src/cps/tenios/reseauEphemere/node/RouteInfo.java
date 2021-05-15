package cps.tenios.reseauEphemere.node;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.RouteInfoI;

/**
 * Class represantant les Information relative a une route
 * @author Tenios
 *
 */
public class RouteInfo implements RouteInfoI {
	
	/**
	 * Addresse de desitanation 
	 */
	private AddressI destination;
	/**
	 * Nombre de saut necessaire pour atteidre la destination
	 */
	private int numberOfHops;
	
	/**
	 * Constructeur permettant de construire une routeInfo avec la destination et le nombre de saut necessaire
	 * @param destination adresse de destination 
	 * @param numberOfHops nombre de saut necessaire pour y aller
	 */
	public RouteInfo(AddressI destination, int numberOfHops) {
		super();
		this.destination = destination;
		this.numberOfHops = numberOfHops;
	}

	@Override
	public AddressI getDestination() {
		return destination;
	}

	@Override
	public int getNumberOfHops() {
		return numberOfHops;
	}

}

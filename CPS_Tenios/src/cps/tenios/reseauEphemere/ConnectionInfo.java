package cps.tenios.reseauEphemere;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.PositionI;

/**
 * Contient les données relative a un noeud
 * @author Tenios
 *
 */
public class ConnectionInfo {
	/**
	 * Adresse du noeud
	 */
	private AddressI address;
	/**
	 * URI du port entrant de communication
	 */
	private String communicationInboundURI;
	/**
	 * Le noeud est capable de router
	 */
	private boolean routing;
	/**
	 * URI du port entrant du routeur
	 */
	private String routingInboundPortURI;
	/**
	 * Position du noeud
	 */
	private PositionI pos;
	
	
	/**
	 * Constructeur de ConnectionInfo
	 * @param address Adresse du noeud
	 * @param communicationInboundURI URI du port entrant de communication
	 * @param routing Le noeud est capable de router
	 * @param routingInboundPortURI URI du port entrant du routeur
	 * @param pos Position du noeud
	 */
	public ConnectionInfo(AddressI address, String communicationInboundURI, boolean routing,
			String routingInboundPortURI, PositionI pos) {
		this.address = address;
		this.communicationInboundURI = communicationInboundURI;
		this.routing = routing;
		this.routingInboundPortURI = routingInboundPortURI;
		this.pos = pos;
	}
	/**
	 * Retourne l'adress du noeud
	 * @return l'adress du noeud
	 */
	public AddressI getAddress() {
		return address;
	}
	/**
	 * Retourne URI du port entrant de communication
	 * @return URI du port entrant de communication
	 */
	public String getCommunicationInboundURI() {
		return communicationInboundURI;
	}
	/**
	 * Retourne si le true si le noeud peut router
	 * @return routing
	 */
	public boolean isRouting() {
		return routing;
	}
	/**
	 * URI du port entrant du routeur
	 * @return URI du port entrant du routeur
	 */
	public String getRoutingInboundPortURI() {
		return routingInboundPortURI;
	}
	
	/**
	 * Retourne Position du noeud
	 * @return Position du noeud
	 */
	public PositionI getPosition() {
		return pos;
	}
	
	

}

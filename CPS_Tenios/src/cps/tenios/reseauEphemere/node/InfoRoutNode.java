package cps.tenios.reseauEphemere.node;

import cps.tenios.reseauEphemere.interfaces.AddressI;

/**
 * Classe permettant de regrouper l'adressen le port de communication ainsi que le port de routage
 * @author Tenios
 *
 */
public class InfoRoutNode {
	
	/**
	 * Addresse d'un noeud
	 */
	private AddressI address;
	/**
	 * Port de communication
	 */
	private CommunicationOutboundPort node;
	/**
	 * Port de routage
	 */
	private RoutingOutboundPort rout;

	/**
	 * Permet de construire l'objet
	 * @param label addresse du noeud
	 * @param node port de communication
	 * @param rout port de routage
	 */
	public InfoRoutNode(AddressI address, CommunicationOutboundPort node, RoutingOutboundPort rout) {
		super();
		this.address = address;
		this.node = node;
		this.rout = rout;
	}

	/**
	 * Retourne l'addresse stocker
	 * @return l'addresse  stocker dans l'objet
	 */
	//TODO refactor address : il faut 2 d
	public AddressI getAdress() {
		return address;
	}
	
	/**
	 * Retoune le port de communication
	 * @return le port de communication
	 */
	public CommunicationOutboundPort getNode() {
		return node;
	}
	
	/**
	 * Permet de changer l'addresse stocker
	 * @param label l'addresse que l'on veut stocker
	 */
	public void setAddress(AddressI address) {
		this.address = address;
	}
	
	/**
	 * Permet de changer le port de communication stocker
	 * @param node le nouveau noeud de communication a stocker
	 */
	public void setNode(CommunicationOutboundPort node) {
		this.node = node;
	}

	/**
	 * Retourne le port de routage
	 * @return le port de routage
	 */
	public RoutingOutboundPort getRout() {
		return rout;
	}

	/**
	 * Permet de changer le port de routage stocker
	 * @param rout le port de routage stocker
	 */
	public void setRout(RoutingOutboundPort rout) {
		this.rout = rout;
	}

}

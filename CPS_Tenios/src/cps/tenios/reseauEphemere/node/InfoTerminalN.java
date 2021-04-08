package cps.tenios.reseauEphemere.node;

import cps.tenios.reseauEphemere.interfaces.AddressI;

/**
 * Information relative aux noeuds terminaux
 * @author Tenios
 *
 */
public class InfoTerminalN {

	/**
	 * Addresse du noeud
	 */
	private AddressI addr;
	/**
	 * Port de communication sortant
	 */
	private CommunicationOutboundPort node;
	
	/**
	 * Constructeur
	 * @param addr Addresse du noeud
	 * @param node Port de communication sortant
	 */
	public InfoTerminalN(AddressI addr, CommunicationOutboundPort node) {
		this.addr = addr;
		this.node = node;
	}

	/**
	 * Obtenir l'adresse du noeud
	 * @return adresse du noeud
	 */
	public AddressI getAddress() {
		return addr;
	}
	
	/**
	 * Obtenir le port de communication sortant du noeud
	 * @return port de communication sortant du noeud
	 */
	public CommunicationOutboundPort getNode() {
		return node;
	}
	
	/**
	 * Modifier l'adresse 
	 * @param addr Nouvelle adresse 
	 */
	public void setAddress(AddressI addr) {
		this.addr = addr;
	}
	
	/**
	 * Modifier le port de Communication sortant
	 * @param node Nouveau port de Communication sortant
	 */
	public void setNode(CommunicationOutboundPort node) {
		this.node = node;
	}

}

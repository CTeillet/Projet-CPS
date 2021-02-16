package cps.tenios.reseauEphemere.interfaces;

/**
 * Permet de repr�senter l'adresse d'un noeud interne
 * @author Tenios
 *
 */
public interface NodeAddressI extends AddressI {
	/**
	 * Permet de savoir si le noeud fait partie du r�seau interne
	 */
	boolean isNodeAddress();
	/**
	 * Permet de savoir si le noeud fait partie du r�seau externe
	 */
	boolean isNetworkAddress();
}

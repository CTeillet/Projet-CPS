package cps.tenios.reseauEphemere.interfaces;

/**
 * Permet de représenter l'adresse d'un noeud interne
 * @author Tenios
 *
 */
public interface NodeAddressI extends AddressI {
	/**
	 * Permet de savoir si le noeud fait partie du réseau interne
	 */
	boolean isNodeAddress();
	/**
	 * Permet de savoir si le noeud fait partie du réseau externe
	 */
	boolean isNetworkAddress();
}

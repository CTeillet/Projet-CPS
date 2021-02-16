package cps.tenios.reseauEphemere.interfaces;

/**
 * Permet de représenter une adresse externe au réseau
 * @author Tenios
 *
 */
public interface NetworkAddressI extends AddressI{
	/**
	 * Permet de savoir si l'adresse fait partie du réseau interne
	 */
	boolean isNodeAddress();
	/**
	 * Permet de savoir si le noeud fait partie du réseau externe
	 */
	boolean isNetworkAddress();
}

package cps.tenios.reseauEphemere.interfaces;

/**
 * Permet de repr�senter une adresse externe au r�seau
 * @author Tenios
 *
 */
public interface NetworkAddressI extends AddressI{
	/**
	 * Permet de savoir si l'adresse fait partie du r�seau interne
	 */
	boolean isNodeAddress();
	/**
	 * Permet de savoir si le noeud fait partie du r�seau externe
	 */
	boolean isNetworkAddress();
}

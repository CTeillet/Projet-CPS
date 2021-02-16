package cps.tenios.reseauEphemere.interfaces;

/**
 * Permet de représenter une route pour atteindre un adresse
 * @author Tenios
 *
 */
public interface RouteInfoI {
	/**
	 * Retourne la destination de la route
	 * @return la destiantion de la route
	 */
	AddressI getDestination();
	/**
	 * Retourne le nombre de sauts pour arriver à destination
	 * @return le nombre de sauts pour arriver à destination
	 */
	int getNumberOfHops();
}

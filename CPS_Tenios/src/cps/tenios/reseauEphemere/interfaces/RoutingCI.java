package cps.tenios.reseauEphemere.interfaces;

import java.util.Set;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * Permet de representer un composant routage
 * @author Tenios
 *
 */
public interface RoutingCI extends OfferedCI, RequiredCI{
	/**
	 * Permet de mettre à jour la route la plus optimale vers le 
	 * @param neighbour voisin du noeud courant
	 * @param routes routes vers les adresses
	 */
	void updateRouting(NodeAddressI neighbour, Set<RouteInfoI> routes) throws Exception;
	/**
	 * Permet de mettre à jour la route la plus courte vers un point d'accès
	 * @param neighbour voisins du noeuds courant
	 * @param numberOfHops nombre de saut requis pour y arriver
	 */
	void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception;
	
}

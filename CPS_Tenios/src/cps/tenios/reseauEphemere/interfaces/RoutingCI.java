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
	 * Permet de mettre a jour la route la plus optimale vers la destination et met a jour les noeuds voisins en cas de changement
	 * @param neighbour voisin ayant envoyé les information de stable de routage
	 * @param routes routes vers les adresses
	 * @throws Exception
	 */
	void updateRouting(AddressI neighbour, Set<RouteInfoI> routes) throws Exception;
	/**
	 * Permet de mettre a jour la route la plus courte vers un point d'acces
	 * @param neighbour voisins ayant envoyer les information vers le point d'acces
	 * @param numberOfHops nombre de saut requis pour y arriver
	 * @throws Exception
	 */
	void updateAccessPoint(AddressI neighbour, int numberOfHops) throws Exception;
	
}

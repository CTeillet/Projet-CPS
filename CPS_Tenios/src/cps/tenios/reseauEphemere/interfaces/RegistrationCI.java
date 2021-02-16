package cps.tenios.reseauEphemere.interfaces;

import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
/**
 * Permet de représenter un gestionnaire de réseau
 * @author Tenios
 *
 */
public interface RegistrationCI extends RequiredCI, OfferedCI {

	/**
	 * Permet d'enregistrer un noeud terminal dans le réseau
	 * @param address l'adresse du noeud
	 * @param communicationInboundPortURI le port de communication du noeud
	 * @param initialPosition la position initial du noeud
	 * @param initialRange la portée du noeud
	 * @return l'ensemble de noeud étant voisin du noeud ayant appelé
	 */
	Set<ConnectionInfo> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) throws Exception;
	
	/**
	 * Permet d'enregistrer un noeud point d'accès dans le réseau
	 * @param address l'adresse du noeud
	 * @param communicationInboundPortURI le port de communication du noeud
	 * @param initialPosition la position initial du noeud
	 * @param initialRange la portée du noeud
	 * @param routingInboundPortURI le port de routage du noeud
	 * @return l'ensemble de noeud étant voisin du noeud ayant appelé
	 */
	Set<ConnectionInfo> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception;
	
	/**
	 * Permet d'enregistrer un noeud de routage dans le réseau
	 * @param address l'adresse du noeud
	 * @param communicationInboundPortURI le port de communication du noeud
	 * @param initialPosition la position initial du noeud
	 * @param initialRange la portée du noeud
	 * @param routingInboundPortURI le port de routage du noeud
	 * @return l'ensemble de noeud étant voisin du noeud ayant appelé
	 */
	Set<ConnectionInfo> registerRoutingNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception;
	
	/**
	 * Permet de se désenregistrer du réseau
	 * @param address
	 */
	void unregister (NodeAddressI address) throws Exception;

	
}

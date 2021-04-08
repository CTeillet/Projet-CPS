package cps.tenios.reseauEphemere.interfaces;

import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
/**
 * Permet de repr�senter un gestionnaire de r�seau
 * @author Tenios
 *
 */
public interface RegistrationCI extends RequiredCI, OfferedCI {

	/**
	 * Permet d'enregistrer un noeud terminal dans le r�seau
	 * @param address l'adresse du noeud
	 * @param communicationInboundPortURI le port de communication du noeud
	 * @param initialPosition la position initial du noeud
	 * @param initialRange la port�e du noeud
	 * @return l'ensemble de noeud �tant voisin du noeud ayant appel�
	 * @throws Exception en cas de probleme
	 */
	Set<ConnectionInfo> registerTerminalNode(AddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) throws Exception;
	
	/**
	 * Permet d'enregistrer un noeud point d'acc�s dans le r�seau
	 * @param address l'adresse du noeud
	 * @param communicationInboundPortURI le port de communication du noeud
	 * @param initialPosition la position initial du noeud
	 * @param initialRange la port�e du noeud
	 * @param routingInboundPortURI le port de routage du noeud
	 * @return l'ensemble de noeud �tant voisin du noeud ayant appel�
	 * @throws Exception en cas de probleme
	 */
	Set<ConnectionInfo> registerAccessPoint(AddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception;
	
	/**
	 * Permet d'enregistrer un noeud de routage dans le r�seau
	 * @param address l'adresse du noeud
	 * @param communicationInboundPortURI le port de communication du noeud
	 * @param initialPosition la position initial du noeud
	 * @param initialRange la port�e du noeud
	 * @param routingInboundPortURI le port de routage du noeud
	 * @return l'ensemble de noeud �tant voisin du noeud ayant appel�
	 * @throws Exception en cas de probleme
	 */
	Set<ConnectionInfo> registerRoutingNode(AddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception;
	
	/**
	 * Permet de se d�senregistrer du r�seau
	 * @param address
	 * @throws Exception en cas de probleme
	 */
	void unregister (AddressI address) throws Exception;

	
}

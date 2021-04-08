package cps.tenios.reseauEphemere.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * Permet de la communication entre noeuds 
 * @author Tenios
 *
 */
public interface CommunicationCI extends OfferedCI, RequiredCI {
	/**
	 * Permet de connecter deux noeuds entre eux
	 * @param address adresse du noeud avec lequelle se connecter
	 * @param communicationInboundPortURI port par lequelle la communication passe
	 * @throws Exception en cas de probleme
	 */
	void connect (AddressI address, String communicationInboundPortURI) throws Exception;
	/**
	 * Permet de connecter un noeud de routage avec nous
	 * @param address adresse du noeud avec lequelle se connecter
	 * @param communicationInboundPortURI port par lequelle la communication passe
	 * @param routingInboundPortURI port de routage du noeud
	 * @throws Exception en cas de probleme
	 */
	void connectRouting (AddressI address, String communicationInboundPortURI, String routingInboundPortURI) throws Exception;
	/**
	 * Permet de transmettre un message vers son destinataire
	 * @param m le message � transmettre
	 * @throws Exception en cas de probleme
	 */
	void transmitMessage(MessageI m) throws Exception;
	/**
	 * Permet de v�rifier si une route existe vers une addresse
	 * @param address addresse de destination
	 * @return
	 * @throws Exception en cas de probleme
	 */
	int hasRouteFor(AddressI address) throws Exception;
	/**
	 * Permet de v�rifier si un noeud voisin est toujours vivant
	 * @throws Exception en cas de probleme
	 */
	void ping() throws Exception;
}

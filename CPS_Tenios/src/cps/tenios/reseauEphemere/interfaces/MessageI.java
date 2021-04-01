package cps.tenios.reseauEphemere.interfaces;

import java.io.Serializable;

/**
 * 
 * @author Tenios
 *
 */
public interface MessageI {
	/**
	 * Permet de retourner l'adresse de destination
	 * @return l'adresse de destination
	 */
	AddressI getAddress();
	/**
	 * Retourne le contenu du message
	 * @return le contenu
	 */
	Serializable getContent();
	/**
	 * Verifie si le message est toujours en vie
	 * @return vraie ou faux en fonction de si le m
	 */
	boolean stillAlive();
	/**
	 * Decremente le nombre de saut
	 */
	void decrementsGops();
}

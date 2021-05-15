package cps.tenios.reseauEphemere.interfaces;

import java.io.Serializable;

/**
 * Permet de representer une addresse
 * @author Tenios
 *
 */
public interface AddressI extends Serializable {
	
	/**
	 * Permet de v�rifier si l'addresse est une addresse interne au r�seau
	 * @return retourne si c'est une adresse interne
	 */
	boolean isNodeAddress();
	
	/**
	 * Permet de v�rifier si l'adresse est une addresse externe au r�seau
	 * @return si c'est une addresse externe
	 */
	boolean isNetworkAddress();
	
	/**
	 * Permet de comparer deux addresses
	 * @param a l'addresse avec laquelle comparer
	 * @return si les deux addresses sont identiques
	 */
	boolean equals(AddressI a);
}

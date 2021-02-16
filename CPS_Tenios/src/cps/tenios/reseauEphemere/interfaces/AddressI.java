package cps.tenios.reseauEphemere.interfaces;

/**
 * Permet de representer une addresse
 * @author Tenios
 *
 */
public interface AddressI {
	
	/**
	 * Permet de vérifier si l'addresse est une addresse interne au réseau
	 * @return retourne si c'est une adresse interne
	 */
	boolean isNodeAddress();
	
	/**
	 * Permet de vérifier si l'adresse est une addresse externe au réseau
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

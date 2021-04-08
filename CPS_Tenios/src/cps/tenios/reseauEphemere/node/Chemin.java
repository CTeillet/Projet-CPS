package cps.tenios.reseauEphemere.node;

/**
 * Classe permettant de representer un chemin vers un autre noeud du reseau
 * @author Tenios
 *
 */
public class Chemin {

	/**
	 * Port vers l'autre noeud du reseau
	 */
	private CommunicationOutboundPort next;
	/**
	 * Nombre de saut pour y arriver
	 */
	private int numberOfHops;
	
	/**
	 * Permet de creer un Chemin
	 * @param next port vers l'autre noeud
	 * @param numberOfHops nombre de saut
	 */
	public Chemin(CommunicationOutboundPort next, int numberOfHops) {
		this.next = next;
		this.numberOfHops = numberOfHops;
	}

	/**
	 * Retourne le port 
	 * @return le port de communication
	 */
	public CommunicationOutboundPort getNext() {
		return next;
	}

	/**
	 * Permet de changer le port de communication
	 * @param next le nouveau port de communication
	 */
	public void setNext(CommunicationOutboundPort next) {
		this.next = next;
	}

	/**
	 * Le nombre de saut
	 * @return le nombre de saut
	 */
	public int getNumberOfHops() {
		return numberOfHops;
	}

	/**
	 * Permet de cahnger le nombre de saut
	 * @param numberOfHops
	 */
	public void setNumberOfHops(int numberOfHops) {
		this.numberOfHops = numberOfHops;
	}
	
	
	
}

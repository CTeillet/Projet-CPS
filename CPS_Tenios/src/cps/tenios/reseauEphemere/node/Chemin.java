package cps.tenios.reseauEphemere.node;

import java.util.Comparator;

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
	 * Permet de changer le nombre de saut
	 * @param numberOfHops Nouveau nommbre de saut
	 */
	public void setNumberOfHops(int numberOfHops) {
		this.numberOfHops = numberOfHops;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((next == null) ? 0 : next.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chemin other = (Chemin) obj;
		if (next == null) {
			if (other.next != null)
				return false;
		} else {
			try {
				return next.getClientPortURI() == other.next.getClientPortURI();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
		}
		return false;
	}
}




package cps.tenios.reseauEphemere.interfaces;

import java.io.Serializable;

/**
 * Permet de repr�senter la position d'un noeud
 * @author Tenios
 *
 */
public interface PositionI extends Serializable {
	/**
	 * Retourne la distance entre la position appelante et la position courante
	 * @param other la postion avec laquelle on veut savoir la distance
	 * @return la distance entre les deux points
	 */
	double distance(PositionI other);
}

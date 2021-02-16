package cps.tenios.reseauEphemere.interfaces;

/**
 * Permet de représenter la position d'un noeud
 * @author Tenios
 *
 */
public interface PositionI {
	/**
	 * Retourne la distance entre la position appelante et la position courante
	 * @param other la postion avec laquelle on veut savoir la distance
	 * @return la distance entre les deux points
	 */
	double distance(PositionI other);
}

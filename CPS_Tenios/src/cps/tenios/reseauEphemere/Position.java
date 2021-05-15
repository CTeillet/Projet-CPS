package cps.tenios.reseauEphemere;

import cps.tenios.reseauEphemere.interfaces.PositionI;
/**
 * Defini la position d'un noeud
 * @author Tenios
 *
 */
public class Position implements PositionI {
	/**
	 * Utilisé pour la Serialization
	 */
	private static final long serialVersionUID = -5325457738448186339L;
	/**
	 * abscisse
	 */
	protected double x;
	/**
	 * ordonnee
	 */
	private double y;
	
	/**
	 * Constructeur de Position
	 * @param x abscisse
	 * @param y ordonnee
	 */
	public Position(double x, double y) {
		this.x=x;
		this.y=y;
	}

	@Override
	public double distance(PositionI other) {
		Position p = (Position) other;
		return Math.sqrt( Math.pow((x-p.x), 2) + Math.pow((y-p.y), 2) );
	}

	

}

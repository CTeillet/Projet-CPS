package cps.tenios.reseauEphemere;

import cps.tenios.reseauEphemere.interfaces.PositionI;

public class Position implements PositionI {
	protected double x;
	private double y;
	
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

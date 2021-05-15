package cps.tenios.reseauEphemere.Test.junit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cps.tenios.reseauEphemere.Position;
import cps.tenios.reseauEphemere.gestionnaireReseau.GestionnaireReseau;

class TestPosition {
	

	@Test
	void testValeur() {
		Position p = new Position(0, 0);
		assertEquals(p.getX(), 0);
		assertEquals(p.getY(), 0);
	}
	
	@Test
	void testDistance() {
		Position p1 = new Position(0, 0);
		Position p2 = new Position(1, 0);
		assertEquals(p1.distance(p2), 1);
	}

}

package cps.tenios.reseauEphemere.Test.junit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import cps.tenios.reseauEphemere.NetworkAddress;
import cps.tenios.reseauEphemere.NodeAddress;

class TestNodeAddress {

	@Test
	void testType() {
		NodeAddress n = new NodeAddress(0, 0);
		assertTrue(n.isNodeAddress());
		assertFalse(n.isNetworkAddress());
	}
	
	@Test
	void testEquals() {
		NodeAddress n1 = new NodeAddress(0, 0);
		NodeAddress n2 = new NodeAddress(0, 0);
		assertTrue(n2.equals(n1));
		
		NetworkAddress nw = new NetworkAddress(0, 0);
		assertFalse(n1.equals(nw));
	}

}

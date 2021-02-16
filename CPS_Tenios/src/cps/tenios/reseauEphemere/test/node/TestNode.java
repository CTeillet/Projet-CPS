package cps.tenios.reseauEphemere.test.node;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import cps.tenios.reseauEphemere.node.Node;
import cps.tenios.reseauEphemere.node.TerminalNode;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;

public class TestNode {

		
	@Test
	void testConstructeurNode() {
		String uriInboundPort = AbstractPort.generatePortURI();
		try {
			String uri = AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {uriInboundPort});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//assertEquals(null);
	}
	

}

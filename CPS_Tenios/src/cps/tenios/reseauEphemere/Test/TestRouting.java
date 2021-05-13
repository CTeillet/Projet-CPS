package cps.tenios.reseauEphemere.Test;

import cps.tenios.reseauEphemere.NodeAddress;
import cps.tenios.reseauEphemere.RegistrationConnector;
import cps.tenios.reseauEphemere.gestionnaireReseau.GestionnaireReseau;
import cps.tenios.reseauEphemere.node.RoutingNode;
import cps.tenios.reseauEphemere.node.TerminalNode;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class TestRouting extends AbstractCVM {

	public TestRouting() throws Exception {
	}

	public static void main(String[] args) {
		try {
			TestRouting c = new TestRouting();
			c.startStandardLifeCycle(6000L);
			System.exit(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void deploy() throws Exception {
		try {
			String [] uri = new String [5];
			NodeAddress [] addr = new NodeAddress[5];
			for (int i = 0; i < uri.length; i++) {	
				uri[i] = AbstractPort.generatePortURI();
				addr[i] = new NodeAddress(1, i);
			}


			AbstractComponent.createComponent(GestionnaireReseau.class.getCanonicalName(),new Object[] {});

			String[] routingURI = new String [5];
			for (int i = 0; i < uri.length; i++) {
				routingURI[i] = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {uri[i], addr[i], i*5, 10, 15.});
			}

			for (int i = 0; i < uri.length; i++) {
				this.doPortConnection(routingURI[i], uri[i],  GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());

			}

			super.deploy();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}

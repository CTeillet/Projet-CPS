package cps.tenios.reseauEphemere.Test;

import cps.tenios.reseauEphemere.RegistrationConnector;
import cps.tenios.reseauEphemere.gestionnaireReseau.GestionnaireReseau;
import cps.tenios.reseauEphemere.node.RoutingNode;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class TestRouting extends AbstractCVM {

	public TestRouting() throws Exception {
	}

	public static void main(String[] args) {
		try {
			TestRouting c = new TestRouting();
			c.startStandardLifeCycle(60000L);
			System.exit(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	public void deploy() throws Exception {
		try {
			String uriRouting1 = AbstractPort.generatePortURI();
			String uriRouting2 = AbstractPort.generatePortURI();
			
			AbstractComponent.createComponent(GestionnaireReseau.class.getCanonicalName(),new Object[] {});
			
			String routingURI1 = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {uriRouting1, 0, 10, 15.});
			String routingURI2 = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {uriRouting2, 0, 10, 15.});
			
			this.doPortConnection(routingURI1, uriRouting1,  GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
			this.doPortConnection(routingURI2, uriRouting2,  GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
	
			super.deploy();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}

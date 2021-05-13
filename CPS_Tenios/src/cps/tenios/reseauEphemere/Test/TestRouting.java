package cps.tenios.reseauEphemere.Test;

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
			String uri1 = AbstractPort.generatePortURI();
			String uri2 = AbstractPort.generatePortURI();
			String uri3 = AbstractPort.generatePortURI();
			String uri4 = AbstractPort.generatePortURI();
			String uri5 = AbstractPort.generatePortURI();

			
			AbstractComponent.createComponent(GestionnaireReseau.class.getCanonicalName(),new Object[] {});
			
			String routingURI1 = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {uri1, 0, 10, 15.});
			String routingURI2 = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {uri2, 0, 10, 15.});
			String routingURI3 = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {uri3, 0, 10, 15.});
			
			String terURI1 = AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {uri4, 0, 0, 15.});
			String terURI2 = AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {uri5, 0, 0, 15.});
			
			this.doPortConnection(routingURI1, uri1,  GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
			this.doPortConnection(routingURI2, uri2,  GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
			this.doPortConnection(routingURI3, uri3,  GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
			
			this.doPortConnection(terURI1, uri4,  GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
			this.doPortConnection(terURI2, uri5,  GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
	
			super.deploy();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}

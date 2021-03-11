package cps.tenios.reseauEphemere;

import cps.tenios.reseauEphemere.gestionnaireReseau.GestionnaireReseau;
import cps.tenios.reseauEphemere.node.AccessPointNode;
import cps.tenios.reseauEphemere.node.RoutingNode;
import cps.tenios.reseauEphemere.node.TerminalNode;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM extends AbstractCVM {

	public CVM() throws Exception {		
	}
	
	@Override
	public void deploy() throws Exception {
		String uriTerminal1 = AbstractPort.generatePortURI();
		String uriTerminal2 = AbstractPort.generatePortURI();
		String uriTerminal3 = AbstractPort.generatePortURI();
		
		String uriRouting1 = AbstractPort.generatePortURI();
		
		String uriAccess1 = AbstractPort.generatePortURI();
		
		AbstractComponent.createComponent(GestionnaireReseau.class.getCanonicalName(),new Object[] {});
		
		String terminalURI =  AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(),new Object[] {uriTerminal1, 0, 0, 15.}); // Calculator dan
		String terminalURI2 = AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {uriTerminal2, 5, 5, 15.});
		String terminalURI3 = AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {uriTerminal3, 20, 20, 15.});
		
		String routingURI1 = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {uriRouting1, 0, 10, 15.});
		
		String accessURI1 = AbstractComponent.createComponent(AccessPointNode.class.getCanonicalName(), new Object[] {uriAccess1, 10, 10, 15.});

		this.doPortConnection(terminalURI, uriTerminal1, GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
		this.doPortConnection(routingURI1, uriRouting1,  GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
		this.doPortConnection(terminalURI2, uriTerminal2, GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
		this.doPortConnection(terminalURI3, uriTerminal3, GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
		this.doPortConnection(accessURI1, uriAccess1, GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
		
		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVM c = new CVM();
			c.startStandardLifeCycle(60000L);
			System.exit(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}

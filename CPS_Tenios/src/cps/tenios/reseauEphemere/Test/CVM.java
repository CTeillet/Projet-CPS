package cps.tenios.reseauEphemere.Test;

import cps.tenios.reseauEphemere.NodeAddress;
import cps.tenios.reseauEphemere.RegistrationConnector;
import cps.tenios.reseauEphemere.gestionnaireReseau.GestionnaireReseau;
import cps.tenios.reseauEphemere.node.AccessPointNode;
import cps.tenios.reseauEphemere.node.RoutingNode;
import cps.tenios.reseauEphemere.node.TerminalNode;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM extends AbstractCVM {

	private final int idMachine;
	
	
	public CVM(int idMachine) throws Exception {
		super();
		this.idMachine = idMachine;
	}

	@Override
	public void deploy() throws Exception {
		
		int nbComposant = 8;
		
		String [] composantsURIs = new String[nbComposant];
		NodeAddress [] addresses = new NodeAddress[nbComposant];

		for (int i = 0; i < nbComposant; i++) {
			composantsURIs[i]  = AbstractPort.generatePortURI();
			addresses[i] = new NodeAddress(idMachine, i+1);
		}
		
		AbstractComponent.createComponent(GestionnaireReseau.class.getCanonicalName(),new Object[] {});
		
		String terminalURI1 =  AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(),new Object[] {composantsURIs[0], addresses[0], 0, 0, 15.});
		String terminalURI2 = AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {composantsURIs[1], addresses[1], 5, 5, 15.});
		String terminalURI3 = AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {composantsURIs[2], addresses[2], 30, 30, 15.});
		
		String routingURI1 = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {composantsURIs[3], addresses[3], 0, 10, 15.});
		String routingURI2 = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {composantsURIs[4], addresses[4], 10, 20, 15.});
		String routingURI3 = AbstractComponent.createComponent(RoutingNode.class.getCanonicalName(), new Object[] {composantsURIs[5], addresses[5], 20, 30, 15.});
		
		String accessURI1 = AbstractComponent.createComponent(AccessPointNode.class.getCanonicalName(), new Object[] {composantsURIs[6], addresses[6], 10, 10, 15.});
		String accessURI2 = AbstractComponent.createComponent(AccessPointNode.class.getCanonicalName(), new Object[] {composantsURIs[7], addresses[7], 30, 25, 15.});

		this.doPortConnection(terminalURI1, composantsURIs[0], GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
		this.doPortConnection(terminalURI2, composantsURIs[1], GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
		this.doPortConnection(terminalURI3, composantsURIs[2], GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
		
		
		this.doPortConnection(routingURI1, composantsURIs[3],  GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
		this.doPortConnection(routingURI2, composantsURIs[4],  GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
		this.doPortConnection(routingURI3, composantsURIs[5],  GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
		
		this.doPortConnection(accessURI1, composantsURIs[6], GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
		this.doPortConnection(accessURI2, composantsURIs[7], GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
		
		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVM c = new CVM(1);
			c.startStandardLifeCycle(6000L);
			System.exit(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}

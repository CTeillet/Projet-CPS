package cps.tenios.reseauEphemere;

import cps.tenios.reseauEphemere.gestionnaireReseau.GestionnaireReseau;
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
		String gestionReseauURI = AbstractComponent.createComponent(GestionnaireReseau.class.getCanonicalName(),new Object[] {});
		String terminalURI =  AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(),new Object[] {uriTerminal1}); // Calculator dan
		String terminalURI2 = AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(), new Object[] {uriTerminal2});
		//System.out.println("GR : " + gestionReseauURI);
		this.doPortConnection(terminalURI, uriTerminal1, GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
		this.doPortConnection(terminalURI2, uriTerminal2, GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVM c = new CVM();
			c.startStandardLifeCycle(10000L);
			System.exit(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}

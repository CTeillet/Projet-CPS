package cps.tenios.reseauEphemere;

import cps.tenios.reseauEphemere.gestionnaireReseau.GestionnaireReseau;
import cps.tenios.reseauEphemere.node.TerminalNode;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class CVM extends AbstractCVM {

	public CVM() throws Exception {		
	}
	
	@Override
	public void deploy() throws Exception {
		
		String gestionReseauURI = AbstractComponent.createComponent(GestionnaireReseau.class.getCanonicalName(),new Object[] {});
		String terminalURI =  AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(),new Object[] {});
		this.doPortConnection(terminalURI, TerminalNode.REGISTRATION_URI, GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
		super.deploy();
	}

	public static void main(String[] args) {
		try {
			CVM c = new CVM();
			c.startStandardLifeCycle(5000L);
			System.exit(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}

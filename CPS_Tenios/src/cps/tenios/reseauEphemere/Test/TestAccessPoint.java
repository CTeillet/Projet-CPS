package cps.tenios.reseauEphemere.Test;

import cps.tenios.reseauEphemere.RegistrationConnector;
import cps.tenios.reseauEphemere.gestionnaireReseau.GestionnaireReseau;
import cps.tenios.reseauEphemere.node.AccessPointNode;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class TestAccessPoint extends AbstractCVM {

	public TestAccessPoint() throws Exception {
	}
	
	public static void main(String[] args) {
		try {
			TestAccessPoint c = new TestAccessPoint();
			c.startStandardLifeCycle(10000L);
			System.exit(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	public void deploy() throws Exception {
		try {
					String uriAccess1 = AbstractPort.generatePortURI();
		String uriAccess2 = AbstractPort.generatePortURI();
		
		AbstractComponent.createComponent(GestionnaireReseau.class.getCanonicalName(),new Object[] {});
		
		String access1 = AbstractComponent.createComponent(AccessPointNode.class.getCanonicalName(), new Object[] {uriAccess1, 0, 10, 15.});
		String access2 = AbstractComponent.createComponent(AccessPointNode.class.getCanonicalName(), new Object[] {uriAccess2, 0, 10, 15.});
		
		this.doPortConnection(access1, uriAccess1,  GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
		this.doPortConnection(access2, uriAccess2,  GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
		}catch (Exception e) {
			e.printStackTrace();
		}

	}

}

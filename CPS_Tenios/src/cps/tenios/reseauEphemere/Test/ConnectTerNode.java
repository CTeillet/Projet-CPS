package cps.tenios.reseauEphemere.Test;

import java.util.ArrayList;

import cps.tenios.reseauEphemere.RegistrationConnector;
import cps.tenios.reseauEphemere.gestionnaireReseau.GestionnaireReseau;
import cps.tenios.reseauEphemere.node.AccessPointNode;
import cps.tenios.reseauEphemere.node.RoutingNode;
import cps.tenios.reseauEphemere.node.TerminalNode;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.cvm.AbstractCVM;

public class ConnectTerNode extends AbstractCVM {

	public ConnectTerNode() throws Exception {		
	}
	
	@Override
	public void deploy() throws Exception {
		
		ArrayList<String> nodesURI = new ArrayList<>();
		ArrayList<String> componentsURI = new ArrayList<>();
		for(int i=0; i < 6; i++) {
			String uri = AbstractPort.generatePortURI();
			nodesURI.add(uri);
			componentsURI.add( AbstractComponent.createComponent(TerminalNode.class.getCanonicalName(),new Object[] {uri, i, i, 2.0}));
		}
		
		AbstractComponent.createComponent(GestionnaireReseau.class.getCanonicalName(),new Object[] {});
		
		for(int i=0; i < 6; i++) {
			this.doPortConnection(componentsURI.get(i), nodesURI.get(i), GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
		}
		super.deploy();
	}

	public static void main(String[] args) {
		try {
			ConnectTerNode c = new ConnectTerNode();
			c.startStandardLifeCycle(60000L);
			System.exit(0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

}

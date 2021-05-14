package cps.tenios.reseauEphemere.Test;

import cps.tenios.reseauEphemere.NodeAddress;
import cps.tenios.reseauEphemere.RegistrationConnector;
import cps.tenios.reseauEphemere.gestionnaireReseau.GestionnaireReseau;
import cps.tenios.reseauEphemere.node.AccessPointNode;
import cps.tenios.reseauEphemere.node.RoutingNode;
import cps.tenios.reseauEphemere.node.TerminalNode;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;

public class DistributedCVM extends AbstractDistributedCVM {
 
	protected int nbComposant = 8;
	protected String jvmGestionnaire;
	protected String[] jvmComposantURI;
	
	protected String[] composantsURI;
	protected String[] uri;
	protected NodeAddress[] addresses;
	
	public DistributedCVM(String[] args) throws Exception {
		super(args);
		
	}
	@Override
	public void instantiateAndPublish() throws Exception {
		
		if (AbstractCVM.getThisJVMURI().equals(jvmGestionnaire)) {
			AbstractComponent.createComponent(GestionnaireReseau.class.getCanonicalName(),new Object[] {});
		
		} else {
			
			int i;
			for(i=0; i < jvmComposantURI.length; i++) {
				if (AbstractCVM.getThisJVMURI().equals(jvmComposantURI[i])) {
					
					String kind;
					if (i < 3) {
						kind = TerminalNode.class.getCanonicalName();
					} else if (i < 6) {
						kind = RoutingNode.class.getCanonicalName();
					} else {
						kind = AccessPointNode.class.getCanonicalName();
					}
					
					uri[i] = AbstractComponent.createComponent(kind,new Object[] {composantsURI[i], addresses[i], (int) (Math.random() * 25), (int) (Math.random() * 25), 15.});
					
					break;
				}
			}
			
			if (i == jvmComposantURI.length) {
				System.out.println("Unknown JVM URI : "
						+ AbstractCVM.getThisJVMURI());
			}
		}
		
		super.instantiateAndPublish();
	}
	@Override
	public void interconnect() throws Exception {

		if (!AbstractCVM.getThisJVMURI().equals(jvmGestionnaire)) {
			int i;
			for(i=0; i < jvmComposantURI.length; i++) {
				if (AbstractCVM.getThisJVMURI().equals(jvmComposantURI[i])) {
					this.doPortConnection(uri[i], composantsURI[i], GestionnaireReseau.INBOUNDPORT_URI, RegistrationConnector.class.getCanonicalName());
					break;
				}
			}
			if (i == jvmComposantURI.length) {
				System.out.println("Unknown JVM URI : "
						+ AbstractCVM.getThisJVMURI());
			}
		}
		super.interconnect();
	}
	public static void main(String[] args) {

		try {
			DistributedCVM dcvm = new DistributedCVM(args);
			
			dcvm.startStandardLifeCycle(2000L);
			Thread.sleep(5000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

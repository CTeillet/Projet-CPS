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
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.components.helpers.CVMDebugModes;

public class DistributedCVM extends AbstractDistributedCVM {
	
	// uri des jvm
	protected final String GESTIONNAIRE_JVM_URI = "gestionnaire_jvm_uri";
	protected final String NODES_JVM_URI = "nodes_jvm_uri";
	
	// uri des composant
	protected final String GESTIONNAIRE_COMPO_URI = "gestionnaire_compo_uri";
	protected final String NODES_COMPO_URI = "nodes_compo_uri";
	
	
	protected final int nbNodes = 8; // nombre de nodes
	protected final String[] nodesPortURIs = new String[nbNodes]; 
	protected final String[] nodesObjectURIs = new String[nbNodes];
	private NodeAddress[] addresses = new NodeAddress[nbNodes];
	private String gestionnaireURI;
	

	public DistributedCVM(String[] args) throws Exception {
		super(args);
		for (int i = 0; i < nbNodes; i++) {
			nodesPortURIs[i]  = AbstractPort.generatePortURI();
			addresses[i] = new NodeAddress(0, i+1);
		}
	}
	
	public DistributedCVM(String[] args, int xLayout, int yLayout) throws Exception {
		super(args, xLayout, yLayout);
		for (int i = 0; i < nbNodes; i++) {
			nodesPortURIs[i]  = AbstractPort.generatePortURI();
			addresses[i] = new NodeAddress(0, i+1);
		}
	}
	

	@Override
	public void initialise() throws Exception {
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.LIFE_CYCLE);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.INTERFACES);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.PORTS);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CONNECTING);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.CALLING);
		AbstractCVM.DEBUG_MODE.add(CVMDebugModes.EXECUTOR_SERVICES);

		super.initialise();
	}

	@Override
	public void instantiateAndPublish() throws Exception {
		
		if (thisJVMURI.equals(GESTIONNAIRE_JVM_URI)) {
			gestionnaireURI = AbstractComponent.createComponent(GestionnaireReseau.class.getCanonicalName(),new Object[] {});
		
			assert	this.isDeployedComponent(gestionnaireURI);
			// make it trace its operations; comment and uncomment the line to see
			// the difference
			//this.toggleTracing(this.gestionnaireURI );
			//this.toggleLogging(this.gestionnaireURI );
			assert	this.gestionnaireURI  != null;
			
		} else if (thisJVMURI.equals(NODES_JVM_URI)) {
			for (int i = 0; i < nbNodes; i++) {
				String kind;
				if (i < 3) {
					kind = TerminalNode.class.getCanonicalName();
				} else if (i < 6) {
					kind = RoutingNode.class.getCanonicalName();
				} else {
					kind = AccessPointNode.class.getCanonicalName();
				}
				nodesObjectURIs[i]  = AbstractComponent.createComponent(kind,
						new Object[] {nodesPortURIs[i], addresses[i], (int) (Math.random() * 25), (int) (Math.random() * 25), 15.});
			
				assert	this.isDeployedComponent(this.nodesObjectURIs[i]);
				//this.toggleTracing(this.nodesObjectURIs[i]);
				//this.toggleLogging(this.nodesObjectURIs[i]);
				assert	this.nodesObjectURIs[i] != null;
			}
			
			
			// make it trace its operations; comment and uncomment the line to see
			// the difference
			
			assert this.gestionnaireURI == null;
			
		} else {
			System.out.println("Unknown JVM URI : "
					+ AbstractCVM.getThisJVMURI());
		}
		
		super.instantiateAndPublish();
	}

	@Override
	public void interconnect() throws Exception {
		// TODO Auto-generated method stub
		
		if (thisJVMURI.equals(GESTIONNAIRE_JVM_URI)) {
			assert	this.gestionnaireURI != null;
		
		} else if (thisJVMURI.equals(NODES_JVM_URI)) {
			int i;
			for(i=0; i < nbNodes; i++) {

				this.doPortConnection(nodesObjectURIs[i], nodesPortURIs[i],
						GestionnaireReseau.INBOUNDPORT_URI,
						RegistrationConnector.class.getCanonicalName());
				assert	this.nodesObjectURIs[i] != null;
			}
			assert	this.gestionnaireURI == null;
		} else {
			System.out.println("Unknown JVM URI : "
					+ AbstractCVM.getThisJVMURI());
		}
		
		super.interconnect();
	}

	
	
	@Override
	public void finalise() throws Exception {

		if (thisJVMURI.equals(GESTIONNAIRE_JVM_URI)) {
			assert	this.gestionnaireURI != null;
		
		} else if (thisJVMURI.equals(NODES_JVM_URI)) {
			int i;
			for(i=0; i < nbNodes; i++) {

				this.doPortDisconnection(nodesObjectURIs[i], nodesPortURIs[i]);
				assert	this.nodesObjectURIs[i] != null;
			}
			assert	this.gestionnaireURI == null;
		} else {
			System.out.println("Unknown JVM URI : "
					+ AbstractCVM.getThisJVMURI());
		}
		
		super.finalise();
	}

	@Override
	public void shutdown() throws Exception {
		// TODO Auto-generated method stub
		super.shutdown();
	}

	public static void main(String[] args) {

		try {
			DistributedCVM dcvm = new DistributedCVM(args);
			
			dcvm.startStandardLifeCycle(60000L);
			Thread.sleep(600000L);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

package cps.tenios.reseauEphemere.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.NodeAddress;
import cps.tenios.reseauEphemere.NodeConnector;
import cps.tenios.reseauEphemere.Position;
import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.PositionI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
/**
 * Classes représentant un noeud
 * @author Tenios
 *
 */
@OfferedInterfaces(offered = {CommunicationCI.class})
@RequiredInterfaces(required = {CommunicationCI.class, RegistrationCI.class})
public abstract class  Node extends AbstractComponent{
	/**
	 * Compteur du nombre de noeud, utilisé pour les adresses
	 */
	private static int cmp = 0;
	
	/**
	 * URI du port entrants de communication
	 */
	protected final String INBOUNDPORT_URI;
	/**
	 * URI de port sortants vers le gestionnaire réseau
	 */
	private final String REGISTRATION_URI;
	/**
	 * Port entrants pour les communication avec les Noeuds
	 */
	protected NodeInboundPort nodeInboundPort;
	/**
	 * Listes des ports sortants vers les autres Noeuds
	 */
	protected List<NodeOutboundPort> nodesOutboundPort;
	/**
	 * Adresse du noeud
	 */
	protected NodeAddressI addr;
	
	/**
	 * Position du Noeud
	 */
	protected PositionI pos;
	
	/**
	 * Port sortant pour communiquer avec le gestionnaire Reseau
	 */
	protected NodeRegistrationOutboundPort registrationOutboundPort;
	
	/**
	 * Ensemble des voisins du noeud
	 */
	protected Set<ConnectionInfo> voisin;
	/**
	 * Index du noeud
	 */
	protected int index = cmp;
	/**
	 * permet de creer un Composant noeud
	 * @param uri URI du port sortant vers le gestionnaire réseau
	 * @throws Exception s'il y a un probleme
	 */
	protected Node(String uri) throws Exception {
		super(0, 1);
		REGISTRATION_URI = uri;
		INBOUNDPORT_URI = AbstractPort.generatePortURI();
		
		nodeInboundPort = new NodeInboundPort(INBOUNDPORT_URI,this);
		nodeInboundPort.publishPort();
	
		
		nodesOutboundPort = new ArrayList<NodeOutboundPort>();
		
		registrationOutboundPort = new NodeRegistrationOutboundPort(REGISTRATION_URI, this);
		registrationOutboundPort.publishPort();
		
		this.toggleLogging();
		this.toggleTracing();
		
		addr = new NodeAddress(cmp++);
		Random r =new Random();
		pos = new Position(r.nextInt(10), r.nextInt(10));
	}
	
	@Override
	public abstract void execute() throws Exception;

	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(REGISTRATION_URI);
		for(NodeOutboundPort np : nodesOutboundPort) this.doPortDisconnection(np.getPortURI());
		//for(NodeInboundPort np : nodesInboundPort) this.doPortDisconnection(np.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.registrationOutboundPort.unpublishPort();
			this.nodeInboundPort.unpublishPort();
			for(NodeOutboundPort np : nodesOutboundPort) np.unpublishPort();
			//for(NodeInboundPort np : nodesInboundPort)np.unpublishPort();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
	
	/**
	 * Se connecte avec le noeud d'adresse address, et le rajoute à ses voisins
	 * @param address l'adresse du port avec lequel on veut se connecter 
	 * @param communicationInboundPortURI l'uri du port avec lequel on veut se connecter
	 * @throws Exception s'il y a un probleme
	 */
	public void connect(NodeAddressI address, String communicationInboundPortURI) throws Exception {
		voisin.add(new ConnectionInfo(address, communicationInboundPortURI, false, "", null));
		logMessage("Ajout de voisin" + voisin.size());
		connection(communicationInboundPortURI);
		logMessage("Ajout de outbound" + nodesOutboundPort.size());
	}
	
	/**
	 * Permet de se connecter avec le port entré du composant
	 * @param communicationInboundPortURI 
	 * @return le port de sortie du composant
	 * @throws Exception s'il y a un probleme
	 */
	protected NodeOutboundPort connection(String communicationInboundPortURI) throws Exception {
		//Connexion à l'uriInbound
		NodeOutboundPort nodeOutbound = new NodeOutboundPort(this);
		nodeOutbound.publishPort();
		nodesOutboundPort.add(nodeOutbound);
		try {
			doPortConnection(nodeOutbound.getPortURI(), communicationInboundPortURI, NodeConnector.class.getCanonicalName());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return nodeOutbound;
	}

	/**
	 * Permet de connecter un noeud de routage
	 * @param address l'adresse du noeud
	 * @param communicationInboundPortURI l'adresse du port de communication de l'autre noeud
	 * @param routingInboundPortURI l'adresse de routage de l'autre noeud
	 * @throws Exception s'il y a un probleme
	 */
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI)
			throws Exception {
		voisin.add(new ConnectionInfo(address, communicationInboundPortURI, true, routingInboundPortURI, null));
		logMessage("Ajout de voisin" + voisin.size());
		connection(communicationInboundPortURI);
		logMessage("Ajout de outbound" + nodesOutboundPort.size());
	}
	
	/**
	 * Permet de recevoir un message. S'il nous est attribué ou qu'il n'a plus de saut, alors on arrete de le transmettre
	 * sinon on le transmet à nos voisins
	 * @param m le message que l'on veut transmettre
	 * @throws Exception s'il y a un probleme
	 */
	public void transmitMessage(MessageI m) throws Exception{
		m.decrementsGops();
		logMessage("Message en transit" + m);
		if(m.getAddress().equals(addr)) {
			logMessage("Message recue " + m.getContent());
			return;
		}
		if(!m.stillAlive()) {
			logMessage("Mort du Message");
			return;
		}
		boolean temp = false;
//		for (NodeOutboundPort n : nodesOutboundPort) {
//			if(n.hasRouteFor(m.getAddress())) {
//				temp=true;
//			}
//		}
//		
		if(!temp) {
			logMessage("Taille Outbound " + nodesOutboundPort.size());
			for (NodeOutboundPort n : nodesOutboundPort) {
				logMessage("Dans For");
				
				n.transmitMessage(m);
			}
		}
	}
	
	/**
	 * Verifie qu'il existe un chemin entre nous et l'adresse
	 * @param address l'address de destination
	 * @return si une jonction existe
	 * @throws Exception s'il y a un probleme
	 */
	public boolean hasRouteFor(AddressI address) throws Exception{
		return false;
	}
	
	/**
	 * Permet de vérifier que les voisins sont 
	 * @throws Exception s'il y a un probleme
	 */
	public abstract void ping() throws Exception;
	
	/**
	 * Permet de connaitre le nombre de noeud crée au total
	 * @return le nombre total de noeud
	 */
	public static int getCmp() {
		return cmp;
	}
	
	/**
	 * Permet d'avoir l'addresse d'un noeud
	 * @return l'adresse d'un noeud
	 */
	public NodeAddressI getAddr() {
		return addr;
	}
	
}

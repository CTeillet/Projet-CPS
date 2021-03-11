package cps.tenios.reseauEphemere.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.NodeAddress;
import cps.tenios.reseauEphemere.NodeConnector;
import cps.tenios.reseauEphemere.Position;
import cps.tenios.reseauEphemere.RoutingNodeConnector;
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
 * Classes repr�sentant un noeud
 * @author Tenios
 *
 */
@OfferedInterfaces(offered = {CommunicationCI.class})
@RequiredInterfaces(required = {CommunicationCI.class, RegistrationCI.class})
public abstract class  Node extends AbstractComponent{
	/**
	 * Compteur du nombre de noeud, utilis� pour les adresses
	 */
	private static int cmp = 0;
	
	/**
	 * URI du port entrants de communication
	 */
	protected final String COMM_INBOUNDPORT_URI;
	/**
	 * URI de port sortants vers le gestionnaire r�seau
	 */
	private final String REGISTRATION_URI;
	/**
	 * Port entrants pour les communication avec les Noeuds
	 */
	protected NodeInboundPort nodeInboundPort;
	/**
	 * Listes des ports sortants vers les autres Noeuds
	 */
	//protected List<NodeOutboundPort> nodesOutboundPort;
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
	
	// TODO : refair jav doc et voir si util
	protected List<Pair<NodeAddressI, NodeOutboundPort>> terminalNodes;
	protected List<Triplet<NodeAddressI, NodeOutboundPort, RoutingNodeOutboundPort>> routingNodes;
	//protected Set<ConnectionInfo> voisin;
	/**
	 * Index du noeud
	 */
	protected int index = cmp;
	/**
	 * permet de creer un Composant noeud
	 * @param uri URI du port sortant vers le gestionnaire r�seau
	 * @throws Exception s'il y a un probleme
	 */
	protected Node(String uri) throws Exception {
		super(5, 10);
		REGISTRATION_URI = uri;
		COMM_INBOUNDPORT_URI = AbstractPort.generatePortURI();
		
		nodeInboundPort = new NodeInboundPort(COMM_INBOUNDPORT_URI,this);
		nodeInboundPort.publishPort();
	
		
		//nodesOutboundPort = new ArrayList<NodeOutboundPort>();
		
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
		//Deconnexion des ports
		this.doPortDisconnection(REGISTRATION_URI);

		for(Pair<NodeAddressI, NodeOutboundPort> node : terminalNodes) {
			this.doPortDisconnection(node.getNode().getClientPortURI());
		}
		for(Triplet<NodeAddressI, NodeOutboundPort, RoutingNodeOutboundPort> node : routingNodes) {
			this.doPortDisconnection(node.getNode().getClientPortURI());
			this.doPortDisconnection(node.getRout().getClientPortURI());
		}
		
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			//Depublication des ports
			this.nodeInboundPort.unpublishPort();
			//ports sortant
			this.registrationOutboundPort.unpublishPort();
			for(Pair<NodeAddressI, NodeOutboundPort> node : terminalNodes) {
				node.getNode().unpublishPort();
			}
			for(Triplet<NodeAddressI, NodeOutboundPort, RoutingNodeOutboundPort> node : routingNodes) {
				node.getNode().unpublishPort();
				node.getRout().unpublishPort();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
	
	/**
	 * Se connecte avec le noeud d'adresse address, et le rajoute � ses voisins
	 * @param address l'adresse du port avec lequel on veut se connecter 
	 * @param communicationInboundPortURI l'uri du port avec lequel on veut se connecter
	 * @throws Exception s'il y a un probleme
	 */
	public void connect(NodeAddressI address, String communicationInboundPortURI) throws Exception {
		
		logMessage("Dans connect " + this.addr + ", " + address);
		
		//voisin.add(new ConnectionInfo(address, communicationInboundPortURI, false, "", null));
		//logMessage("Ajout de voisin" + voisin.size());
		NodeOutboundPort nodeOutboundPort = connection(communicationInboundPortURI);
		terminalNodes.add(new Pair<NodeAddressI, NodeOutboundPort>(address, nodeOutboundPort));
		
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
		
		assert this.addr != address;
		
		//voisin.add(new ConnectionInfo(address, communicationInboundPortURI, true, routingInboundPortURI, null));
		//logMessage("Ajout de voisin" + voisin.size());
		
		NodeOutboundPort nodeOutboundPort = connection(communicationInboundPortURI);
		RoutingNodeOutboundPort routingOutboundPort = connectionRouting(routingInboundPortURI);
		routingNodes.add(new Triplet<NodeAddressI, NodeOutboundPort, RoutingNodeOutboundPort>(address, nodeOutboundPort, routingOutboundPort));
	}
	
	/**
	 * Permet de se connecter avec le port entr� du composant
	 * @param communicationInboundPortURI 
	 * @return le port de sortie du composant
	 * @throws Exception s'il y a un probleme
	 */
	protected NodeOutboundPort connection(String communicationInboundPortURI) throws Exception {
		//Connexion a l'uriInbound
		NodeOutboundPort nodeOutbound = new NodeOutboundPort(this);
		nodeOutbound.publishPort();
		//nodesOutboundPort.add(nodeOutbound);
		try {
			doPortConnection(nodeOutbound.getPortURI(), communicationInboundPortURI, NodeConnector.class.getCanonicalName());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return nodeOutbound;
	}
	
	//TODO javadoc
	protected RoutingNodeOutboundPort connectionRouting(String routingInboundPortURI) throws Exception {
		//Connexion au RoutingNodeOutboundPort
		RoutingNodeOutboundPort outbound = new RoutingNodeOutboundPort(this);
		outbound.publishPort();
		//nodesOutboundPort.add(nodeOutbound);
		try {
			doPortConnection(outbound.getPortURI(), routingInboundPortURI, RoutingNodeConnector.class.getCanonicalName());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return outbound;
	}

	
	/**
	 * Permet de recevoir un message. S'il nous est attribu� ou qu'il n'a plus de saut, alors on arrete de le transmettre
	 * sinon on le transmet � nos voisins
	 * @param m le message que l'on veut transmettre
	 * @throws Exception s'il y a un probleme
	 */
	public void transmitMessage(MessageI msg) throws Exception{
		//Copie du message 
		MessageI m = new Message((Message) msg);;
		m.decrementsGops();
		//arriver a destination
		if(m.getAddress().equals(addr)) {
			logMessage("Message recue : " + m.getContent());
			return;
		}
		//Destruction 
		if(!m.stillAlive()) {
			logMessage("Mort du Message");
			return;
		}
		
		//inondation 
		for (Triplet<NodeAddressI, NodeOutboundPort, RoutingNodeOutboundPort> n : routingNodes) {

			n.getNode().transmitMessage(m);
		}
		
		// TODO verfier faire pareil avec noeud Terminal 
		
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
	 * Permet de v�rifier que les voisins sont 
	 * @throws Exception s'il y a un probleme
	 */
	public abstract void ping() throws Exception;
	
	/**
	 * Permet de connaitre le nombre de noeud cr�e au total
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

	@Override
	public String toString() {
		return "Node [index=" + index + "]";
	}
	
	protected NodeOutboundPort getNodeOutboundPort(NodeAddressI adrr) {
		for(Triplet<NodeAddressI, NodeOutboundPort, RoutingNodeOutboundPort> node : routingNodes) {
			if(node.getLabel().equals(adrr)) {
				return node.getNode();
			}
		}
		for(Pair<NodeAddressI, NodeOutboundPort> node : terminalNodes) {
			if(node.getLabel().equals(adrr)) {
				return node.getNode();
			}
		}
		return null;
	}
	
	protected RoutingNodeOutboundPort getRoutingOutboundPort(NodeAddressI adrr) {
		for(Triplet<NodeAddressI, NodeOutboundPort, RoutingNodeOutboundPort> node : routingNodes) {
			if(node.getLabel().equals(adrr)) {
				return node.getRout();
			}
		}
		return null;
	}
	
	
	
}

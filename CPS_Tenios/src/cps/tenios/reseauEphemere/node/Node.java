package cps.tenios.reseauEphemere.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cps.tenios.reseauEphemere.NodeAddress;
import cps.tenios.reseauEphemere.NodeConnector;
import cps.tenios.reseauEphemere.Position;
import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
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
	protected CommunicationInboundPort nodeInboundPort;
	/**
	 * Listes des ports sortants vers les autres Noeuds
	 */
	//protected List<NodeOutboundPort> nodesOutboundPort;
	/**
	 * Adresse du noeud
	 */
	protected AddressI addr;
	
	/**
	 * Position du Noeud
	 */
	protected PositionI pos;
	
	/**
	 * Port sortant pour communiquer avec le gestionnaire Reseau
	 */
	protected RegistrationOutboundPort registrationOutboundPort;
	
	/**
	 * Porter du signal du noeud
	 */
	protected double range;
	
	// TODO :  voir si necessaire
	/**
	 * List des noeud terminaux
	 */
	protected List<InfoTerminalN> terminalNodes;
	/**
	 * List des noeud pouvant router des messages
	 */
	protected List<InfoRoutNode> routingNodes;
	
	/**
	 * Index du noeud
	 */
	protected int index = cmp;
	/**
	 * permet de creer un Composant noeud
	 * @param uri URI du port sortant vers le gestionnaire r�seau
	 * @throws Exception s'il y a un probleme
	 */
	protected Node(String uri, int i, int j, double r) throws Exception {
		super(10, 15);
		REGISTRATION_URI = uri;
		COMM_INBOUNDPORT_URI = AbstractPort.generatePortURI();
		
		nodeInboundPort = new CommunicationInboundPort(COMM_INBOUNDPORT_URI,this);
		nodeInboundPort.publishPort();
		
		registrationOutboundPort = new RegistrationOutboundPort(REGISTRATION_URI, this);
		registrationOutboundPort.publishPort();
		// TODO a verifier
		terminalNodes = new ArrayList<InfoTerminalN>();
		routingNodes = new ArrayList<InfoRoutNode>();
		
		this.toggleLogging();
		this.toggleTracing();
		
		this.range=r;
		addr = (AddressI) new NodeAddress(cmp++);
		pos = new Position(i, j);
	}
	/**
	 * Constructeur de node qui lui attribue des coordonnee et et un rayon de facon aleatoire
	 * @param uri
	 * @throws Exception
	 */
	protected Node(String uri) throws Exception {
		this(uri, (new Random()).nextInt(20), (new Random()).nextInt(20), (new Random()).nextDouble());
	}
	
	@Override
	public abstract void execute() throws Exception;

	@Override
	public synchronized void finalise() throws Exception {
		//Deconnexion des ports
		this.doPortDisconnection(REGISTRATION_URI);

		for(InfoTerminalN node : terminalNodes) {
			this.doPortDisconnection(node.getNode().getClientPortURI());
		}
		for(InfoRoutNode node : routingNodes) {
			logMessage("routing disconnect");
			this.doPortDisconnection(node.getNode().getClientPortURI());
			logMessage("disconnect nodePort");
			this.doPortDisconnection(node.getRout().getClientPortURI());
			logMessage("disconnect routPort");
		}
		logMessage("fin disconnect");
		
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		logMessage("shutdown");
		try {
			//Depublication des ports
			this.nodeInboundPort.unpublishPort();
			//ports sortant
			this.registrationOutboundPort.unpublishPort();
			for(InfoTerminalN node : terminalNodes) {
				node.getNode().unpublishPort();
			}
			for(InfoRoutNode node : routingNodes) {
				logMessage("routing unpublished");
				node.getNode().unpublishPort();
				logMessage("node");
				node.getRout().unpublishPort();
				logMessage("routing");
			}
			logMessage("fin for");
			
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
	public void connect(AddressI address, String communicationInboundPortURI) throws Exception {
		
		logMessage("Dans connect " + this.addr + ", " + address);
		
		//voisin.add(new ConnectionInfo(address, communicationInboundPortURI, false, "", null));
		logMessage("Ajout de voisin" + (routingNodes.size() + terminalNodes.size()));
		this.addTerminalNeighbour(address, communicationInboundPortURI);
		logMessage("fin connect");
		
	}

	/**
	 * Permet de connecter un noeud de routage
	 * @param address l'adresse du noeud
	 * @param communicationInboundPortURI l'adresse du port de communication de l'autre noeud
	 * @param routingInboundPortURI l'adresse de routage de l'autre noeud
	 * @throws Exception s'il y a un probleme
	 */
	public void connectRouting(AddressI address, String communicationInboundPortURI, String routingInboundPortURI)
			throws Exception {
		logMessage("connectR " + addr);
		assert this.addr != address;
		
		//voisin.add(new ConnectionInfo(address, communicationInboundPortURI, true, routingInboundPortURI, null));
		logMessage("Ajout de voisin" + (routingNodes.size() + terminalNodes.size()));
		
		this.addRoutingNeighbour(address, communicationInboundPortURI, routingInboundPortURI);
	}
	
	/**
	 * Permet de se connecter avec le port entr� du composant
	 * @param communicationInboundPortURI 
	 * @return le port de sortie du composant
	 * @throws Exception s'il y a un probleme
	 */
	protected CommunicationOutboundPort addTerminalNeighbour(AddressI address, String communicationInboundPortURI) throws Exception {
		logMessage("connection " + addr);
		assert this.addr != address;
		//Connexion a l'uriInbound
		CommunicationOutboundPort nodeOutbound = new CommunicationOutboundPort(this);
		nodeOutbound.publishPort();
		doPortConnection(nodeOutbound.getPortURI(), communicationInboundPortURI, NodeConnector.class.getCanonicalName());
		synchronized(this) {
			terminalNodes.add(new InfoTerminalN(address, nodeOutbound));
		}
		
		return nodeOutbound;
	}
	
	/**
	 * Method used to connect a Node to another that can rout message
	 * @param addr Address of the node we want to connect
	 * @param nodeInboundPortURI port of communication
	 * @param routingInboundPortURI routing port
	 * @return the newly created CommunicationouTboundPort
	 * @throws Exception If there is a problem
	 */
	protected CommunicationOutboundPort addRoutingNeighbour(AddressI addr, String nodeInboundPortURI, String routingInboundPortURI) throws Exception {
		logMessage("connectionRouting " + addr);
		CommunicationOutboundPort nodeOutbound = new CommunicationOutboundPort(this);
		nodeOutbound.publishPort();
		doPortConnection(nodeOutbound.getPortURI(), nodeInboundPortURI, NodeConnector.class.getCanonicalName());
		
//		//Connexion au RoutingNodeOutboundPort
//		RoutingOutboundPort routOutbound = new RoutingOutboundPort(this);
//		routOutbound.publishPort();
//		doPortConnection(routOutbound.getPortURI(), routingInboundPortURI, RoutingConnector.class.getCanonicalName());
//		
//		logMessage("isCreate=" + (routOutbound != null) + ", isPublished=" + routOutbound.isPublished());
		synchronized (this) {
			routingNodes.add(new InfoRoutNode(addr, nodeOutbound, null));
		}
		
		return nodeOutbound;
	}

	
	/**
	 * Permet de recevoir un message. S'il nous est attribu� ou qu'il n'a plus de saut, alors on arrete de le transmettre
	 * sinon on le transmet � nos voisins
	 * @param msg le message que l'on veut transmettre
	 * @throws Exception s'il y a un probleme
	 */
	public void transmitMessage(MessageI msg) throws Exception{
		//Copie du message 
		MessageI m = new Message((Message) msg);;
		//arriver a destination
		logMessage("Dans transmit");
		if(m.getAddress().equals(addr)) {
			logMessage("Message recue : " + m.getContent());
			return;
		}
		//Destruction 
		if(!m.stillAlive()) {
			logMessage("Mort du Message");
			return;
		}
		
		//Retransmission
		m.decrementsGops();
		
		// cherche une route pamie ses voisins
		int rout = -1, tmp; 
		CommunicationOutboundPort next = null;
		for (InfoRoutNode n : routingNodes) {
			tmp = n.getNode().hasRouteFor(m.getAddress());
			if (rout == -1 || (tmp < rout && tmp != -1)) {
				rout = tmp;
				next = n.getNode();
			}
		}
		// si une route existe
		if(rout > -1) {
			next.transmitMessage(m);
			return;
		// sinon
		} else {
			
			inondation(m);
		}
	}

	/**
	 * Transmet par Inondation le message a tous ses voisins
	 * @param m Message a retransmettre
	 * @throws Exception
	 */
	protected synchronized void inondation(MessageI m) throws Exception {
		//retransmet le message a tous ses voisins routeurs
		for (InfoRoutNode n : routingNodes) {
			logMessage("Je transfere a " + n.getAdress());
			n.getNode().transmitMessage(m);
		}
	}
	
	
	/**
	 * Verifie qu'il existe un chemin entre nous et l'adresse
	 * @param address l'addresse de destination
	 * @return le nombre de saut ou -1 si aucun résultat
	 * @throws Exception s'il y a un probleme
	 */
	public int hasRouteFor(AddressI address) throws Exception{
		if(address.equals(this.addr)) {
			return 0;
		}
		return -1;
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
	public AddressI getAddr() {
		return addr;
	}

	@Override
	public String toString() {
		return "Node [index=" + index + "]";
	}
}

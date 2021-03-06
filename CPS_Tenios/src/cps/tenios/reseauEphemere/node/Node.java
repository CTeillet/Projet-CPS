package cps.tenios.reseauEphemere.node;

import java.rmi.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cps.tenios.reseauEphemere.ConnectionInfo;
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
public abstract class  Node extends AbstractComponent {

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
	protected Node(String uri, NodeAddress addr, int i, int j, double r) throws Exception {
		super(10, 15);
		REGISTRATION_URI = uri;
		COMM_INBOUNDPORT_URI = AbstractPort.generatePortURI();

		nodeInboundPort = new CommunicationInboundPort(COMM_INBOUNDPORT_URI,this);
		nodeInboundPort.publishPort();

		registrationOutboundPort = new RegistrationOutboundPort(REGISTRATION_URI, this);
		registrationOutboundPort.publishPort();

		terminalNodes = new ArrayList<InfoTerminalN>();
		routingNodes = new ArrayList<InfoRoutNode>();

		this.toggleLogging();
		this.toggleTracing();

		this.range=r;
		this.addr = addr;
		pos = new Position(i, j);
	}


	@Override
	public abstract void execute() throws Exception;

	@Override
	public synchronized void finalise() throws Exception {

		System.out.println("\n"+this.toString()+"\n");

		//Deconnexion des ports
		this.doPortDisconnection(REGISTRATION_URI);
		//System.out.println("apres registration connected" + isPortConnected(REGISTRATION_URI));

		for(InfoTerminalN node : terminalNodes) {
			this.doPortDisconnection(node.getNode().getClientPortURI());
			//System.out.println("apres : port connected=" + isPortConnected(node.getNode().getClientPortURI()));
		}
		for(InfoRoutNode node : routingNodes) {
			this.doPortDisconnection(node.getNode().getClientPortURI());
			//System.out.println("apres : port connected=" + isPortConnected(node.getNode().getClientPortURI()));

		}
		logMessage("fin disconnect");

		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			//Depublication des ports
			this.nodeInboundPort.unpublishPort();
			//ports sortant
			this.registrationOutboundPort.unpublishPort();
			for(InfoTerminalN node : terminalNodes) {
				node.getNode().unpublishPort();
			}
			for(InfoRoutNode node : routingNodes) {
				node.getNode().unpublishPort();
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
	public abstract void transmitMessage(MessageI msg) throws Exception;

	/**
	 * Transmet par Inondation le message a tous ses voisins
	 * @param m Message a retransmettre
	 * @throws Exception
	 */
	protected synchronized void inondation(MessageI m) throws Exception {
		//retransmet le message a tous ses voisins routeurs
		for (InfoRoutNode n : routingNodes) {
			logMessage("Je transfere a " + n.getAddress());
			n.getNode().transmitMessage(m);
		}
	}

	/**
	 * Cherche une route possible pour transfer un message.
	 * Si parmi les voisins une route existe, on lui transmet le message.
	 * Sinon, on transmet par inondation 
	 * @param m Message a transmettre
	 * @throws Exception
	 */
	protected void seekNtransmit(MessageI m) throws Exception {
		// cherche une route parmie ses voisins
		int rout = -1, tmp; 
		CommunicationOutboundPort next = null;

		synchronized (this) {
			for (InfoRoutNode n : routingNodes) {
				try {
					n.getNode().ping();
					tmp = n.getNode().hasRouteFor(m.getAddress());
					// selection de la route la plus courte
					if (-1 < tmp && tmp < rout) {
						rout = tmp;
						next = n.getNode();
					}

				} catch (ConnectException e) {
					System.err.println("ping voisin mort :" + e.getMessage());
					supprimeVoisin(n.getNode());
				}
			}
		}
		// transmet au voisin suivant si une route existe ou a tous sinon
		if(rout > -1) {
			next.transmitMessage(m);
			return;

		} else {
			inondation(m);
		}

	}

	private void supprimeVoisin(CommunicationOutboundPort node) {
		for (InfoRoutNode in : routingNodes) {
			try {
				if (node.getClientPortURI() == in.getNode().getClientPortURI()) {
					routingNodes.remove(in); 
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}


	/**
	 * Verifie si un message doit être retransmis.
	 * On decremente son nombre de saut possible si c'est le cas.
	 * @param m Message a traiter
	 * @return false si mort ou a destination true sinon
	 */
	protected boolean checkMessage(MessageI m) {
		//arriver a destination
		if(m.getAddress().equals(addr)) {
			logMessage("Message recue : " + m.getContent());
			return false;
		}

		//Destruction 
		if(!m.stillAlive()) {
			logMessage("Mort du Message");
			return false;
		}

		// decrementation pour retransmission
		logMessage("Retransmission du Message");
		m.decrementsGops();
		return true;
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
	 * Permet de v�rifier que les voisins sont toujours actif
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
		StringBuilder str = new StringBuilder("Node :" + addr + "\nterminalNodes=");

		for (InfoTerminalN n : terminalNodes) {
			str.append( "\n\t" + n.getAddress() );
		}

		str.append("\nroutingNodes=");
		for (InfoRoutNode n : routingNodes) {
			str.append("\n\t" + n.getAddress());

		}
		return str.toString();
	}





}

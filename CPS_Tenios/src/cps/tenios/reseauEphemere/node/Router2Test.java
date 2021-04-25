package cps.tenios.reseauEphemere.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import cps.tenios.reseauEphemere.NodeAddress;
import cps.tenios.reseauEphemere.NodeConnector;
import cps.tenios.reseauEphemere.Position;
import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.PositionI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import cps.tenios.reseauEphemere.interfaces.RouteInfoI;
import cps.tenios.reseauEphemere.interfaces.RoutingCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@OfferedInterfaces(offered = {CommunicationCI.class, RoutingCI.class})
@RequiredInterfaces(required = {CommunicationCI.class, RegistrationCI.class, RoutingCI.class})
public abstract class Router2Test extends AbstractComponent {

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
	
	/**
	 * Table de routage
	 */
	protected Map<AddressI, Chemin> routingTable;
	
	/**
	 * URI du port de Routage entrant
	 */
	protected final String ROUTING_INBOUNDPORT_URI;
	/**
	 * Port de routage entrants
	 */
	protected RoutingInboundPort routInbound;
	
	protected int indexTransmit;
	protected int indexUpdate;
	
	
	
	
	protected Router2Test(String uri, int i, int j, double r) throws Exception {
		super(10, 0);
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
		
		routingTable = new HashMap<AddressI, Chemin>();
		ROUTING_INBOUNDPORT_URI = AbstractPort.generatePortURI();
		routInbound = new RoutingInboundPort(ROUTING_INBOUNDPORT_URI, this);
		routInbound.publishPort();
		
		
		indexTransmit = createNewExecutorService(AbstractPort.generatePortURI(), 7, false);
		indexUpdate = createNewExecutorService(AbstractPort.generatePortURI(), 3, false);
	}
	
	/**
	 * Method used to connect a Node to another that can rout message
	 * @param addr Address of the node we want to connect
	 * @param nodeInboundPortURI port of communication
	 * @param routingInboundPortURI routing port
	 * @return the newly created CommunicationouTboundPort
	 * @throws Exception If there is a problem
	 */
	protected abstract CommunicationOutboundPort addRoutingNeighbour(AddressI addr, String nodeInboundPortURI, String routingInboundPortURI) throws Exception;

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
		//section critique
			terminalNodes.add(new InfoTerminalN(address, nodeOutbound));
		//fin
		
		return nodeOutbound;
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
	 * Transmet par Inondation le message a tous ses voisins
	 * @param m Message a retransmettre
	 * @throws Exception
	 */
	protected void inondation(MessageI m) throws Exception {
		//retransmet le message a tous ses voisins routeurs
		for (InfoRoutNode n : routingNodes) {
			logMessage("Je transfere a " + n.getAdress());
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
		
		//section critique
			for (InfoRoutNode n : routingNodes) {
				tmp = n.getNode().hasRouteFor(m.getAddress());
				// selsction de la route la plus courte
				if (-1 < tmp && tmp < rout) {
					rout = tmp;
					next = n.getNode();
				}
			}
		//fin
		
		// transmet au voisin suivant si une route existe ou a tous sinon
		if(rout > -1) {
			next.transmitMessage(m);
			return;

		} else {
			inondation(m);
		}
	}
	
	/**
	 * Permet de recevoir un message. S'il nous est attribu� ou qu'il n'a plus de saut, alors on arrete de le transmettre
	 * sinon on le transmet � nos voisins
	 * @param msg le message que l'on veut transmettre
	 * @throws Exception s'il y a un probleme
	 */
	public abstract void transmitMessage(MessageI msg) throws Exception;
	
	/**
	 * Trouve depuis une adresse le port de Communication sortant correspondant
	 * @param adrr addresse du noeud recherche
	 * @return port de Communication sortant
	 */
	protected CommunicationOutboundPort findNodeOutboundPort(AddressI adrr) {
		for(InfoRoutNode node : routingNodes) {
			if(node.getAdress().equals(adrr)) {
				return node.getNode();
			}
		}
		for(InfoTerminalN node : terminalNodes) {
			if(node.getAddress().equals(adrr)) {
				return node.getNode();
			}
		}
		return null;
	}
	
	/**
	 * Trouve depuis une adresse le port de Routage sortant correspondant
	 * @param adrr addresse du noeud recherche
	 * @return port de Routage sortant
	 */
	protected RoutingOutboundPort findRoutingOutboundPort(AddressI adrr) {
		for(InfoRoutNode node : routingNodes) {
			if(node.getAdress().equals(adrr)) {
				return node.getRout();
			}
		}
		return null;
	}
	
	/**
	 * Permet d'avoir l'addresse d'un noeud
	 * @return l'adresse d'un noeud
	 */
	public AddressI getAddr() {
		return addr;
	}
	
	/**
	 * Permet de connaitre le nombre de noeud cr�e au total
	 * @return le nombre total de noeud
	 */
	public static int getCmp() {
		return cmp;
	}
	
	/**
	 * Permet de v�rifier que les voisins sont toujours actif
	 * @throws Exception s'il y a un probleme
	 */
	public abstract void ping() throws Exception;
	
	@Override
	public String toString() {
		return "Node [index=" + index + "]";
	}
	
	/**
	 * Permet de mettre a jour la route la plus optimale vers la destination et met a jour les noeuds voisins en cas de changement
	 * @param neighbour voisin ayant envoyé les information de stable de routage
	 * @param routes routes vers les adresses
	 * @throws Exception en cas de probleme
	 */
	public void updateRouting(AddressI neighbour, Set<RouteInfoI> routes) throws Exception {
		runTask(this.indexUpdate, new FComponentTask() {
			
			@Override
			public void run(ComponentI owner) {
				boolean hasChanged = false;

				for(RouteInfoI ri : routes) {

					if (ri.getDestination().isNodeAddress()) {

						//section critique
						Chemin tmp = routingTable.get(ri.getDestination());
						//Si pas de route vers address => creation d'un nouveau chemin
						if(tmp == null) {
							hasChanged = true;
							routingTable.put( (AddressI)ri.getDestination(), new Chemin(findNodeOutboundPort(neighbour), ri.getNumberOfHops()));

							// Si meilleur route => Maj
						} else if (tmp.getNumberOfHops() > ri.getNumberOfHops() + 1){
							hasChanged = true;
							// TODO remplacer par une list et ajouter
							tmp.setNext(findNodeOutboundPort(neighbour));
							tmp.setNumberOfHops(ri.getNumberOfHops() + 1);
						}
						//fin

					}
				}
				//propage l'update en cas de changement
				if(hasChanged) {
					try {
						propageUpdate(neighbour);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}				
			}
		});
		
	}
	
	/**
	 * Propage la mis a jour des tables 
	 * @param neighbour adresse du voisins ayant propager sa mis a jour
	 * @throws Exception
	 */
	protected void propageUpdate(AddressI neighbour) throws Exception{
		
		Set<RouteInfoI> r = this.getInfoTableRout();

		for(InfoRoutNode rn : routingNodes) {
			if(!rn.getAdress().equals(neighbour)) {
				rn.getRout().updateRouting(this.getAddr(), r);
			}
		}
	}
	
	/**
	 * retourne les informations de la table de routage
	 * @return les informations de la table de routage
	 */
	protected Set<RouteInfoI> getInfoTableRout() {
		Set<RouteInfoI> voisins = new HashSet<>();
		//section critique
			for(Entry<AddressI, Chemin> v : routingTable.entrySet()) {
				voisins.add(new RouteInfo(v.getKey(), v.getValue().getNumberOfHops()));
			}
		// fin
		return voisins;
	}
	
	/**
	 * Permet d'obtenir la liste des voisins
	 * @return la liste des voisins
	 */
	protected Set<RouteInfoI> getInfoVoisin() {
		//section critique
		Set<RouteInfoI> voisins = new HashSet<>();
		for(Entry<AddressI, Chemin> v : routingTable.entrySet()) {
			voisins.add(new RouteInfo(v.getKey(), v.getValue().getNumberOfHops()));
		}
		return voisins;
	}
	
	/**
	 * Permet de mettre a jour la route la plus courte vers un point d'acces
	 * @param neighbour voisins ayant envoyer les information vers le point d'acces
	 * @param numberOfHops nombre de saut requis pour y arriver
	 * @throws Exception en cas de probleme
	 */
	public abstract void updateAccessPoint(AddressI neighbour, int numberOfHops) throws Exception;
	
	
	// Methode de AbstractComponent
	
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
			//Depublication des ports entrants
			this.nodeInboundPort.unpublishPort();
			this.routInbound.unpublishPort();
			
			//ports sortant
			this.registrationOutboundPort.unpublishPort();
			
			for(InfoTerminalN node : terminalNodes) {
				node.getNode().unpublishPort();
			}
			for(InfoRoutNode node : routingNodes) {
				node.getNode().unpublishPort();
				node.getRout().unpublishPort();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

}

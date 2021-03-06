package cps.tenios.reseauEphemere.node;

import java.rmi.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
	 * URI du port entrants de communication
	 */
	protected final String COMM_INBOUNDPORT_URI;
	/**
	 * Port entrants pour les communication avec les Noeuds
	 */
	protected CommunicationInboundPort nodeInboundPort;

	/**
	 * URI de port sortants vers le gestionnaire r�seau
	 */
	private final String REGISTRATION_URI;
	/**
	 * Port sortant pour communiquer avec le gestionnaire Reseau
	 */
	protected RegistrationOutboundPort registrationOutboundPort;	

	/**
	 * URI du port de Routage entrant
	 */
	protected final String ROUTING_INBOUNDPORT_URI;
	/**
	 * Port de routage entrants
	 */
	protected RoutingInboundPort routInbound;

	protected boolean amIAlive = true;

	/**
	 * Compteur du nombre de noeud, utilis� pour les adresses
	 */
	private static int cmp = 0;
	/**
	 * Index du noeud
	 */
	protected int index = cmp;
	/**
	 * Adresse du noeud
	 */
	protected AddressI addr;

	/**
	 * Position du Noeud
	 */
	protected PositionI pos;
	/**
	 * Porter du signal du noeud
	 */
	protected double range;


	/**
	 * Index du pool de thread pour la communication
	 */
	protected int indexCommunication;
	/**
	 * Index du pool de thread pour la mise a jour des tables.
	 */
	protected int indexUpdate;

	/**
	 * Verrou qui protege des acces concurrent a la list terminalNodes
	 */
	protected Lock lockTerNodes = new ReentrantLock();
	/**
	 * Verrou qui protege des acces concurrent a la list routingNodes
	 */
	protected Lock lockRoutNodes = new ReentrantLock();
	/**
	 * Verrou qui protege des acces concurrent a la table de routage
	 */
	protected Lock lockTable = new ReentrantLock();

	protected Condition tableAndTerminal = lockTable.newCondition();
	protected Condition tableAndRoutes = lockTable.newCondition();

	/**
	 * List des noeud terminaux
	 */
	protected List<InfoTerminalN> terminalNodes;
	/**
	 * List des noeud pouvant router des messages
	 */
	protected List<InfoRoutNode> routingNodes;
	/**
	 * Table de routage
	 */
	protected Map<AddressI, TousChemins> routingTable;


	/**
	 * permet de creer un Composant noeud
	 * @param uri URI du port sortant vers le gestionnaire r�seau
	 * @param i abscisse
	 * @param j ordonnee
	 * @param r range
	 * @throws Exception s'il y a un probleme
	 */
	protected Router2Test(String uri, NodeAddress addr, int i, int j, double r) throws Exception {
		super(10, 0);
		
		this.toggleLogging();
		this.toggleTracing();
		
		//recuperation des uri et creation des port
		REGISTRATION_URI = uri;
		COMM_INBOUNDPORT_URI = AbstractPort.generatePortURI();

		nodeInboundPort = new CommunicationInboundPort(COMM_INBOUNDPORT_URI,this);
		nodeInboundPort.publishPort();
		
		ROUTING_INBOUNDPORT_URI = AbstractPort.generatePortURI();
		routInbound = new RoutingInboundPort(ROUTING_INBOUNDPORT_URI, this);
		routInbound.publishPort();

		registrationOutboundPort = new RegistrationOutboundPort(REGISTRATION_URI, this);
		registrationOutboundPort.publishPort();

		// Creation des structure de donnees
		terminalNodes = new ArrayList<InfoTerminalN>();
		routingNodes = new ArrayList<InfoRoutNode>();
		routingTable = new HashMap<AddressI, TousChemins>();
		
		
		this.range=r;
		this.addr = addr;
		pos = new Position(i, j);
		
		// creation des groupe du pool de thread
		indexCommunication = createNewExecutorService(AbstractPort.generatePortURI(), 7, false);
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
		logMessage("addTerminalNeighbour " + address);
		assert this.addr != address;
		//Connexion a l'uriInbound
		CommunicationOutboundPort nodeOutbound = new CommunicationOutboundPort(this);
		nodeOutbound.publishPort();
		doPortConnection(nodeOutbound.getPortURI(), communicationInboundPortURI, NodeConnector.class.getCanonicalName());

		//ajout du voisins a la list
		lockTerNodes.lock(); 
		terminalNodes.add(new InfoTerminalN(address, nodeOutbound));
		lockTerNodes.unlock();
		
		// mis a jour du nouveau voisins dans la table
		lockTable.lock();
		routingTable.put(addr, new TousChemins(nodeOutbound, 1));
		lockTable.unlock();
		
		logMessage("addTerminalNeighbour fin " + address);
		return nodeOutbound;
	}

	/**
	 * Se connecte avec le noeud d'adresse address, et le rajoute � ses voisins
	 * @param address l'adresse du port avec lequel on veut se connecter 
	 * @param communicationInboundPortURI l'uri du port avec lequel on veut se connecter
	 * @throws Exception s'il y a un probleme
	 */
	public void connect(AddressI address, String communicationInboundPortURI) throws Exception {

		logMessage("Dans connect " + address);

		this.addTerminalNeighbour(address, communicationInboundPortURI);
		logMessage("fin connect" + address);
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
		logMessage("connectRouting " + address);
		assert this.addr != address;

		this.addRoutingNeighbour(address, communicationInboundPortURI, routingInboundPortURI);
		logMessage("connectRouting fin " + address);
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
	 * Transmet par Inondation le message a tous ses voisins
	 * @param m Message a retransmettre
	 * @throws Exception
	 */
	protected void inondation(MessageI m) throws Exception {
		//retransmet le message a tous ses voisins routeurs
		lockRoutNodes.lock();
		//logMessage("lockRout pris");
		for (InfoRoutNode n : routingNodes) {
			logMessage("Je transfere a " + n.getAddress());
			n.getNode().transmitMessage(m);
		}
		//logMessage("lockRout relache");
		lockRoutNodes.unlock();
		//tableAndRoutes.notifyAll();
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

		lockRoutNodes.lock(); //section critique
		//logMessage("lockRout pris");
		for (InfoRoutNode n : routingNodes) {
			tmp = n.getNode().hasRouteFor(m.getAddress());
			// selection de la route la plus courte
			if (-1 < tmp && tmp < rout) {
				rout = tmp;
				next = n.getNode();
			}
		}
		//logMessage("lockRout relache");
		lockRoutNodes.unlock();
		//tableAndRoutes.notifyAll();
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
	 * @throws InterruptedException 
	 */
	protected CommunicationOutboundPort findNodeOutboundPort(AddressI adrr) throws Exception {

		// recherche dans les routing nodes
		lockRoutNodes.lock();
		for(InfoRoutNode node : routingNodes) {
			if(node.getAddress().equals(adrr)) {
				// node trouver, liberation du verrou et retour du resultat
				lockRoutNodes.unlock();
				return node.getNode();
			}
		}
		lockRoutNodes.unlock();

		// recherche dans les terminal nodes
		lockTerNodes.lock();
		for(InfoTerminalN node : terminalNodes) {
			if(node.getAddress().equals(adrr)) {
				// node trouver, liberation du verrou et retour du resultat
				lockTerNodes.unlock();
				return node.getNode();
			}
		}
		lockTerNodes.unlock();

		//Aucun resultat
		return null;
	}

	/**
	 * Trouve depuis une adresse le port de Routage sortant correspondant
	 * @param adrr addresse du noeud recherche
	 * @return port de Routage sortant
	 */
	protected RoutingOutboundPort findRoutingOutboundPort(AddressI adrr) {
		lockRoutNodes.lock();
		for(InfoRoutNode node : routingNodes) {
			if(node.getAddress().equals(adrr)) {
				lockRoutNodes.unlock();
				return node.getRout();
			}
		}
		lockRoutNodes.unlock();
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
	public void ping() throws Exception {
		if ((!amIAlive) || Math.random() < 0.01) {
			amIAlive = false;
			logMessage("mort du composants !");
			throw new ConnectException("ping"); 
		}
	}
	
	/**
	 * Supprime tout trace d'un voisin de la table de routage
	 * @param addrToDelete adress a supprimer
	 * @param portToDelete port a supprimer
	 */
	protected void suppprimeVoisinsTable(AddressI addrToDelete, CommunicationOutboundPort portToDelete) {
		lockTable.lock();
		// supprime le chemin vers ce node
		routingTable.remove(addrToDelete);
		// supprime toutes presence du noeud dans la tablde de routage
		for (Entry<AddressI, TousChemins> entry : routingTable.entrySet()) {
			entry.getValue().delete(portToDelete);
		}
		lockTable.unlock();
	}
	
	protected void testVoisins() throws Exception {

		lockRoutNodes.lock();
		for(InfoRoutNode n : routingNodes) {
			try {
				n.getNode().ping();
			} catch (ConnectException e) {
				logMessage(e.getMessage() + "suppression " + n.getAddress());
				routingNodes.remove(n);
				lockRoutNodes.unlock();
				//suppprimeVoisinsTable(n.getAddress(), n.getNode());
				testVoisins();
				return;
			}
		}
		lockRoutNodes.unlock();
		
		lockTerNodes.lock();
		for(InfoTerminalN n : terminalNodes) {
			try {
				n.getNode().ping();
			} catch (ConnectException e) {
				logMessage(e.getMessage() + "suppression " + n.getAddress());
				terminalNodes.remove(n);
				lockTerNodes.unlock();
				suppprimeVoisinsTable(n.getAddress(), n.getNode());
				//testVoisins();
				return;
			}
		}
		lockTerNodes.unlock();
	}
	
	/**
	 * supprime un voisins
	 * @param toDelete port a supprimer
	 */
//	protected void suppprimeVoisins(InfoTerminalN v) {
//		// recherche de quel type de noeud il s'agit
//		
//		lockTerNodes.lock();
//		for (InfoTerminalN v: terminalNodes)
//		{
//			if (v.getNode() == toDelete) {
//				terminalNodes.remove(v);
//				lockTerNodes.unlock();
//				suppprimeVoisinsTable(v.getAddress(), v.getNode());
//				return;
//			}
//		}
//		lockTerNodes.unlock();
//		
//		lockRoutNodes.lock();
//		for (InfoRoutNode v: routingNodes)
//		{
//			if (v.getNode() == toDelete) {
//				routingNodes.remove(v);
//				lockRoutNodes.unlock();
//				suppprimeVoisinsTable(v.getAddress(), v.getNode());
//				return;
//			}
//		}
//		lockRoutNodes.unlock();
//	}
	
	protected boolean isMemberOfRountingNodes (AddressI addr) {
		lockRoutNodes.lock();
		try {
			for (InfoRoutNode c : routingNodes) {
				if (c.getAddress() == addr) {
					return true;
				}
			}
			return false;
		} finally {
		lockRoutNodes.unlock();
		}
	}

	/**
	 * retourne les informations de la table de routage
	 * @return les informations de la table de routage
	 */
	protected Set<RouteInfoI> getInfoTableRout() {
		Set<RouteInfoI> voisins = new HashSet<>();
		lockTable.lock(); //section critique
		//logMessage("lockTable pris");
		for(Entry<AddressI, TousChemins> v : routingTable.entrySet()) {
			voisins.add(new RouteInfo(v.getKey(), v.getValue().getFirstChemin().getNumberOfHops()));
		}
		//logMessage("lockTable relache");
		lockTable.unlock();
		return voisins;
	}

	/**
	 * Permet de mettre a jour la route la plus courte vers un point d'acces
	 * @param neighbour voisins ayant envoyer les information vers le point d'acces
	 * @param numberOfHops nombre de saut requis pour y arriver
	 * @throws Exception en cas de probleme
	 */
	public abstract void updateAccessPoint(AddressI neighbour, int numberOfHops) throws Exception;

	/**
	 * Permet de mettre a jour la route la plus optimale vers la destination et met a jour les noeuds voisins en cas de changement
	 * @param neighbour voisin ayant envoyé les information de stable de routage
	 * @param routes routes vers les adresses
	 * @throws Exception en cas de probleme
	 */
	public void updateRouting(AddressI neighbour, Set<RouteInfoI> routes) throws Exception {
		logMessage("updateRouting");
		
		if (!isMemberOfRountingNodes(neighbour)) {
			logMessage("le voisins ne fait pas partie de la table");
			return;
		}
		runTask(this.indexUpdate, new FComponentTask() {

			@Override
			public void run(ComponentI owner) {
				logMessage("run updateRouting!! ");
				boolean hasChanged = false;
				

				try {
					
					try {
						testVoisins();
					} catch (ConnectException e1) {
						System.out.println(e1.getMessage());
					}
					
					CommunicationOutboundPort neighbourPort = findNodeOutboundPort(neighbour);
					
					for(RouteInfoI ri : routes) {
						if (ri.getDestination().isNodeAddress()) {

							lockTable.lock();//section critique
							//logMessage("lockTable lock");
							TousChemins tmp = routingTable.get(ri.getDestination());
							//Si pas de route vers address => creation d'un nouveau chemin
							if(tmp == null) {
								hasChanged = true;
								routingTable.put( (AddressI)ri.getDestination(), new TousChemins(neighbourPort, ri.getNumberOfHops()));

								// Si meilleur route => Maj
							} else if (tmp.add(neighbourPort, ri.getNumberOfHops())){
								hasChanged = true;
							}
							//logMessage("lockTable unlock");
							lockTable.unlock();
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
				} catch (Exception e) {
					e.printStackTrace();
				}
				logMessage("fin update");
			}
		});

	}

	/**
	 * Propage la mis a jour des tables 
	 * @param neighbour adresse du voisins ayant propager sa mis a jour
	 * @throws Exception
	 */
	protected void propageUpdate(AddressI neighbour) throws Exception{
		logMessage("debut propage");
		Set<RouteInfoI> r = this.getInfoTableRout();

		lockRoutNodes.lock();
		//logMessage("lockRout pris");
		for(InfoRoutNode rn : routingNodes) {
			if(!rn.getAddress().equals(neighbour)) {
				rn.getRout().updateRouting(this.getAddr(), r);
			}
		}
		//logMessage("lockRout relache");
		lockRoutNodes.unlock();
		//tableAndRoutes.notifyAll();
		logMessage("fin propage");
	}


	// Methode de AbstractComponent

	@Override
	public abstract void execute() throws Exception;

	@Override
	public synchronized void finalise() throws Exception {
		
		//affichage des voisins
		System.out.println("\n"+this.toString()+"\n");
		//Deconnexion des ports
		this.doPortDisconnection(REGISTRATION_URI);
		//System.out.println("apres registration connected" + isPortConnected(REGISTRATION_URI));

		for(InfoTerminalN node : terminalNodes) {
			this.doPortDisconnection(node.getNode().getClientPortURI());
		}
		for(InfoRoutNode node : routingNodes) {
			this.doPortDisconnection(node.getNode().getClientPortURI());
			this.doPortDisconnection(node.getRout().getClientPortURI());
			//System.out.println("apres : node port connected = " + isPortConnected(node.getNode().getClientPortURI()));
			//System.out.println("apres : rout port connected = " + isPortConnected(node.getRout().getClientPortURI()));
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
			super.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ComponentShutdownException(e);
		}

	}
	
	@Override
	public String toString() {
		String str = "Node :" + addr + "\nterminalNodes=";
		
		for (InfoTerminalN n : terminalNodes) {
			str += "\n\t" + n.getAddress();
			
		}
		
		str += "\nroutingNodes=";
		for (InfoRoutNode n : routingNodes) {
			str += "\n\t" + n.getAddress();
			
		}
		return str;
	}

}

package cps.tenios.reseauEphemere.node;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.NodeConnector;
import cps.tenios.reseauEphemere.RoutingConnector;
import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import cps.tenios.reseauEphemere.interfaces.RouteInfoI;
import cps.tenios.reseauEphemere.interfaces.RoutingCI;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

/**
 * Classe representant un noeud de routage
 * @author Tenios
 *
 */
@OfferedInterfaces(offered = {CommunicationCI.class, RoutingCI.class})
@RequiredInterfaces(required = {CommunicationCI.class, RegistrationCI.class, RoutingCI.class})
public class RoutingNode extends Router {

	/**
	 * Chemin vers le reseau classique
	 */
	protected Chemin path2Network;
	
	
	/**
	 * Constructeur preant URI du port sortant vers le gestionnaire r�seau
	 * @param uri du port sortant vers le gestionnaire r�seau
	 * @throws Exception s'il y a un probleme
	 */
	protected RoutingNode(String uri, int i, int j, double r) throws Exception {
		super(uri, i, j, r);
		path2Network = null;
	}
	
	@Override
	public void execute() throws Exception {
		logMessage("Debut Execute RoutingNode " + this.index);
		// enrigistrement au près du gestionnaire
		Set<ConnectionInfo> voisin = this.registrationOutboundPort.registerRoutingNode(this.addr, this.COMM_INBOUNDPORT_URI, this.pos, this.range, ROUTING_INBOUNDPORT_URI);
		//logMessage("Taille voisin " + voisin.size());
		//Connexion aux voisins
		for(ConnectionInfo c : voisin) {
			CommunicationOutboundPort out; // port de communication sortant 
			
			if (c.isRouting()) {
				// ajout d'un voisin routeur
				out = this.addRoutingNeighbour(c.getAddress(), c.getCommunicationInboundURI(), c.getRoutingInboundPortURI());
			} else {
				// ajout d'un voisin terminal
				out = this.addTerminalNeighbour(c.getAddress(), c.getCommunicationInboundURI());
			}
			// connexion au voisin pour qu'il ajoute le noeud courant
			out.connectRouting(this.addr, this.COMM_INBOUNDPORT_URI, this.ROUTING_INBOUNDPORT_URI);
			// ajout du noeud a la table de routage
			routingTable.put(c.getAddress(), new Chemin(out, 1));
		}
		logMessage("Fin");
	}

	@Override
	public void ping() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public int hasRouteFor(AddressI address) throws Exception {
		
		if(address.isNetworkAddress()) {
			if (path2Network != null) {
				return path2Network.getNumberOfHops();
			}
		} else {
			Chemin chemin = routingTable.get(address);
			if(chemin != null) {
				return chemin.getNumberOfHops();
			}
		}
		
		return -1;
	}
	
	@Override
	public void transmitMessage(MessageI msg) throws Exception {
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
		// Retransmission
		m.decrementsGops();
		// message a destion du reseau
		if (m.getAddress().isNetworkAddress()) {
			//si chemin connue
			if(path2Network != null) {
				this.path2Network.getNext().transmitMessage(m);
				return;
			}
			// sinon inondation
			this.inondation(m);
			return ;
		}
		// Cherche l'adresse dans la table 
		Chemin path = routingTable.get(m.getAddress());
		if(path != null) {
			logMessage("Gagner");
			path.getNext().transmitMessage(m);
			return ;
		}
		// par defaut innondation du reseau
		this.inondation(m);
	}
	
	/**
	 * Permet de mettre a jour la route la plus optimale vers la destination et met a jour les noeuds voisins en cas de changement
	 * @param neighbour voisin ayant envoyé les information de stable de routage
	 * @param routes routes vers les adresses
	 * @throws Exception en cas de probleme
	 */
	public void updateRouting(AddressI neighbour, Set<RouteInfoI> routes) throws Exception {
		boolean hasChanged = false; // un changement a eu lieu
		
		for(RouteInfoI ri : routes) {
			
			if (ri.getDestination().isNodeAddress()) {
				Chemin tmp = routingTable.get(ri.getDestination());
				//Si pas de route vers address => creation d'un nouveau chemin
				if(tmp == null) {
					hasChanged = true;
					routingTable.put( (AddressI)ri.getDestination(), new Chemin(this.findNodeOutboundPort(neighbour), ri.getNumberOfHops()));
				
				// Si meilleur route => Maj
				} else if (tmp.getNumberOfHops() > ri.getNumberOfHops() + 1){
					hasChanged = true;
					// TODO remplacer par une list et ajouter
					tmp.setNext(this.findNodeOutboundPort(neighbour));
					tmp.setNumberOfHops(ri.getNumberOfHops() + 1);
				}
			}
		}
		if(hasChanged) {
			propageUpdate(neighbour);
		}
	}
	
	/**
	 * Propage la mis a jour des tables 
	 * @param neighbour adresse du voisins ayant propager sa mis a jour
	 * @throws Exception
	 */
	private void propageUpdate(AddressI neighbour) throws Exception{
		
		Set<RouteInfoI> r = this.getInfoTableRout();

		for(InfoRoutNode rn : routingNodes) {
			if(!rn.getAdress().equals(neighbour)) {
				rn.getRout().updateRouting(this.getAddr(), r);
			}
		}
	}

	/**
	 * Permet de mettre a jour la route la plus courte vers un point d'acces
	 * @param neighbour voisins ayant envoyer les information vers le point d'acces
	 * @param numberOfHops nombre de saut requis pour y arriver
	 * @throws Exception en cas de probleme
	 */
	public void updateAccessPoint(AddressI neighbour, int numberOfHops) throws Exception {
		if(path2Network == null || path2Network.getNumberOfHops() > numberOfHops + 1) {
			path2Network = new Chemin(this.findNodeOutboundPort(neighbour), numberOfHops + 1);
			// propagation de l'update
			for(InfoRoutNode v : routingNodes) {
				if (!neighbour.equals(v.getAdress()))
				v.getRout().updateAccessPoint(this.getAddr(), path2Network.getNumberOfHops());
			}
		} 
	}
	
	@Override
	public String toString() {
		return "RoutingNode ["+ super.toString() +"]";
	}
	
	@Override
	protected CommunicationOutboundPort addRoutingNeighbour(AddressI addr, String nodeInboundPortURI, String routingInboundPortURI) throws Exception {
		logMessage("connectionRouting " + addr);
		// Connexion au node par un port de Communication
		CommunicationOutboundPort nodeOutbound = new CommunicationOutboundPort(this);
		nodeOutbound.publishPort();
		doPortConnection(nodeOutbound.getPortURI(), nodeInboundPortURI, NodeConnector.class.getCanonicalName());

		//Connexion au RoutingNodeOutboundPort
		RoutingOutboundPort routOutbound = new RoutingOutboundPort(this);
		routOutbound.publishPort();
		doPortConnection(routOutbound.getPortURI(), routingInboundPortURI, RoutingConnector.class.getCanonicalName());
		//logMessage("isCreate=" + (routOutbound != null) + ", isPublished=" + routOutbound.isPublished());
		
		// ajout du noeud dans list des voisins
		routingNodes.add(new InfoRoutNode(addr, nodeOutbound, routOutbound));
		// mis a jour du nouvau voisins
		routingTable.put(addr, new Chemin(nodeOutbound, 1));
		routOutbound.updateRouting(this.addr, this.getInfoTableRout());
		if (path2Network != null) {
			routOutbound.updateAccessPoint(this.addr, path2Network.getNumberOfHops());
		}
		
		
		return nodeOutbound;
	}
	
	/**
	 * retourne les informations de la table de routage
	 * @return les informations de la table de routage
	 */
	private Set<RouteInfoI> getInfoTableRout() {
		Set<RouteInfoI> voisins = new HashSet<>();
		for(Entry<AddressI, Chemin> v : routingTable.entrySet()) {
			voisins.add(new RouteInfo(v.getKey(), v.getValue().getNumberOfHops()));
		}
		return voisins;
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.routInbound.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
	

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
}

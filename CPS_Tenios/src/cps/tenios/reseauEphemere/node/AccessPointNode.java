package cps.tenios.reseauEphemere.node;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.NodeConnector;
import cps.tenios.reseauEphemere.RoutingConnector;
import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import cps.tenios.reseauEphemere.interfaces.RouteInfoI;
import cps.tenios.reseauEphemere.interfaces.RoutingCI;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
/**
 * Classe reprï¿½sentant un point d'accï¿½s
 * @author Tenios
 *
 */
@OfferedInterfaces(offered = {CommunicationCI.class, RoutingCI.class})
@RequiredInterfaces(required = {CommunicationCI.class, RegistrationCI.class, RoutingCI.class})
public class AccessPointNode extends Node {
	
	/**
	 * Table de routage contenant les différents destinataires
	 */
	protected HashMap<AddressI, Chemin> routingTable;
	/**
	 * URI du routing inbound port
	 */
	protected final String ROUTING_INBOUNDPORT_URI;
	
	
	/**
	 * Permet de creer un composant point d'accï¿½s en spï¿½cifiant l'URI du port de registration sortant
	 * @param uri du port de registration sortant
	 * @throws Exception s'il y a un probleme
	 */
	protected AccessPointNode(String uri, int i, int j, double r) throws Exception {
		super(uri, i, j, r);
		this.ROUTING_INBOUNDPORT_URI = AbstractPort.generatePortURI();
		routingTable = new HashMap<AddressI, Chemin>();
	}

	@Override
	public void execute() throws Exception {
		logMessage("Dans Access Point " + this.index);
		Set<ConnectionInfo> voisin = this.registrationOutboundPort.registerAccessPoint(this.addr, this.COMM_INBOUNDPORT_URI, this.pos, 100.0, "");
		
		for(ConnectionInfo c : voisin) {
			CommunicationOutboundPort out;
			
			// TODO faire ajout dans connection & connectionRouting 
			
			if (c.isRouting()) {
				// ajout d'un voisin routeur
				out = this.addRoutingNeighbour(c.getAddress(), c.getCommunicationInboundURI(), c.getRoutingInboundPortURI());
				// TODO updateRouting + update AcessPoint
			} else {
				// ajout d'un voisin terminal
				out = this.addTerminalNeighbour(c.getAddress(), c.getCommunicationInboundURI());
				this.terminalNodes.add(new InfoTerminalN(c.getAddress(), out));
			}
			routingTable.put(c.getAddress(), new Chemin(out, 1));
			out.connectRouting(this.addr, this.COMM_INBOUNDPORT_URI, ROUTING_INBOUNDPORT_URI);
		}
		
		
		
		logMessage("Fin");
		//this.registrationOutboundPort.unregister(this.addr);
	}

	@Override
	public void transmitMessage(MessageI msg) throws Exception {
		//Copie du message 
		logMessage("Dans transmit");
		MessageI m = new Message((Message) msg);;
		//arriver a destination
		if(m.getAddress().equals(addr)) {
			logMessage("Message recue : " + m.getContent());
			return;
		}
		// Message pour le reseau
		if(m.getAddress().isNetworkAddress()) {
			logMessage("Le message a atteint le reseau :" + m.getContent());
			return ;
		}
		//Destruction 
		if(!m.stillAlive()) {
			logMessage("Mort du Message");
			return;
		}
		m.decrementsGops();
		// Cherche l'adresse dans la table 
		Chemin path = routingTable.get(m.getAddress());
		if(path != null) {
			logMessage("Gagner");
			path.getNext().transmitMessage(m);
			return ;
		}
		//innondation du reseau
		this.inondation(m);
	}

	@Override
	public int hasRouteFor(AddressI address) throws Exception {
		if(address.isNetworkAddress()) {
			return 0;
		}
		Chemin path = routingTable.get(address);
		if(path!=null) {
			return path.getNumberOfHops();
		}
		return -1;
	}

	@Override
	public void ping() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return "AccessPointNode [" + super.toString() + "]";
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
		routOutbound.updateRouting(this.addr, this.getInfoVoisin());
		routOutbound.updateAccessPoint(this.addr, 0);
		
		return nodeOutbound;
	}
	
	/**
	 * Permet d'obtenir la liste des voisins
	 * @return la liste des voisins
	 */
	private Set<RouteInfoI> getInfoVoisin() {
		Set<RouteInfoI> voisins = new HashSet<>();
		for(Entry<AddressI, Chemin> v : routingTable.entrySet()) {
			voisins.add(new RouteInfo(v.getKey(), v.getValue().getNumberOfHops()));
		}
		return voisins;
	}
	
	/**
	 * Permet d'obtenir un outboundPort qui correspond à l'addresse donnée 
	 * @param adrr adresse donnée ou l'on cherche le port
	 * @return le poert correspondant à l'adresse donnée
	 */
	protected CommunicationOutboundPort getNodeOutboundPort(AddressI adrr) {
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
	 * Permet de mettre a jour la route la plus optimale vers la destination et met a jour les noeuds voisins en cas de changement
	 * @param neighbour voisin ayant envoyÃ© les information de stable de routage
	 * @param routes routes vers les adresses
	 */
	public void updateRouting(AddressI neighbour, Set<RouteInfoI> routes) throws Exception {
		boolean hasChanged = false;
		for(RouteInfoI ri : routes) {
			if (ri.getDestination().isNodeAddress()) {
				Chemin tmp = routingTable.get(ri.getDestination());
				//Si pas de route vers address => creation d'un nouveau chemin
				if(tmp == null) {
					hasChanged = true;
					routingTable.put( (AddressI)ri.getDestination(), new Chemin(this.getNodeOutboundPort(neighbour), ri.getNumberOfHops()));
				// Si meilleur route => Maj
				} else if (tmp.getNumberOfHops() > ri.getNumberOfHops() + 1){
					hasChanged = true;
					tmp.setNext(this.getNodeOutboundPort(neighbour));
					tmp.setNumberOfHops(ri.getNumberOfHops() + 1);
				}
			}
		}
		if(hasChanged) {
			propageUpdate(neighbour);
		}
	}
	
	/**
	 * Permet de mettre à jour l'accès au réseau exterieur
	 * @param neighbour Adresse du voisin connecté au reseau exterieur
	 * @param numberOfHops nombre de saut pour y acceder
	 * @throws Exception s'il y a un probleme
	 */
	public void updateAccessPoint(AddressI neighbour, int numberOfHops) throws Exception {}
	
	/**
	 * Permet de propager la mise à jour des tables de routages
	 * @param neighbour le voisin vers lequel propager
	 * @throws Exception s'il y a un probleme
	 */
	private void propageUpdate(AddressI neighbour) throws Exception{
		Set<RouteInfoI> r = new HashSet<RouteInfoI>();
		
		for(Entry<AddressI, Chemin>  t :routingTable.entrySet()) {
			r.add(new RouteInfo(t.getKey(), t.getValue().getNumberOfHops()));
		}
		for(InfoRoutNode rn : routingNodes) {
			if(!rn.getAdress().equals(neighbour)) {
				rn.getRout().updateRouting(this.getAddr(), r);
			}
		}
	}
}

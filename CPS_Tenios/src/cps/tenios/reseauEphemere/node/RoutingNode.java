package cps.tenios.reseauEphemere.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.NetworkAddress;
import cps.tenios.reseauEphemere.NodeConnector;
import cps.tenios.reseauEphemere.RoutingConnector;
import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import cps.tenios.reseauEphemere.interfaces.RouteInfoI;
import cps.tenios.reseauEphemere.interfaces.RoutingCI;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

/**
 * Classe repr�sentant un noeud de routage
 * @author Tenios
 *
 */
@OfferedInterfaces(offered = {CommunicationCI.class, RoutingCI.class})
@RequiredInterfaces(required = {CommunicationCI.class, RegistrationCI.class, RoutingCI.class})
public class RoutingNode extends Node {

	protected Chemin path2Network;
	protected Map<AddressI, Chemin> routingTable;
	protected final String ROUTING_INBOUNDPORT_URI;
	protected RoutingInboundPort routInbound;
	/**
	 * Constructeur preant URI du port sortant vers le gestionnaire r�seau
	 * @param uri du port sortant vers le gestionnaire r�seau
	 * @throws Exception s'il y a un probleme
	 */
	protected RoutingNode(String uri, int i, int j, double r) throws Exception {
		super(uri, i, j, r);
		routingTable = new HashMap<AddressI, Chemin>();
		path2Network = null;
		ROUTING_INBOUNDPORT_URI = AbstractPort.generatePortURI();
		routInbound = new RoutingInboundPort(ROUTING_INBOUNDPORT_URI, this);
		routInbound.publishPort();
	}
	
	@Override
	public void execute() throws Exception {
		logMessage("Debut Execute RoutingNode " + this.index);

		Set<ConnectionInfo> voisin = this.registrationOutboundPort.registerRoutingNode(this.addr, this.COMM_INBOUNDPORT_URI, this.pos, this.range, ROUTING_INBOUNDPORT_URI);
		
		logMessage("Taille voisin " + voisin.size());
		for(ConnectionInfo c : voisin) {
			CommunicationOutboundPort out;
			
			// TODO faire ajout dans connection & connectionRouting 
			
			if (c.isRouting()) {
				// ajout d'un voisin routeur
				out = this.addRoutingNeighbor(c.getAddress(), c.getCommunicationInboundURI(), c.getRoutingInboundPortURI());
				
				// TODO updateRouting + update AcessPoint
			} else {
				// ajout d'un voisin terminal
				out = this.addTerminalNeighbor(c.getAddress(), c.getCommunicationInboundURI());
			}
			routingTable.put(c.getAddress(), new Chemin(out, 1));
			out.connectRouting(this.addr, this.COMM_INBOUNDPORT_URI, this.ROUTING_INBOUNDPORT_URI);
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
		logMessage("Envoie vers " + m.getAddress());
		// envoie vers le reseau
		if (m.getAddress().isNetworkAddress()) {
			if(path2Network != null) {
				m.decrementsGops();
				this.path2Network.getNext().transmitMessage(m);
				return;
			}
			
			super.transmitMessage(msg);
			return ;
		}
		// Cherche l'adresse dans la table 
		Chemin path = routingTable.get(m.getAddress());
		if(path != null) {
			logMessage("Gagner");
			m.decrementsGops();
			path.getNext().transmitMessage(m);
			return ;
		}

		//aucune route => inondation
		super.transmitMessage(msg);
	}
	
	
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

	public void updateAccessPoint(AddressI neighbour, int numberOfHops) throws Exception {
		if(path2Network == null || path2Network.getNumberOfHops() > numberOfHops + 1) {
			path2Network = new Chemin(this.getNodeOutboundPort(neighbour), numberOfHops + 1);
			// TODO propagation
		} 
	}
	
	private void propageUpdateAP() throws Exception {
		for(InfoRoutNode v : routingNodes) {
			v.getRout().updateAccessPoint(this.getAddr(), path2Network.getNumberOfHops());
		}
	}
	
	@Override
	public String toString() {
		return "RoutingNode ["+ super.toString() +"]";
	}
	
	@Override
	protected CommunicationOutboundPort addRoutingNeighbor(AddressI addr, String nodeInboundPortURI, String routingInboundPortURI) throws Exception {
		logMessage("connectionRouting " + addr);
		CommunicationOutboundPort nodeOutbound = new CommunicationOutboundPort(this);
		nodeOutbound.publishPort();
		doPortConnection(nodeOutbound.getPortURI(), nodeInboundPortURI, NodeConnector.class.getCanonicalName());

		//Connexion au RoutingNodeOutboundPort
		RoutingOutboundPort routOutbound = new RoutingOutboundPort(this);
		routOutbound.publishPort();
		try {
			doPortConnection(routOutbound.getPortURI(), routingInboundPortURI, RoutingConnector.class.getCanonicalName());
		}catch (Exception e) {
			e.printStackTrace();
		}
		logMessage("isCreate=" + (routOutbound != null) + ", isPublished=" + routOutbound.isPublished());
		routingNodes.add(new InfoRoutNode(addr, nodeOutbound, routOutbound));
		return nodeOutbound;
	}
	
	private Set<RouteInfoI> getInfoVoisin() {
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
	
	protected RoutingOutboundPort getRoutingOutboundPort(AddressI adrr) {
		for(InfoRoutNode node : routingNodes) {
			if(node.getAdress().equals(adrr)) {
				return node.getRout();
			}
		}
		return null;
	}
}

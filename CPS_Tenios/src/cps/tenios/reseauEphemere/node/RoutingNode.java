package cps.tenios.reseauEphemere.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import cps.tenios.reseauEphemere.interfaces.RouteInfoI;
import cps.tenios.reseauEphemere.interfaces.RoutingCI;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;

/**
 * Classe repr�sentant un noeud de routage
 * @author Tenios
 *
 */
@OfferedInterfaces(offered = {CommunicationCI.class, RoutingCI.class})
@RequiredInterfaces(required = {CommunicationCI.class, RegistrationCI.class, RoutingCI.class})
public class RoutingNode extends Node {

	protected Chemin path2Network;
	protected Map<NodeAddressI, Chemin> routingTable;
	protected final String ROUTING_INBOUNDPORT_URI;
	/**
	 * Constructeur preant URI du port sortant vers le gestionnaire r�seau
	 * @param uri du port sortant vers le gestionnaire r�seau
	 * @throws Exception s'il y a un probleme
	 */
	protected RoutingNode(String uri, int i, int j, double r) throws Exception {
		super(uri, i, j, r);
		routingTable = new HashMap<NodeAddressI, Chemin>();
		path2Network = null;
		ROUTING_INBOUNDPORT_URI = AbstractPort.generatePortURI();
	}
	
	@Override
	public void execute() throws Exception {
		logMessage("Debut Execute RoutingNode " + this.index);

		Set<ConnectionInfo> voisin = this.registrationOutboundPort.registerRoutingNode(this.addr, this.COMM_INBOUNDPORT_URI, this.pos, this.range, ROUTING_INBOUNDPORT_URI);
		
		logMessage("Taille voisin " + voisin.size());
		for(ConnectionInfo c : voisin) {
			String uriInbound = c.getCommunicationInboundURI();
			NodeOutboundPort out;
			
			// TODO faire ajout dans connection & connectionRouting 
			
			if (c.isRouting()) {
				// ajout d'un voisin routeur
				Pair<RoutingNodeOutboundPort, NodeOutboundPort> rout = this.connectionRouting(c.getRoutingInboundPortURI(), c.getAddress(), c.getCommunicationInboundURI());
				out = rout.getNode();
				// TODO updateRouting + update AcessPoint
			} else {
				// ajout d'un voisin terminal
				out = this.connection(uriInbound, c.getAddress());
				this.terminalNodes.add(new Pair<NodeAddressI, NodeOutboundPort>(c.getAddress(), out));
			}
			routingTable.put(c.getAddress(), new Chemin(out, 1));
			out.connectRouting(this.addr, this.COMM_INBOUNDPORT_URI, ROUTING_INBOUNDPORT_URI);
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
				return -1;
			}
			return path2Network.getNumberOfHops();
		}
		return routingTable.get(address).getNumberOfHops();
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
	
	
	public void updateRouting(NodeAddressI neighbour, Set<RouteInfoI> routes) throws Exception {
		boolean hasChanged = false;
		for(RouteInfoI ri : routes) {
			if (ri.getDestination().isNodeAddress()) {
				Chemin tmp = routingTable.get(ri.getDestination());
				//Si pas de route vers address => creation d'un nouveau chemin
				if(tmp == null) {
					hasChanged = true;
					routingTable.put( (NodeAddressI)ri.getDestination(), new Chemin(this.getNodeOutboundPort(neighbour), ri.getNumberOfHops()));
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
	
	private void propageUpdate(NodeAddressI neighbour) throws Exception{
		Set<RouteInfoI> r = new HashSet<RouteInfoI>();
		
		for(Entry<NodeAddressI, Chemin>  t :routingTable.entrySet()) {
			r.add(new RouteInfo(t.getKey(), t.getValue().getNumberOfHops()));
		}
		for(Triplet<NodeAddressI, NodeOutboundPort, RoutingNodeOutboundPort>  rn : routingNodes) {
			if(!rn.getLabel().equals(neighbour)) {
				rn.getRout().updateRouting(this.getAddr(), r);
			}
		}
	}

	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception {
		if(path2Network == null || path2Network.getNumberOfHops() > numberOfHops + 1) {
			path2Network = new Chemin(this.getNodeOutboundPort(neighbour), numberOfHops + 1);
			// TODO propagation
		} 
	}
	
	@Override
	public String toString() {
		return "RoutingNode ["+ super.toString() +"]";
	}
}

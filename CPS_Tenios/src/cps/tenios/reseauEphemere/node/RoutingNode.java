package cps.tenios.reseauEphemere.node;

import java.util.HashMap;
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
@RequiredInterfaces(required = {CommunicationCI.class, RegistrationCI.class})
public class RoutingNode extends Node {

	
	protected HashMap<NodeAddressI, Chemin> routingTable;
	protected Chemin path2Network;
	protected final String ROUTING_INBOUNDPORT_URI;
	/**
	 * Constructeur preant URI du port sortant vers le gestionnaire r�seau
	 * @param uri du port sortant vers le gestionnaire r�seau
	 * @throws Exception s'il y a un probleme
	 */
	protected RoutingNode(String uri) throws Exception {
		super(uri);
		routingTable = new HashMap<NodeAddressI, Chemin>();
		path2Network = null;
		ROUTING_INBOUNDPORT_URI = AbstractPort.generatePortURI();
	}
	
	@Override
	public void execute() throws Exception {
		logMessage("Debut Execute RoutingNode " + this.index);

		Set<ConnectionInfo> voisin = this.registrationOutboundPort.registerRoutingNode(this.addr, this.COMM_INBOUNDPORT_URI, this.pos, 100.0, ROUTING_INBOUNDPORT_URI);
		
		logMessage("Taille voisin " + voisin.size());
		for(ConnectionInfo c : voisin) {
			String uriInbound = c.getCommunicationInboundURI();
			NodeOutboundPort out = this.connection(uriInbound);
			out.connectRouting(this.addr, this.COMM_INBOUNDPORT_URI, ROUTING_INBOUNDPORT_URI);
			// TODO fair ajout dans connection & connectionRouting 
			// TODO Maj tab de hash
			if (c.isRouting()) {
				// ajout d'un voisin routeur
				RoutingNodeOutboundPort rout = this.connectionRouting(c.getRoutingInboundPortURI());
				this.routingNodes.add(new Triplet<NodeAddressI, NodeOutboundPort, RoutingNodeOutboundPort>(c.getAddress(), out, rout));
				// TODO updateRouting + update AcessPoint
			} else {
				// ajout d'un voisin terminal
				this.terminalNodes.add(new Pair<NodeAddressI, NodeOutboundPort>(c.getAddress(), out));
			}
		}
		logMessage("Fin");
	}

	@Override
	public void ping() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean hasRouteFor(AddressI address) throws Exception {
		if(address.isNetworkAddress()) {
			return path2Network != null;
		}
		return routingTable.get(address) != null;
	}
	
	@Override
	public void transmitMessage(MessageI msg) throws Exception {
		//Copie du message 
				MessageI m = new Message((Message) msg);;
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
				
				if (this.hasRouteFor(m.getAddress())) {
					m.decrementsGops();
					//TODO recupere le vosin et envoyer m 
					Chemin path = routingTable.get(m.getAddress());
					path.getNext().transmitMessage(m);
					
				} else {
					//aucune route => inondation
					super.transmitMessage(msg);
				}
				
		
	}
	
	public void updateRouting(NodeAddressI neighbour, Set<RouteInfoI> routes) throws Exception {
		for(RouteInfoI ri : routes) {
			if (ri.getDestination().isNodeAddress()) {
				Chemin tmp = routingTable.get(ri.getDestination());
				//Si pas de route vers address => creation d'un nouveau chemin
				if(tmp == null) {
					routingTable.put( (NodeAddressI)ri.getDestination(), new Chemin(this.getNodeOutboundPort(neighbour), ri.getNumberOfHops()));
				// Si meilleur route => Maj
				} else if (tmp.getNumberOfHops() > ri.getNumberOfHops()){
					tmp.setNext(this.getNodeOutboundPort(neighbour));
					tmp.setNumberOfHops(ri.getNumberOfHops());
				}
			}
		}
	}
	
	public void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception {
		if(path2Network == null || path2Network.getNumberOfHops() > numberOfHops) {
			path2Network = new Chemin(this.getNodeOutboundPort(neighbour), numberOfHops);
		} 
	}
	
	@Override
	public String toString() {
		return "RoutingNode ["+ super.toString() +"]";
	}
}

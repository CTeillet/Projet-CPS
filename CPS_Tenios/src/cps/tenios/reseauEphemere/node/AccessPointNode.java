package cps.tenios.reseauEphemere.node;


import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.NodeConnector;
import cps.tenios.reseauEphemere.RoutingConnector;
import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import cps.tenios.reseauEphemere.interfaces.RoutingCI;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
/**
 * Classe repr�sentant un point d'acc�s
 * @author Tenios
 *
 */
@OfferedInterfaces(offered = {CommunicationCI.class, RoutingCI.class})
@RequiredInterfaces(required = {CommunicationCI.class, RegistrationCI.class, RoutingCI.class})
public class AccessPointNode extends Router2Test {
	
	/**
	 * Constructeur de AccessPointNode
	 * @param uri Uri du port sortant vers le gestionnaire reseau
	 * @param i Abscisse de la coordonee du AccessPointNode
	 * @param j Ordonnee de la coordonee du AccessPointNode
	 * @param r Portee du signal 
	 * @throws Exception En case de probleme
	 */
	protected AccessPointNode(String uri, int i, int j, double r) throws Exception {
		super(uri, i, j, r);
	}

	@Override
	public void execute() throws Exception {
		logMessage("Dans Access Point " + this.index);
		Set<ConnectionInfo> voisin = this.registrationOutboundPort.registerAccessPoint(this.addr, this.COMM_INBOUNDPORT_URI, this.pos, 100.0, "");
		
		for(ConnectionInfo c : voisin) {
			CommunicationOutboundPort out;
			
			
			if (c.isRouting()) {
				// ajout d'un voisin routeur
				out = this.addRoutingNeighbour(c.getAddress(), c.getCommunicationInboundURI(), c.getRoutingInboundPortURI());
				// TODO updateRouting + update AcessPoint
			} else {
				// ajout d'un voisin terminal
				out = this.addTerminalNeighbour(c.getAddress(), c.getCommunicationInboundURI());
				synchronized (this) {
					routingTable.put(c.getAddress(), new Chemin(out, 1));
				}
			}
			
			out.connectRouting(this.addr, this.COMM_INBOUNDPORT_URI, ROUTING_INBOUNDPORT_URI);
		}
		
		
		
		logMessage("Fin");
		//this.registrationOutboundPort.unregister(this.addr);
	}

	@Override
	public void transmitMessage(MessageI msg) throws Exception {
		//Copie du message 
		MessageI m = new Message((Message) msg);;

		// verifie si le message est arrivé a destination, a destination du reseau, mort ou a retransmettre
		if (this.checkMessage(m)) {
			// Cherche l'adresse dans la table 
			Chemin path = routingTable.get(m.getAddress());
			if(path != null) {
				logMessage("Gagner");
				path.getNext().transmitMessage(m);
				return ;
			}
			this.seekNtransmit(m);
		}
	}

	@Override
	protected boolean checkMessage(MessageI m) {
		// Message pour le reseau
		if(m.getAddress().isNetworkAddress()) {
			logMessage("Le message a atteint le reseau :" + m.getContent());
			return false;
		}
		return super.checkMessage(m);
	}

	@Override
	public int hasRouteFor(AddressI address) throws Exception {
		
		if(address.equals(this.addr)) {
			return 0;
		}
		
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
		logMessage("addRoutingNeighbour " + addr);
		// Connexion au node par un port de Communication
		CommunicationOutboundPort nodeOutbound = new CommunicationOutboundPort(this);
		nodeOutbound.publishPort();
		doPortConnection(nodeOutbound.getPortURI(), nodeInboundPortURI, NodeConnector.class.getCanonicalName());

		//Connexion au RoutingNodeOutboundPort
		RoutingOutboundPort routOutbound = new RoutingOutboundPort(this);
		routOutbound.publishPort();
		doPortConnection(routOutbound.getPortURI(), routingInboundPortURI, RoutingConnector.class.getCanonicalName());
		
		synchronized (routOutbound) {
			// ajout du noeud dans list des voisins
			routingNodes.add(new InfoRoutNode(addr, nodeOutbound, routOutbound));
			// mis a jour du nouvau voisins
			routingTable.put(addr, new Chemin(nodeOutbound, 1));
		}
		
		routOutbound.updateRouting(this.addr, this.getInfoVoisin());
		routOutbound.updateAccessPoint(this.addr, 0);
		
		return nodeOutbound;
	}
	
	
	
	@Override
	public void updateAccessPoint(AddressI neighbour, int numberOfHops) throws Exception {}
	
}

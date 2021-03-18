package cps.tenios.reseauEphemere.node;


import java.util.HashMap;
import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.NodeConnector;
import cps.tenios.reseauEphemere.RoutingConnector;
import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import cps.tenios.reseauEphemere.interfaces.RoutingCI;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
/**
 * Classe repr�sentant un point d'acc�s
 * @author Tenios
 *
 */
@OfferedInterfaces(offered = {CommunicationCI.class, RoutingCI.class})
@RequiredInterfaces(required = {CommunicationCI.class, RegistrationCI.class, RoutingCI.class})
public class AccessPointNode extends Node {
	
	// TODO java doc
	protected HashMap<NodeAddressI, Chemin> routingTable;
	protected final String ROUTING_INBOUNDPORT_URI;
	
	
	/**
	 * Permet de creer un composant point d'acc�s en sp�cifiant l'URI du port de registration sortant
	 * @param uri du port de registration sortant
	 * @throws Exception s'il y a un probleme
	 */
	protected AccessPointNode(String uri, int i, int j, double r) throws Exception {
		super(uri, i, j, r);
		this.ROUTING_INBOUNDPORT_URI = AbstractPort.generatePortURI();
		routingTable = new HashMap<NodeAddressI, Chemin>();
	}

	@Override
	public void execute() throws Exception {
		logMessage("Dans Access Point " + this.index);
		Set<ConnectionInfo> voisin = this.registrationOutboundPort.registerAccessPoint(this.addr, this.COMM_INBOUNDPORT_URI, this.pos, 100.0, "");
		
		for(ConnectionInfo c : voisin) {
			NodeOutboundPort out;
			
			// TODO faire ajout dans connection & connectionRouting 
			
			if (c.isRouting()) {
				// ajout d'un voisin routeur
				out = this.connectionRouting(c.getAddress(), c.getCommunicationInboundURI(), c.getRoutingInboundPortURI());
				// TODO updateRouting + update AcessPoint
			} else {
				// ajout d'un voisin terminal
				out = this.connection(c.getAddress(), c.getCommunicationInboundURI());
				this.terminalNodes.add(new Pair<NodeAddressI, NodeOutboundPort>(c.getAddress(), out));
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
		// Cherche l'adresse dans la table 
		Chemin path = routingTable.get(m.getAddress());
		if(path != null) {
			m.decrementsGops();
			path.getNext().transmitMessage(m);
			return;
		}

		//aucune route => inondation
		super.transmitMessage(msg);
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
	protected NodeOutboundPort connectionRouting(NodeAddressI addr, String nodeInboundPortURI, String routingInboundPortURI) throws Exception {
		logMessage("connectionRouting " + addr);
		NodeOutboundPort nodeOutbound = new NodeOutboundPort(this);
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
		routingNodes.add(new Triplet<>(addr, nodeOutbound, routOutbound));
		return nodeOutbound;
	}

}

package cps.tenios.reseauEphemere.node;

import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import cps.tenios.reseauEphemere.interfaces.RouteInfoI;
import cps.tenios.reseauEphemere.interfaces.RoutingCI;
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

	/**
	 * Constructeur preant URI du port sortant vers le gestionnaire r�seau
	 * @param uri du port sortant vers le gestionnaire r�seau
	 * @throws Exception s'il y a un probleme
	 */
	protected RoutingNode(String uri) throws Exception {
		super(uri);
	}

	@Override
	public void execute() throws Exception {
		logMessage("Debut Execute RoutingNode " + this.index);
		
		//TODO routingInboundPort
		voisin = this.registrationOutboundPort.registerRoutingNode(this.addr, this.INBOUNDPORT_URI, this.pos, 100.0, "");
		
		logMessage("Taille voisin " + voisin.size());
		for(ConnectionInfo c : voisin) {
			String uriInbound = c.getCommunicationInboundURI();
			this.connection(uriInbound).connectRouting(this.addr, this.INBOUNDPORT_URI, "");
		}
		showNeighbourg(voisin);
		logMessage("Fin");
		//this.registrationOutboundPort.unregister(this.addr);
	}

	@Override
	public void ping() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public String toString() {
		return "RoutingNode ["+ super.toString() +"]";
	}
	
	void updateRouting(NodeAddressI neighbour, Set<RouteInfoI> routes) throws Exception {
		// TODO
	}
	
	void updateAccessPoint(NodeAddressI neighbour, int numberOfHops) throws Exception {
		// TODO
	}

	
}

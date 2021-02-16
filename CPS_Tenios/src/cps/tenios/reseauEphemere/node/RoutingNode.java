package cps.tenios.reseauEphemere.node;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;

/**
 * Classe représentant un noeud de routage
 * @author Tenios
 *
 */
@OfferedInterfaces(offered = {CommunicationCI.class})
@RequiredInterfaces(required = {CommunicationCI.class, RegistrationCI.class})
public class RoutingNode extends Node {

	/**
	 * Constructeur preant URI du port sortant vers le gestionnaire réseau
	 * @param uri du port sortant vers le gestionnaire réseau
	 * @throws Exception s'il y a un probleme
	 */
	protected RoutingNode(String uri) throws Exception {
		super(uri);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws Exception {
		logMessage("Debut Execute RoutingNode");
		
		//TODO routingInboundPort
		voisin = super.registrationOutboundPort.registerRoutingNode(super.addr, super.INBOUNDPORT_URI, super.pos, 100.0, "");
		logMessage("Taille voisin " + voisin.size());
		for(ConnectionInfo c : voisin) {
			NodeAddressI addr = c.getAddress();
			String uriInbound = c.getCommunicationInboundURI(); //TODO modifier pour le routing
			System.out.println("TETSTSTST");
			super.connection(uriInbound).connectRouting(addr, uriInbound, "");
		}
		
		super.registrationOutboundPort.unregister(super.addr);
	}

	@Override
	public void transmitMessage(MessageI m) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasRouteFor(AddressI address) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void ping() throws Exception {
		// TODO Auto-generated method stub
		
	}

}

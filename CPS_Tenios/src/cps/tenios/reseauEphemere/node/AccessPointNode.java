package cps.tenios.reseauEphemere.node;


import java.util.HashMap;
import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.NetworkAddress;
import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import cps.tenios.reseauEphemere.interfaces.RoutingCI;
import fr.sorbonne_u.components.AbstractComponent;
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
	protected AccessPointNode(String uri) throws Exception {
		super(uri);
		this.ROUTING_INBOUNDPORT_URI = AbstractPort.generatePortURI();
		routingTable = new HashMap<NodeAddressI, Chemin>();
	}

	@Override
	public void execute() throws Exception {
		logMessage("Dans Access Point " + this.index);
		Set<ConnectionInfo> voisin = this.registrationOutboundPort.registerAccessPoint(this.addr, this.COMM_INBOUNDPORT_URI, this.pos, 100.0, "");
		
		for(ConnectionInfo c : voisin) {
			String uriInbound = c.getCommunicationInboundURI(); //TODO modifier pour le routing
			this.connection(uriInbound).connectRouting(this.addr, this.COMM_INBOUNDPORT_URI, this.ROUTING_INBOUNDPORT_URI);
		}
		
		
		
		logMessage("Fin");
		//this.registrationOutboundPort.unregister(this.addr);
	}

	@Override
	public void transmitMessage(MessageI m) throws Exception {
		if(m.getAddress().isNetworkAddress()) {
			return ;
		}
		super.transmitMessage(m);
		
	}

	@Override
	public int hasRouteFor(AddressI address) throws Exception {
		if(address.isNetworkAddress()) {
			return 0;
		}
		return ;
	}

	@Override
	public void ping() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return "AccessPointNode [" + super.toString() + "]";
	}
	

}

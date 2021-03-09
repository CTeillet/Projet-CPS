package cps.tenios.reseauEphemere.node;


import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.NetworkAddress;
import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
/**
 * Classe repr�sentant un point d'acc�s
 * @author Tenios
 *
 */
@OfferedInterfaces(offered = {CommunicationCI.class})
@RequiredInterfaces(required = {CommunicationCI.class, RegistrationCI.class})
public class AccessPointNode extends Node {
	/**
	 * Permet de creer un composant point d'acc�s en sp�cifiant l'URI du port de registration sortant
	 * @param uri du port de registration sortant
	 * @throws Exception s'il y a un probleme
	 */
	protected AccessPointNode(String uri) throws Exception {
		super(uri);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws Exception {
		logMessage("Dans Access Point " + this.index);
		voisin = this.registrationOutboundPort.registerAccessPoint(this.addr, this.INBOUNDPORT_URI, this.pos, 100.0, "");
		
		for(ConnectionInfo c : voisin) {
			String uriInbound = c.getCommunicationInboundURI(); //TODO modifier pour le routing
			this.connection(uriInbound).connectRouting(this.addr, this.INBOUNDPORT_URI, "");
		}
		
		
		showNeighbourg(voisin);
		logMessage("Fin");
		//this.registrationOutboundPort.unregister(this.addr);
	}

	@Override
	public void transmitMessage(MessageI m) throws Exception {
		if(m.getAddress() instanceof NetworkAddress) {
			return ;
		}
		super.transmitMessage(m);
		
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

	@Override
	public String toString() {
		return "AccessPointNode [" + super.toString() + "]";
	}
	

}

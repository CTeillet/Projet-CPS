package cps.tenios.reseauEphemere.node;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.NodeAddress;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;

/**
 * Classe repr�sentant un noeud terminal
 * @author Tenios
 *
 */
@OfferedInterfaces(offered = {CommunicationCI.class})
@RequiredInterfaces(required = {CommunicationCI.class, RegistrationCI.class})
public class TerminalNode extends Node {
	
	
	/**
	 * Constructeur initialisant un noeud terminal
	 * @param uri du port de registration
	 * @throws Exception s'il y a un probleme
	 */
	protected TerminalNode(String uri) throws Exception {
		super(uri);
	}

	@Override
	public void execute() throws Exception {
		logMessage("Debut Execute TerminalNode "+ index);
		voisin = this.registrationOutboundPort.registerTerminalNode(this.addr, this.INBOUNDPORT_URI, this.pos, 100.);
		
		for(ConnectionInfo c : voisin) {
			logMessage((c.getAddress()==this.addr) +"");
		}
		
		logMessage("voisisn " + voisin.size());
		//Connexion a ses voisins
		for(ConnectionInfo c : voisin) {
			NodeAddressI addr = c.getAddress();
			String uriInbound = c.getCommunicationInboundURI();
			this.connection(uriInbound).connect(this.addr, uriInbound); //Probleme sur l'un des deux
			
			logMessage("Moi " + this.addr + ", copain " + nodesOutboundPort.get(nodesOutboundPort.size()-1).getAddress()) ;
			
		}

		if(this.index==2) {
			MessageI m = new Message(new NodeAddress(1), "Bonjour", 8);
			logMessage("J'envoie le message " + m.getContent());
			try {
				//showNeighbourg(voisin);
				this.transmitMessage(m);
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		logMessage("Fin");
		//super.registrationOutboundPort.unregister(super.addr);
	}

	@Override
	public void ping() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		return "TerminalNode ["+super.toString()+"t]";
	}

	

}

package cps.tenios.reseauEphemere.node;

import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.NodeAddress;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
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
	protected TerminalNode(String uri, int i, int j, double r) throws Exception {
		super(uri, i, j, r);
	}

	@Override
	public void execute() throws Exception {
		logMessage("Debut Execute TerminalNode "+ index);
		Set<ConnectionInfo> voisin = this.registrationOutboundPort.registerTerminalNode(this.addr, this.COMM_INBOUNDPORT_URI, this.pos, this.range);
		logMessage("1");
		//Connexion a ses voisins
		for(ConnectionInfo c : voisin) {
			logMessage("voisin : " + c.getAddress());
			NodeOutboundPort out;
			if(c.isRouting()) {
				out = this.connectionRouting(c.getAddress(), c.getCommunicationInboundURI(), c.getRoutingInboundPortURI()); // Probleme
			}else {
				out = this.connection(c.getAddress(),c.getCommunicationInboundURI());
			}
			out.connect(this.addr, this.COMM_INBOUNDPORT_URI);
		}
		
		logMessage("1");
		if(this.index==2) {
			MessageI m = new Message(new NodeAddress(1), "Bonjour", 8);
			logMessage("J'envoie le message " + m.getContent());
						this.transmitMessage(m);
		}
		
		logMessage("Fin");
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

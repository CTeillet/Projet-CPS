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
 * Classe representant un noeud terminal
 * @author Tenios
 *
 */
@OfferedInterfaces(offered = {CommunicationCI.class})
@RequiredInterfaces(required = {CommunicationCI.class, RegistrationCI.class})
public class TerminalNode extends Node {

	private MessageI toSend;

	/**
	 * Constructeur initialisant un noeud terminal
	 * @param uri du port de registration
	 * @throws Exception s'il y a un probleme
	 */
	protected TerminalNode(String uri, NodeAddress addr, int i, int j, double r) throws Exception {
		super(uri, addr, i, j, r);
		toSend = null;
	}

	/**
	 * Constructeur initialisant un noeud terminal
	 * @param uri du port de registration
	 * @throws Exception s'il y a un probleme
	 */
	protected TerminalNode(String uri, NodeAddress addr, int i, int j, double r, MessageI msg) throws Exception {
		super(uri, addr, i, j, r);
		toSend = msg;
	}

	@Override
	public void execute() throws Exception {
		logMessage("Debut Execute TerminalNode "+ this.addr);
		// enrigistrement au près du gestionnaire
		Set<ConnectionInfo> voisin = this.registrationOutboundPort.registerTerminalNode(this.addr, this.COMM_INBOUNDPORT_URI, this.pos, this.range);
		//Connexion aux voisins
		for(ConnectionInfo c : voisin) {
			logMessage("voisin : " + c.getAddress());
			CommunicationOutboundPort out;
			if(c.isRouting()) {
				// ajout dans nouveau voisin 
				out = this.addRoutingNeighbour(c.getAddress(), c.getCommunicationInboundURI(), c.getRoutingInboundPortURI()); // Probleme
				// connexion au voisin pour qu'il ajoute le noeud courant
				out.connect(this.addr, this.COMM_INBOUNDPORT_URI);
			} else {
				logMessage("Ne doit pas arriver : 2 terN connected");
			}
		}

		if(toSend != null) {

			logMessage("J'envoie le message " + toSend.getContent());
			this.transmitMessage(toSend);
		}

		logMessage("\n Fin execute !!!\n");
	}

	@Override
	public void ping() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void transmitMessage(MessageI msg) throws Exception {
		//Copie du message 
		MessageI m = new Message((Message) msg);;

		// verifie si le message est arrivé a destination, mort ou a retransmettre
		if (this.checkMessage(m)) {
			this.seekNtransmit(m);
		}
	}

	@Override
	public String toString() {
		return "Terminal"+super.toString();
	};

}

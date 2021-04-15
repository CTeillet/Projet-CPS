package cps.tenios.reseauEphemere.node.request;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.node.Chemin;
import cps.tenios.reseauEphemere.node.CommunicationOutboundPort;
import cps.tenios.reseauEphemere.node.InfoRoutNode;
import cps.tenios.reseauEphemere.node.Message;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ComponentI.ComponentService;
import fr.sorbonne_u.components.ComponentI.ComponentTask;

public class TransmitRequest implements ComponentService{
	private MessageI m;
	private Chemin path2Network;
	private Map<AddressI, Chemin> routingTable;
	private List<InfoRoutNode> routingNodes;
	private AddressI addr;
	private ComponentI owner;

	
	
	public TransmitRequest(AbstractComponent owner, MessageI m, Chemin path2Network, Map<AddressI, Chemin> routingTable,
			List<InfoRoutNode> routingNodes, AddressI addr) {
		this.owner = owner;
		this.m = m;
		this.path2Network = path2Network;
		this.routingTable = routingTable;
		this.routingNodes = routingNodes;
		this.addr = addr;
	}

	@Override
	public Object call() throws Exception {
		// verifie si le message est arrivé a destination, mort ou a retransmettre
		if (checkMessage(m)) {
			// message a destion du reseau
			if (m.getAddress().isNetworkAddress()) {
				//si chemin connue
				if(path2Network != null) {
					this.path2Network.getNext().transmitMessage(m);
					return null;
				}

			} else {
				// Cherche l'adresse dans la table 
				Chemin path = routingTable.get(m.getAddress());
				if(path != null) {
					//logMessage("Gagner");
					path.getNext().transmitMessage(m);
					return null;
				}
			}
			this.seekNtransmit(m);
		}
		return null;
	}
	
	private void seekNtransmit(MessageI m) throws Exception {
		// cherche une route parmie ses voisins
		int rout = -1, tmp; 
		CommunicationOutboundPort next = null;
		
			for (InfoRoutNode n : routingNodes) {
				tmp = n.getNode().hasRouteFor(m.getAddress());
				// selsction de la route la plus courte
				if (-1 < tmp && tmp < rout) {
					rout = tmp;
					next = n.getNode();
				}
			}
		
		// transmet au voisin suivant si une route existe ou a tous sinon
		if(rout > -1) {
			next.transmitMessage(m);
			return;

		} else {
			inondation(m);
		}
	}
	private void inondation(MessageI m) throws Exception {
		//retransmet le message a tous ses voisins routeurs
		for (InfoRoutNode n : routingNodes) {
			//logMessage("Je transfere a " + n.getAdress());
			n.getNode().transmitMessage(m);
		}
	}
	
	protected boolean checkMessage(MessageI m) {
		//arriver a destination
		if(m.getAddress().equals(addr)) {
			//logMessage("Message recue : " + m.getContent());
			return false;
		}

		//Destruction 
		if(!m.stillAlive()) {
//			logMessage("Mort du Message");
			return false;
		}

		// decrementation pour retransmission
//		logMessage("Retransmission du Message");
		m.decrementsGops();
		return true;
	}


	@Override
	public Object getServiceProviderReference() {
		return null;
	}

	@Override
	public void setOwnerReference(ComponentI owner) {
		this.owner=owner;
		
	}

	@Override
	public AbstractComponent getServiceOwner() {
		// TODO Auto-generated method stub
		return (AbstractComponent) owner;
	}


}
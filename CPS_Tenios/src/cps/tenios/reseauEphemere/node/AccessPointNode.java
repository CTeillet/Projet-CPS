package cps.tenios.reseauEphemere.node;


import java.rmi.ConnectException;
import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.NodeAddress;
import cps.tenios.reseauEphemere.NodeConnector;
import cps.tenios.reseauEphemere.RoutingConnector;
import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import cps.tenios.reseauEphemere.interfaces.RoutingCI;
import fr.sorbonne_u.components.ComponentI;
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
	protected AccessPointNode(String uri, NodeAddress addr, int i, int j, double r) throws Exception {
		super(uri, addr, i, j, r);
	}

	@Override
	public void execute() throws Exception {
		logMessage("Dans Access Point " + this.addr);
		Set<ConnectionInfo> voisin = this.registrationOutboundPort.registerAccessPoint(this.addr, this.COMM_INBOUNDPORT_URI, this.pos, 100.0, this.ROUTING_INBOUNDPORT_URI);
		logMessage("my"+this.ROUTING_INBOUNDPORT_URI);
		for(ConnectionInfo c : voisin) {
			logMessage("getRoutingInboundPortURI : "+c.getRoutingInboundPortURI());
			ajoutVoisins(c);
		}
		logMessage("\n Fin !!!\n");
		
//		while(true) {
//			Thread.sleep(1000);
//			testVoisins();
//		}
	}

	private void ajoutVoisins(ConnectionInfo c) throws Exception {

		runTask(super.indexCommunication, new FComponentTask() {

			@Override
			public void run(ComponentI owner) {
				CommunicationOutboundPort out; // port de communication sortant 
				try {

					if (c.isRouting()) {
						// ajout d'un voisin routeur
						logMessage("ajout d'un voisin routeur " + c.getAddress());
						out = addRoutingNeighbour(c.getAddress(), c.getCommunicationInboundURI(), c.getRoutingInboundPortURI());
					} else {
						// ajout d'un voisin terminal
						logMessage("ajout d'un voisin terminal " + c.getAddress());
						out = addTerminalNeighbour(c.getAddress(), c.getCommunicationInboundURI());
					}
					// connexion au voisin pour qu'il ajoute le noeud courant
					out.connectRouting(addr, COMM_INBOUNDPORT_URI, ROUTING_INBOUNDPORT_URI);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void transmitMessage(MessageI msg) throws Exception {
		//Copie du message 
		MessageI m = new Message((Message) msg);;
		
		runTask(super.indexCommunication, new FComponentTask() {

			@Override
			public void run(ComponentI owner) {
				try {
					try {
						testVoisins();
					} catch (ConnectException e1) {
						System.out.println(e1.getMessage());
					}
					// verifie si le message est arrivé a destination, a destination du reseau, mort ou a retransmettre
					if (checkMessage(m)) {
						// Cherche l'adresse dans la table 
						lockTable.lock();
						TousChemins path = routingTable.get(m.getAddress());
						if(path != null) {
							path.getFirstChemin().getNext().transmitMessage(m);
							lockTable.unlock();
							return ;
						}
						lockTable.unlock();
						
						seekNtransmit(m);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
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

		if(address.equals(this.addr) || address.isNetworkAddress()) {
			return 0;
		}
		lockTable.lock();
		TousChemins path = routingTable.get(address);
		lockTable.unlock();
		if(path!=null) {
			return path.getFirstChemin().getNumberOfHops();
		}
		return -1;
	}


	@Override
	protected CommunicationOutboundPort addRoutingNeighbour(AddressI addr, String nodeInboundPortURI, String routingInboundPortURI) throws Exception {
		logMessage("addRoutingNeighbour " + addr);
		CommunicationOutboundPort nodeOutbound = null;
		RoutingOutboundPort routOutbound = null;
		logMessage("avant tyr");
		// Connexion au node par un port de Communication
		nodeOutbound = new CommunicationOutboundPort(this);
		nodeOutbound.publishPort();
		doPortConnection(nodeOutbound.getPortURI(), nodeInboundPortURI, NodeConnector.class.getCanonicalName());

		//Connexion au RoutingNodeOutboundPort
		routOutbound = new RoutingOutboundPort(this);
		routOutbound.publishPort();
		doPortConnection(routOutbound.getPortURI(), routingInboundPortURI, RoutingConnector.class.getCanonicalName());

		// ajout du noeud dans list des voisins
		lockRoutNodes.lock();
		try {
			routingNodes.add(new InfoRoutNode(addr, nodeOutbound, routOutbound));
		} catch (Exception e) {
			e.printStackTrace();
		}
		lockRoutNodes.unlock();

		// mis a jour du nouvau voisins
		lockTable.lock();
		routingTable.put(addr, new TousChemins(nodeOutbound, 1));
		lockTable.unlock();

		logMessage("lancement update ");
		routOutbound.updateRouting(this.addr, this.getInfoTableRout());
		routOutbound.updateAccessPoint(this.addr, 0);
		logMessage("addRoutingNeighbour fin" + addr);
		return nodeOutbound;
	}



	@Override
	public void updateAccessPoint(AddressI neighbour, int numberOfHops) throws Exception {}

	@Override
	public String toString() {
		return "AcessPoint"+super.toString();
	};

}

package cps.tenios.reseauEphemere.node;

import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
 * Classe representant un noeud de routage
 * @author Tenios
 *
 */
@OfferedInterfaces(offered = {CommunicationCI.class, RoutingCI.class})
@RequiredInterfaces(required = {CommunicationCI.class, RegistrationCI.class, RoutingCI.class})
public class RoutingNode extends Router2Test {

	/**
	 * Chemin vers le reseau classique
	 */
	protected Chemin path2Network;
	/**
	 * Verrou qui protege les acces concurrent au path2Network
	 */
	protected Lock lockP2N = new ReentrantLock();

	/**
	 * Constructeur de RoutingNode
	 * @param uri Uri du port sortant vers le gestionnaire reseau
	 * @param i Abscisse de la coordonee du RoutingNode
	 * @param j Ordonnee de la coordonee du RoutingNode
	 * @param r Portee du signal 
	 * @throws Exception En case de probleme
	 */
	protected RoutingNode(String uri, NodeAddress addr, int i, int j, double r) throws Exception {
		super(uri, addr, i, j, r);
		path2Network = null;
	}

	@Override
	public void execute() throws Exception {
		logMessage("Debut Execute RoutingNode " + this.addr);
		// enrigistrement au près du gestionnaire
		Set<ConnectionInfo> voisin = this.registrationOutboundPort.registerRoutingNode(this.addr, this.COMM_INBOUNDPORT_URI, this.pos, this.range, ROUTING_INBOUNDPORT_URI);
		logMessage("ajout de" + voisin.size() + " voisin ");
		for(ConnectionInfo c : voisin) {
			ajoutVoisins(c);
		}
		logMessage("\n Fin !!!\n");
	}

	/**
	 * @param c
	 * @throws Exception
	 */
	private void ajoutVoisins(ConnectionInfo c) throws Exception {
		
		runTask(super.indexCommunication, new FComponentTask() {
			
			@Override
			public void run(ComponentI owner) {
				// TODO Auto-generated method stub
				CommunicationOutboundPort out; // port de communication sortant 
				try {
					if (c.isRouting()) {
						// ajout d'un voisin routeur
						logMessage("avant addRouting");

						out = addRoutingNeighbour(c.getAddress(), c.getCommunicationInboundURI(), c.getRoutingInboundPortURI());

						logMessage("add ok routeur : " + (out!= null));
					} else {
						// ajout d'un voisin terminal
						logMessage("avant addTerminal");
						out = addTerminalNeighbour(c.getAddress(), c.getCommunicationInboundURI());
						logMessage("addTerminal ok");
					}
					logMessage("avant connect");
					// connexion au voisin pour qu'il ajoute le noeud courant
					out.connectRouting(addr, COMM_INBOUNDPORT_URI, ROUTING_INBOUNDPORT_URI);
					logMessage("apres connect");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	@Override
	public int hasRouteFor(AddressI address) throws Exception {

		if(address.equals(this.addr)) {
			return 0;
		}

		if(address.isNetworkAddress()) {
			if (path2Network != null) {
				return path2Network.getNumberOfHops();
			}
		} else {
			lockTable.lock();
			Chemin chemin = routingTable.get(address);
			lockTable.unlock();
			if(chemin != null) {
				return chemin.getNumberOfHops();
			}
		}

		return -1;
	}

	@Override
	public void transmitMessage(MessageI msg) throws Exception {
		MessageI m = new Message((Message) msg);

		//handleRequest(super.indexTransmit,new TransmitRequest(this, m, path2Network, routingTable, routingNodes, addr));

		// TODO voir aussi ComponentTask 
		runTask(super.indexCommunication, new FComponentTask() {
			@Override
			public void run(ComponentI owner) {
				// verifie si le message est arrivé a destination, mort ou a retransmettre
				if (checkMessage(m)) {
					// message a destion du reseau
					if (m.getAddress().isNetworkAddress()) {
						//si chemin connue
						lockP2N.lock();
						if(path2Network != null) {
							try {
								CommunicationOutboundPort next = path2Network.getNext();
								lockP2N.unlock();
								next.transmitMessage(m);
							} catch (Exception e) {
								e.printStackTrace();
							}

							return;
						}
						lockP2N.unlock();

					} else {
						// Cherche l'adresse dans la table 
						lockTable.lock();
						Chemin path = routingTable.get(m.getAddress());
						lockTable.unlock();
						if(path != null) {
							//logMessage("Gagner");
							try {
								path.getNext().transmitMessage(m);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return;
						}
					}
					try {
						seekNtransmit(m);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}				
			}
		});
	}



	@Override
	public void updateAccessPoint(AddressI neighbour, int numberOfHops) throws Exception {
		//section critique
		runTask(this.indexUpdate, new FComponentTask() {

			@Override
			public void run(ComponentI owner)  {
				logMessage("dans update");
				try {
					lockP2N.lock();
					if(path2Network == null || path2Network.getNumberOfHops() > numberOfHops + 1) {

						path2Network = new Chemin(findNodeOutboundPort(neighbour), numberOfHops + 1);

						lockP2N.unlock();
						lockRoutNodes.lock();
						logMessage("lockRout pris");
						// propagation de l'update
						for(InfoRoutNode v : routingNodes) {
							if (!neighbour.equals(v.getAddress()))
								try {
									v.getRout().updateAccessPoint(getAddr(), path2Network.getNumberOfHops());
								} catch (Exception e) {
									e.printStackTrace();
								}
						}
						logMessage("lockRout relache");
						lockRoutNodes.unlock();
						//tableAndRoutes.notifyAll();
					} else {
						lockP2N.unlock();
					}

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				logMessage("fin update");
			}
		});
	}

	@Override
	protected CommunicationOutboundPort addRoutingNeighbour(AddressI addr, String nodeInboundPortURI, String routingInboundPortURI) throws Exception {
		logMessage("addRoutingNeighbour " + addr);
		CommunicationOutboundPort nodeOutbound = null;
		RoutingOutboundPort routOutbound = null;
		logMessage("avant tyr");
		try {
			// Connexion au node par un port de Communication
			nodeOutbound = new CommunicationOutboundPort(this);
			nodeOutbound.publishPort();
			doPortConnection(nodeOutbound.getPortURI(), nodeInboundPortURI, NodeConnector.class.getCanonicalName());
			logMessage("Communication ");
			//Connexion au RoutingNodeOutboundPort
			routOutbound = new RoutingOutboundPort(this);
			routOutbound.publishPort();
			doPortConnection(routOutbound.getPortURI(), routingInboundPortURI, RoutingConnector.class.getCanonicalName());
			logMessage("Rout");
		} catch (Exception e ) {
			e.printStackTrace();
		}
		logMessage("isCreate=" + (routOutbound != null) + ", isPublished=" + routOutbound.isPublished());

		logMessage("avant synchro");

		// ajout du noeud dans list des voisins
		lockRoutNodes.lock();
		//logMessage("lockRout pris");
		try {
			routingNodes.add(new InfoRoutNode(addr, nodeOutbound, routOutbound));
		} catch (Exception e) {
			e.printStackTrace();
		}
		//logMessage("ajout ok");
		lockRoutNodes.unlock();
		
		//logMessage("lockRout relache");
		// mis a jour du nouvau voisins
		lockTable.lock();
		//logMessage("lockTable pris");
		routingTable.put(addr, new Chemin(nodeOutbound, 1));
		//logMessage("lockTable relache");
		lockTable.unlock();
		//logMessage("locktable");

		int hops = -1;
		lockP2N.lock();
		//logMessage("p2n pris");
		if (path2Network != null) {
			hops = path2Network.getNumberOfHops();
		}
		//logMessage("p2n relache");
		lockP2N.unlock();
		//logMessage("p2n");

		if (hops > -1) {
			routOutbound.updateAccessPoint(this.addr, hops);
		}
		routOutbound.updateRouting(this.addr, this.getInfoTableRout());

		logMessage("fin add" + addr);
		return nodeOutbound;
	}
	
	@Override
	public String toString() {
		return "Routing"+super.toString();
	};
}

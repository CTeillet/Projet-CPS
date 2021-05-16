package cps.tenios.reseauEphemere.node;

import java.rmi.ConnectException;
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
		// enregistrement au près du gestionnaire
		Set<ConnectionInfo> voisin = this.registrationOutboundPort.registerRoutingNode(this.addr, this.COMM_INBOUNDPORT_URI, this.pos, this.range, ROUTING_INBOUNDPORT_URI);
		logMessage("ajout de" + voisin.size() + " voisin ");
		for(ConnectionInfo c : voisin) {
			ajoutVoisins(c);
		}
		logMessage("\n Fin !!!\n");

		//		while(true) {
		//			Thread.sleep(1000);
		//			testVoisins();
		//		}
	}

	/**
	 * @param c
	 * @throws Exception
	 */
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
			TousChemins chemin = routingTable.get(address);
			lockTable.unlock();
			if(chemin != null) {
				return chemin.getFirstChemin().getNumberOfHops();
			}
		}

		return -1;
	}

	@Override
	public void transmitMessage(MessageI msg) throws Exception {
		MessageI m = new Message((Message) msg);

		runTask(super.indexCommunication, new FComponentTask() {
			@Override
			public void run(ComponentI owner) {
				try {
					try {
						testVoisins();
					} catch (ConnectException e1) {
						System.out.println(e1.getMessage());
					}
					// verifie si le message est arrivé a destination, mort ou a retransmettre
					if (checkMessage(m)) {
						// message a destion du reseau
						if (m.getAddress().isNetworkAddress()) {
							//si chemin connue
							lockP2N.lock();
							if(path2Network != null) {

								CommunicationOutboundPort next = path2Network.getNext();
								lockP2N.unlock();
								next.transmitMessage(m);

								return;
							}
							lockP2N.unlock();

						} else {
							// Cherche l'adresse dans la table 
							lockTable.lock();
							TousChemins tc = routingTable.get(m.getAddress());
							if(tc != null) {
								CommunicationOutboundPort path = tc.getFirstChemin().getNext();
								lockTable.unlock();
								path.transmitMessage(m);;

								return;
							} else {
								lockTable.unlock();
							}
						}

						seekNtransmit(m);

					}	
				} catch (Exception e) {
					e.printStackTrace();
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
					try {
						testVoisins();
					} catch (ConnectException e1) {
						System.out.println(e1.getMessage());
					}
					lockP2N.lock();
					if(path2Network == null || path2Network.getNumberOfHops() > numberOfHops + 1) {

						path2Network = new Chemin(findNodeOutboundPort(neighbour), numberOfHops + 1);

						lockP2N.unlock();
						lockRoutNodes.lock();
						// propagation de l'update
						for(InfoRoutNode v : routingNodes) {
							if (!neighbour.equals(v.getAddress()))
								try {
									v.getRout().updateAccessPoint(getAddr(), path2Network.getNumberOfHops());
								} catch (Exception e) {
									e.printStackTrace();
								}
						}
						lockRoutNodes.unlock();
						//tableAndRoutes.notifyAll();
					} else {
						lockP2N.unlock();
					}

				} catch (Exception e1) {
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
		// Connexion au node par un port de Communication
		nodeOutbound = new CommunicationOutboundPort(this);
		nodeOutbound.publishPort();
		doPortConnection(nodeOutbound.getPortURI(), nodeInboundPortURI, NodeConnector.class.getCanonicalName());

		//Connexion au RoutingNodeOutboundPort
		routOutbound = new RoutingOutboundPort(this);
		routOutbound.publishPort();
		doPortConnection(routOutbound.getPortURI(), routingInboundPortURI, RoutingConnector.class.getCanonicalName());

		//logMessage("isCreate=" + (routOutbound != null) + ", isPublished=" + routOutbound.isPublished());


		// ajout du noeud dans list des voisins
		lockRoutNodes.lock();

		routingNodes.add(new InfoRoutNode(addr, nodeOutbound, routOutbound));

		lockRoutNodes.unlock();

		// mis a jour du nouveau voisins dans la table
		lockTable.lock();
		routingTable.put(addr, new TousChemins(nodeOutbound, 1));
		lockTable.unlock();

		int hops = -1;
		lockP2N.lock();
		if (path2Network != null) {
			hops = path2Network.getNumberOfHops();
		}
		lockP2N.unlock();

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

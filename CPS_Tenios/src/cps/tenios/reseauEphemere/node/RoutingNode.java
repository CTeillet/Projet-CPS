package cps.tenios.reseauEphemere.node;

import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.NodeConnector;
import cps.tenios.reseauEphemere.RoutingConnector;
import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import cps.tenios.reseauEphemere.interfaces.RoutingCI;
import cps.tenios.reseauEphemere.node.request.TransmitRequest;
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
	 * Constructeur de RoutingNode
	 * @param uri Uri du port sortant vers le gestionnaire reseau
	 * @param i Abscisse de la coordonee du RoutingNode
	 * @param j Ordonnee de la coordonee du RoutingNode
	 * @param r Portee du signal 
	 * @throws Exception En case de probleme
	 */
	protected RoutingNode(String uri, int i, int j, double r) throws Exception {
		super(uri, i, j, r);
		path2Network = null;
	}

	@Override
	public void execute() throws Exception {
		logMessage("Debut Execute RoutingNode " + this.index);
		// enrigistrement au près du gestionnaire
		Set<ConnectionInfo> voisin = this.registrationOutboundPort.registerRoutingNode(this.addr, this.COMM_INBOUNDPORT_URI, this.pos, this.range, ROUTING_INBOUNDPORT_URI);
		//logMessage("Taille voisin " + voisin.size());
		//Connexion aux voisins
		for(ConnectionInfo c : voisin) {
			CommunicationOutboundPort out; // port de communication sortant 

			if (c.isRouting()) {
				// ajout d'un voisin routeur
				out = this.addRoutingNeighbour(c.getAddress(), c.getCommunicationInboundURI(), c.getRoutingInboundPortURI());
				logMessage("add ok routeur : " + (out!= null));
			} else {
				// ajout d'un voisin terminal
				out = this.addTerminalNeighbour(c.getAddress(), c.getCommunicationInboundURI());
			}
			logMessage("avant connect");
			// connexion au voisin pour qu'il ajoute le noeud courant
			out.connectRouting(this.addr, this.COMM_INBOUNDPORT_URI, this.ROUTING_INBOUNDPORT_URI);
			logMessage("apres connect");
			// ajout du noeud a la table de routage
			routingTable.put(c.getAddress(), new Chemin(out, 1));
		}
		logMessage("Fin");
	}

	@Override
	public void ping() throws Exception {
		// TODO Auto-generated method stub
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
			Chemin chemin = routingTable.get(address);
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
		runTask(super.indexTransmit, new FComponentTask() {
			@Override
			public void run(ComponentI owner) {
				// verifie si le message est arrivé a destination, mort ou a retransmettre
				if (checkMessage(m)) {
					// message a destion du reseau
					if (m.getAddress().isNetworkAddress()) {
						//si chemin connue
						if(path2Network != null) {
							try {
								path2Network.getNext().transmitMessage(m);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return;
						}

					} else {
						// Cherche l'adresse dans la table 
						Chemin path = routingTable.get(m.getAddress());
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
			public void run(ComponentI owner) {
				if(path2Network == null || path2Network.getNumberOfHops() > numberOfHops + 1) {
					path2Network = new Chemin(findNodeOutboundPort(neighbour), numberOfHops + 1);
					// propagation de l'update
					for(InfoRoutNode v : routingNodes) {
						if (!neighbour.equals(v.getAdress()))
							try {
								v.getRout().updateAccessPoint(getAddr(), path2Network.getNumberOfHops());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
				} 				
			}
		});
	}

	@Override
	public String toString() {
		return "RoutingNode ["+ super.toString() +"]";
	}

	@Override
	protected CommunicationOutboundPort addRoutingNeighbour(AddressI addr, String nodeInboundPortURI, String routingInboundPortURI) throws Exception {
		logMessage("addRoutingNeighbour " + addr);
		try {
			// Connexion au node par un port de Communication
			CommunicationOutboundPort nodeOutbound = new CommunicationOutboundPort(this);
			nodeOutbound.publishPort();
			doPortConnection(nodeOutbound.getPortURI(), nodeInboundPortURI, NodeConnector.class.getCanonicalName());
			
			//Connexion au RoutingNodeOutboundPort
			RoutingOutboundPort routOutbound = new RoutingOutboundPort(this);
			routOutbound.publishPort();
			doPortConnection(routOutbound.getPortURI(), routingInboundPortURI, RoutingConnector.class.getCanonicalName());
			//logMessage("isCreate=" + (routOutbound != null) + ", isPublished=" + routOutbound.isPublished());

			logMessage("avant synchro");
			int hops = -1;
			//section critique
				// ajout du noeud dans list des voisins
				routingNodes.add(new InfoRoutNode(addr, nodeOutbound, routOutbound));
				// mis a jour du nouvau voisins
				routingTable.put(addr, new Chemin(nodeOutbound, 1));
				if (path2Network != null) {
					hops = path2Network.getNumberOfHops();
				}
			//fin
//					if (hops > -1) {
//						routOutbound.updateAccessPoint(this.addr, hops);
//					}
//					routOutbound.updateRouting(this.addr, this.getInfoTableRout());
			logMessage("fin add");
			return nodeOutbound;

		} catch (Exception e) {
			e.printStackTrace();// TODO: handle exception
			throw e;
		}
	}
}

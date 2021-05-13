package cps.tenios.reseauEphemere.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import cps.tenios.reseauEphemere.NodeAddress;
import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import cps.tenios.reseauEphemere.interfaces.RouteInfoI;
import cps.tenios.reseauEphemere.interfaces.RoutingCI;
import fr.sorbonne_u.components.AbstractPort;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@OfferedInterfaces(offered = {CommunicationCI.class, RoutingCI.class})
@RequiredInterfaces(required = {CommunicationCI.class, RegistrationCI.class, RoutingCI.class})
public abstract class Router extends Node {
	
	/**
	 * Table de routage
	 */
	protected Map<AddressI, Chemin> routingTable;
	
	/**
	 * URI du port de Routage entrant
	 */
	protected final String ROUTING_INBOUNDPORT_URI;
	/**
	 * Port de routage entrants
	 */
	protected RoutingInboundPort routInbound;
	
	/**
	 * Constructeur de d'un noeud capable de router
	 * @param uri Uri du port sortant vers le gestionnaire reseau
	 * @param i Abscisse de la coordonee du noeud
	 * @param j Ordonnee de la coordonee du noeud
	 * @param r Portee du signal 
	 * @throws Exception En case de probleme
	 */
	public Router(String uri, NodeAddress addr, int i, int j, double r) throws Exception {
		super(uri, addr, i, j, r);
		routingTable = new HashMap<AddressI, Chemin>();
		ROUTING_INBOUNDPORT_URI = AbstractPort.generatePortURI();
		routInbound = new RoutingInboundPort(ROUTING_INBOUNDPORT_URI, this);
		routInbound.publishPort();
	}

	
	@Override
	public void ping() throws Exception {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Permet de mettre a jour la route la plus optimale vers la destination et met a jour les noeuds voisins en cas de changement
	 * @param neighbour voisin ayant envoyé les information de stable de routage
	 * @param routes routes vers les adresses
	 * @throws Exception en cas de probleme
	 */
	public void updateRouting(AddressI neighbour, Set<RouteInfoI> routes) throws Exception {
		
		boolean hasChanged = false;
		
		for(RouteInfoI ri : routes) {
			
			if (ri.getDestination().isNodeAddress()) {
				Chemin tmp = routingTable.get(ri.getDestination());
				//Si pas de route vers address => creation d'un nouveau chemin
				if(tmp == null) {
					hasChanged = true;
					routingTable.put( (AddressI)ri.getDestination(), new Chemin(this.findNodeOutboundPort(neighbour), ri.getNumberOfHops()));
				
				// Si meilleur route => Maj
				} else if (tmp.getNumberOfHops() > ri.getNumberOfHops() + 1){
					hasChanged = true;
					// TODO remplacer par une list et ajouter
					tmp.setNext(this.findNodeOutboundPort(neighbour));
					tmp.setNumberOfHops(ri.getNumberOfHops() + 1);
				}
			}
		}
		//propage l'update en cas de changement
		if(hasChanged) {
			propageUpdate(neighbour);
		}
	}
	
	/**
	 * Propage la mis a jour des tables 
	 * @param neighbour adresse du voisins ayant propager sa mis a jour
	 * @throws Exception
	 */
	protected void propageUpdate(AddressI neighbour) throws Exception{
		
		Set<RouteInfoI> r = this.getInfoTableRout();

		for(InfoRoutNode rn : routingNodes) {
			if(!rn.getAddress().equals(neighbour)) {
				rn.getRout().updateRouting(this.getAddr(), r);
			}
		}
	}
	
	/**
	 * retourne les informations de la table de routage
	 * @return les informations de la table de routage
	 */
	protected Set<RouteInfoI> getInfoTableRout() {
		Set<RouteInfoI> voisins = new HashSet<>();
		for(Entry<AddressI, Chemin> v : routingTable.entrySet()) {
			voisins.add(new RouteInfo(v.getKey(), v.getValue().getNumberOfHops()));
		}
		return voisins;
	}
	

	/**
	 * Trouve depuis une adresse le port de Communication sortant correspondant
	 * @param adrr addresse du noeud recherche
	 * @return port de Communication sortant
	 */
	protected CommunicationOutboundPort findNodeOutboundPort(AddressI adrr) {
		for(InfoRoutNode node : routingNodes) {
			if(node.getAddress().equals(adrr)) {
				return node.getNode();
			}
		}
		for(InfoTerminalN node : terminalNodes) {
			if(node.getAddress().equals(adrr)) {
				return node.getNode();
			}
		}
		return null;
	}
	
	/**
	 * Trouve depuis une adresse le port de Routage sortant correspondant
	 * @param adrr addresse du noeud recherche
	 * @return port de Routage sortant
	 */
	protected RoutingOutboundPort findRoutingOutboundPort(AddressI adrr) {
		for(InfoRoutNode node : routingNodes) {
			if(node.getAddress().equals(adrr)) {
				return node.getRout();
			}
		}
		return null;
	}
	
	/**
	 * Permet de mettre a jour la route la plus courte vers un point d'acces
	 * @param neighbour voisins ayant envoyer les information vers le point d'acces
	 * @param numberOfHops nombre de saut requis pour y arriver
	 * @throws Exception en cas de probleme
	 */
	public abstract void updateAccessPoint(AddressI neighbour, int numberOfHops) throws Exception;

	@Override
	public abstract void execute() throws Exception;
	
	@Override
	public abstract void transmitMessage(MessageI msg) throws Exception;

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.routInbound.unpublishPort();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
}

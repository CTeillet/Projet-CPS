package cps.tenios.reseauEphemere.gestionnaireReseau;

import java.util.HashSet;
import java.util.Set;

import cps.tenios.reseauEphemere.ConnectionInfo;
import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.PositionI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
/**
 * Gestionnaire d'un reseau de noeud terminaux, routeurs et points d'acces
 * @author Tenios
 *
 */
@OfferedInterfaces(offered = {RegistrationCI.class})
public class GestionnaireReseau extends AbstractComponent {
	/**
	 * URI du port entrant de registration
	 */
	public final static String  INBOUNDPORT_URI = "registration-uri";
	/**
	 * port entrant registration
	 */
	private RegistrationInboundPort registrationInboundPort;

	/**
	 * ensemble des noeuds terminaux du reseau
	 */
	private Set<ConnectionInfo> tableNoeudTerminal;
	/**
	 * ensemble des points d'acces du reseau
	 */
	private Set<ConnectionInfo> tableNoeudAccess;
	/**
	 * ensemble des noeuds de routage du reseau
	 */
	private Set<ConnectionInfo> tableNoeudRouting;
	
	/**
	 * Constructeur de gestionnaire reseau
	 * @throws Exception si il y a un probleme
	 */
	protected GestionnaireReseau() throws Exception {
		super(1, 0);
		this.registrationInboundPort = new RegistrationInboundPort(INBOUNDPORT_URI, this);
		this.registrationInboundPort.publishPort();
		
		tableNoeudTerminal = new HashSet<ConnectionInfo>();
		tableNoeudAccess = new HashSet<ConnectionInfo>();
		tableNoeudRouting = new HashSet<ConnectionInfo>();
		
		
		this.toggleLogging();
		this.toggleTracing();
	}
	
	/**
	 * Enregistre un nouveau noeud Terminal dans la table "tableNoeudTerminal" 
	 * @param address Adresse du noeud
	 * @param communicationInboundPortURI URI du port entrant du noeud
	 * @param initialPosition Position initial du noeud
	 * @param initialRange Porte de communication du noeud
	 * @return ensemble des voisins accessibles
	 */
	public Set<ConnectionInfo> registerTerminalNode(AddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange){
		ConnectionInfo c = new ConnectionInfo(address, communicationInboundPortURI, false, "", initialPosition);
		
		Set<ConnectionInfo> res = new HashSet<ConnectionInfo>();  // Ensemble des noeuds accessibles
		// Ajout des noeud capable de router a sa porté
		synchronized (tableNoeudRouting) {
			tableNoeudRouting.stream()
			.filter( x -> x.getPosition().distance(initialPosition)<=initialRange)
			.forEach(x-> res.add(x));
		}
		synchronized (tableNoeudAccess) {
			tableNoeudAccess.stream()
			.filter( x -> x.getPosition().distance(initialPosition)<=initialRange)
			.forEach(x-> res.add(x));
		}
		
		// Ajout du noeud a la table
		synchronized (tableNoeudTerminal) {
			tableNoeudTerminal.add(c);
		}
		
		logMessage("TerminalNode:" + address + "\ntaille=" + size());
		return res;	
	}
	
	/**
	 * Enregistre un nouveau Point d'acces dans la table "tableNoeudAccess"
	 * @param address Adresse du noeud
	 * @param communicationInboundPortURI URI du port entrant du noeud
	 * @param initialPosition Position initial du noeud
	 * @param initialRange Porte de communication du noeud
	 * @param routingInboundPortURI URI du port entrant de routage
	 * @return ensemble des voisins accessible
	 */
	public Set<ConnectionInfo> registerAccessPoint(AddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI){
		
		Set<ConnectionInfo> res = new HashSet<ConnectionInfo>(); // Ensemble des noeuds accessibles
		ConnectionInfo c = new ConnectionInfo(address, communicationInboundPortURI, true, routingInboundPortURI, initialPosition);
		
		
		// Ajout des RoutingNode a sa porté
		synchronized (tableNoeudRouting) {
			tableNoeudRouting.stream()
			.filter( x -> x.getPosition().distance(initialPosition)<=initialRange)
			.forEach(x-> res.add(x));
		}
		// Ajout des TerminalNode a sa porté
		synchronized (tableNoeudTerminal) {
			tableNoeudTerminal.stream()
		.filter( x -> x.getPosition().distance(initialPosition)<=initialRange)
		.forEach(x-> res.add(x));
		}
		
		synchronized (tableNoeudAccess) {
			// ajout de tous les AccessPointNode
			res.addAll(tableNoeudAccess);
			
			// Ajout du noeud a la table
			tableNoeudAccess.add(c);
		}
		
		logMessage("AccessPoint: " + address + "\ntaille=" + size());
		return res;
	}
	
	/**
	 * Enregistre un nouveau routeur dans la table "tableNoeudAccess"
	 * @param address Adresse du noeud
	 * @param communicationInboundPortURI URI du port entrant du noeud
	 * @param initialPosition Position initial du noeud
	 * @param initialRange Porte de communication du noeud
	 * @param routingInboundPortURI URI du port entrant de routage
	 * @return ensemble des voisins accessible
	 */
	public Set<ConnectionInfo> registerRoutingNode(AddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI){
		
		Set<ConnectionInfo> res = new HashSet<ConnectionInfo>();  // Ensemble des noeuds accessibles
		ConnectionInfo c = new ConnectionInfo(address, communicationInboundPortURI, true, routingInboundPortURI, initialPosition);
		
		// Ajout de tous les noeuds a sa porté
		synchronized(tableNoeudAccess) {
			tableNoeudAccess.stream()
			.filter( x -> x.getPosition().distance(initialPosition)<=initialRange)
			.forEach(x-> res.add(x));
		}
		synchronized (tableNoeudTerminal) {
			tableNoeudTerminal.stream()
			.filter( x -> x.getPosition().distance(initialPosition)<=initialRange)
			.forEach(x-> res.add(x));
		}
		synchronized (tableNoeudRouting) {
			tableNoeudRouting.stream()
			.filter( x -> x.getPosition().distance(initialPosition)<=initialRange)
			.forEach(x-> res.add(x));

			// Ajout du noeud a la table
			tableNoeudRouting.add(c);
		}
		
		logMessage("RoutingNode:" + address + "\ntaille=" + size());
		return res;
	}
	
	/**
	 * Retire un noeud des tables 
	 * @param address Adresse du noeud a retiré
	 * @throws Exception si une exception est throw
	 */
	public void unregister (AddressI address) throws Exception{
		tableNoeudAccess.removeIf(c-> c.getAddress()==address);
		tableNoeudRouting.removeIf(c-> c.getAddress()==address);
		tableNoeudTerminal.removeIf(c-> c.getAddress()==address);
		logMessage("Unregister taille ensemble " + size());
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
			this.registrationInboundPort.unpublishPort();
			super.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}
	
	@Override
	public void finalise() throws Exception {
		System.out.println(this.toString());
		super.finalise();
	}
	
	/**
	 * Retourne le nombre d'elements des tables 
	 * @return le nombre d'elements des tables
	 */
	private int size() {
		return tableNoeudAccess.size() + tableNoeudRouting.size() + tableNoeudTerminal.size();
	}

	@Override
	public String toString() {
		String str = "GestionnaireReseau :\ntableNoeudTerminal=";
		for (ConnectionInfo ci : tableNoeudTerminal) {
			str += "\n\t" + ci.getAddress();
		}
		
		str += "\ntableNoeudRouting=";
		for (ConnectionInfo ci : tableNoeudRouting) {
			str += "\n\t" + ci.getAddress();
		}
		
		str += "\ntableNoeudAccess=";
		for (ConnectionInfo ci : tableNoeudAccess) {
			str += "\n\t" + ci.getAddress();
			
		}
		return str;
	}
}

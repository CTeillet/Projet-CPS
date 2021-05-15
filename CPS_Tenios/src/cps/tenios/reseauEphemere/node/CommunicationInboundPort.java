package cps.tenios.reseauEphemere.node;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class CommunicationInboundPort extends AbstractInboundPort implements CommunicationCI {

	/**
	 *  Version de s�rie de la classe
	 */
	private static final long serialVersionUID = 3226720899115114653L;

	/**
	 * Permet de cr�er un port entrant pour un noeud en sp�cifiant son propri�taire
	 * @param owner propri�taire du noeud
	 * @throws Exception s'il y a un probleme
	 */
	public CommunicationInboundPort(ComponentI owner) throws Exception {
		super(CommunicationCI.class, owner);
		assert owner.isOfferedInterface(CommunicationCI.class);
	}

	/**
	 * Cr�e un port en prenant l'uri voulue pour le port, ainsi que son propri�taire
	 * @param uri voulue pour le port
	 * @param owner propri�taire de classe
	 * @throws Exception s'il y a un probleme
	 */
	public CommunicationInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, CommunicationCI.class, owner);
		assert owner.isOfferedInterface(CommunicationCI.class);
	}

	@Override
	public void connect(AddressI address, String communicationInboundPortURI) throws Exception {
		this.getOwner().runTask( c -> {
			try {
				if (c instanceof Node) {((Node)c).connect(address, communicationInboundPortURI);}
				else {((Router2Test)c).connect(address, communicationInboundPortURI);}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

	}

	@Override
	public void connectRouting(AddressI address, String communicationInboundPortURI, String routingInboundPortURI) throws Exception {
		this.getOwner().runTask( c -> {
			try {
				if (c instanceof Node) {((Node)c).connectRouting(address, communicationInboundPortURI, routingInboundPortURI);}
				else {((Router2Test)c).connectRouting(address, communicationInboundPortURI, routingInboundPortURI);}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void transmitMessage(MessageI m) throws Exception {
		this.getOwner().runTask(c->{
			try {
				if(c instanceof Node) {((Node)c).transmitMessage(m);}
				else {((Router2Test)c).transmitMessage(m);}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public int hasRouteFor(AddressI address)  throws Exception{
		return this.getOwner().handleRequest( c -> {
			if (c instanceof Node) return ((Node)c).hasRouteFor(address);
			else return ((Router2Test)c).hasRouteFor(address);
		});
	}

	@Override
	public void ping()  throws Exception{
		this.getOwner().handleRequest( c -> {
			if (c instanceof Node) {((Node)c).ping();}
			else {((Node)c).ping();}
			return null;
		} );
	}
}

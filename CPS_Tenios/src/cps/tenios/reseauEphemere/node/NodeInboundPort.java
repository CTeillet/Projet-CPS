package cps.tenios.reseauEphemere.node;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class NodeInboundPort extends AbstractInboundPort implements CommunicationCI {
	
	/**
	 *  Verison de s�rie de la classe
	 */
	private static final long serialVersionUID = 3226720899115114653L;
	
	/**
	 * Permet de cr�er un port entrant pour un noeud en sp�cifiant son propri�taire
	 * @param owner propri�taire du noeud
	 * @throws Exception s'il y a un probleme
	 */
	public NodeInboundPort(ComponentI owner) throws Exception {
		super(CommunicationCI.class, owner);
		assert owner.isOfferedInterface(CommunicationCI.class);
	}
	
	/**
	 * Cr�e un port en prenant l'uri voulue pour le port, ainsi que son propri�taire
	 * @param uri voulue pour le port
	 * @param owner propri�taire de classe
	 * @throws Exception s'il y a un probleme
	 */
	public NodeInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, CommunicationCI.class, owner);
		assert owner.isOfferedInterface(CommunicationCI.class);
	}

	@Override
	public void connect(NodeAddressI address, String communicationInboundPortURI) throws Exception {
		this.getOwner().handleRequest( c -> {((CommunicationCI)c).connect(address, communicationInboundPortURI); return null;} );

	}

	@Override
	public void connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI) throws Exception {
		this.getOwner().handleRequest( c -> {((CommunicationCI)c).connectRouting(address, communicationInboundPortURI, routingInboundPortURI);return null;});


	}

	@Override
	public void transmitMessage(MessageI m) throws Exception {
		//this.getOwner().handleRequest( c -> {((Node)c).transmitMessage(m); return null;} );
		this.getOwner().runTask(c->{
			try {
				((CommunicationCI)c).transmitMessage(m);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public int hasRouteFor(AddressI address)  throws Exception{
		return this.getOwner().handleRequest( c -> ((CommunicationCI)c).hasRouteFor(address));
	}

	@Override
	public void ping()  throws Exception{
		this.getOwner().handleRequest( c -> {((CommunicationCI)c).ping(); return null;} );

	}
}

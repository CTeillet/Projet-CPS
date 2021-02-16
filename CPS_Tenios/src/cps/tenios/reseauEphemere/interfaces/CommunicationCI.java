package cps.tenios.reseauEphemere.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * 
 * @author teill
 *
 */
public interface CommunicationCI extends OfferedCI, RequiredCI {
	/**
	 * 
	 * @param address
	 * @param communicationInboundPortURI
	 */
	void connect (NodeAddressI address, String communicationInboundPortURI) throws Exception;
	/**
	 * 
	 * @param address
	 * @param communicationInboundPortURI
	 * @param routingInboundPortURI
	 */
	void connectRouting (NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI) throws Exception;
	/**
	 * 
	 * @param m
	 */
	void transmitMessage(MessageI m) throws Exception;
	/**
	 * 
	 * @param address
	 * @return
	 */
	boolean hasRouteFor(AddressI address) throws Exception;
	/**
	 * 
	 */
	void ping() throws Exception;
}

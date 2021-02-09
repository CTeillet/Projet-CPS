package cps.tenios.interfaces;

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
	void connect (NodeAddressI address, String communicationInboundPortURI);
	/**
	 * 
	 * @param address
	 * @param communicationInboundPortURI
	 * @param routingInboundPortURI
	 */
	void connectRouting (NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI);
	/**
	 * 
	 * @param m
	 */
	void transmitMessage(MessageI m);
	/**
	 * 
	 * @param address
	 * @return
	 */
	boolean hasRouteFor(AddressI address);
	/**
	 * 
	 */
	void ping();
}

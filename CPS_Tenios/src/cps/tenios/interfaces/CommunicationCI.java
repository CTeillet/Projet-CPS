package cps.tenios.interfaces;
/**
 * 
 * @author teill
 *
 */
public interface CommunicationCI {
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

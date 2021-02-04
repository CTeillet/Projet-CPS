package cps.tenios.interfaces;

import java.util.Set;
/**
 * 
 * @author alexc
 *
 */
public interface RegistrationCI {

	/**
	 * 
	 * @param address
	 * @param communicationInboundPortURI
	 * @param initialPosition
	 * @param initialRange
	 * @return
	 */
	Set<ConnectionInfoI> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI, PositionI initialPosition, double initialRange);
	
	/**
	 * 
	 * @param address
	 * @param communicationInboundPortURI
	 * @param initialPosition
	 * @param initialRange
	 * @param routingInboundPortURI
	 * @return
	 */
	Set<ConnectionInfoI> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI, PositionI initialPosition, double initialRange, String routingInboundPortURI);
	
	/**
	 * 
	 * @param address
	 * @param communicationInboundPortURI
	 * @param initialPosition
	 * @param initialRange
	 * @param routingInboundPortURI
	 * @return
	 */
	Set<ConnectionInfoI> registerRoutingNode(NodeAddressI address, String communicationInboundPortURI, PositionI initialPosition, double initialRange, String routingInboundPortURI);
	
	/**
	 * 
	 * @param address
	 */
	void unregister (NodeAddressI address);

	
}

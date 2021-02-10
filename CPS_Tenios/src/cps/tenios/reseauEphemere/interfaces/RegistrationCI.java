package cps.tenios.reseauEphemere.interfaces;

import java.util.Set;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;
/**
 * 
 * @author alexc
 *
 */
public interface RegistrationCI extends RequiredCI, OfferedCI {

	/**
	 * 
	 * @param address
	 * @param communicationInboundPortURI
	 * @param initialPosition
	 * @param initialRange
	 * @return
	 */
	Set<ConnectionInfoI> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange) throws Exception;
	
	/**
	 * 
	 * @param address
	 * @param communicationInboundPortURI
	 * @param initialPosition
	 * @param initialRange
	 * @param routingInboundPortURI
	 * @return
	 */
	Set<ConnectionInfoI> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception;
	
	/**
	 * 
	 * @param address
	 * @param communicationInboundPortURI
	 * @param initialPosition
	 * @param initialRange
	 * @param routingInboundPortURI
	 * @return
	 */
	Set<ConnectionInfoI> registerRoutingNode(NodeAddressI address, String communicationInboundPortURI,
			PositionI initialPosition, double initialRange, String routingInboundPortURI) throws Exception;
	
	/**
	 * 
	 * @param address
	 */
	void unregister (NodeAddressI address) throws Exception;

	
}

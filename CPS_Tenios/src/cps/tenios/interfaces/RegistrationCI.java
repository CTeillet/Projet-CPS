package cps.tenios.interfaces;

import java.util.Set;

public interface RegistrationCI {

	Set<ConnectionInfoI> registerTerminalNode(NodeAddressI address, String communicationInboundPortURI, PositionI initialPosition, double initialRange);
	Set<ConnectionInfoI> registerAccessPoint(NodeAddressI address, String communicationInboundPortURI, PositionI initialPosition, double initialRange, String routingInboundPortURI);
	Set<ConnectionInfoI> registerRoutingNode(NodeAddressI address, String communicationInboundPortURI, PositionI initialPosition, double initialRange, String routingInboundPortURI);
	void unregister (NodeAddressI address);
	
}

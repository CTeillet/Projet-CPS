package cps.tenios.interfaces;

public interface CommunicationCI {
	void connect (NodeAddressI address, String communicationInboundPortURI);
	void connectRouting (NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI);
	void transmitMessage(MessageI m);
	boolean hasRouteFor(AddressI address);
	void ping();
}

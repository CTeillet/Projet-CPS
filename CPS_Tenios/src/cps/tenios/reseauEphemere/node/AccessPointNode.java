package cps.tenios.reseauEphemere.node;

import java.sql.Connection;
import java.util.Set;

import cps.tenios.reseauEphemere.NodeAddress;
import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;

public class AccessPointNode extends Node {

	protected AccessPointNode() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws Exception {
		super.addr=new NodeAddress(0); // TODO Modifier le numéro

				
	}

	@Override
	public void connect(NodeAddressI address, String communicationInboundPortURI) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String connectRouting(NodeAddressI address, String communicationInboundPortURI, String routingInboundPortURI)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void transmitMessage(MessageI m) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasRouteFor(AddressI address) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void ping() throws Exception {
		// TODO Auto-generated method stub
		
	}

}

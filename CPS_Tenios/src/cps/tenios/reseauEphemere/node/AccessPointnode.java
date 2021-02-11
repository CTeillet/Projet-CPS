package cps.tenios.reseauEphemere.node;

import java.sql.Connection;
import java.util.Set;

import cps.tenios.reseauEphemere.NodeAddress;

public class AccessPointnode extends Node {

	protected AccessPointnode() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws Exception {
		super.addr=new NodeAddress(0); // TODO Modifier le numéro
		Set<Connection> res = super.registrationOutboundPort.registerAccessPoint(
				super.addr, super.inb, null,
				INITIAL_EXECUTOR_SERVICES_POOL_SIZE, reflectionInboundPortURI);
				
	}

}

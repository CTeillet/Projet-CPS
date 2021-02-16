package cps.tenios.reseauEphemere.node;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.CommunicationCI;
import cps.tenios.reseauEphemere.interfaces.MessageI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;

@OfferedInterfaces(offered = {CommunicationCI.class})
@RequiredInterfaces(required = {CommunicationCI.class, RegistrationCI.class})
public class TerminalNode extends Node {

	protected TerminalNode(String uri) throws Exception {
		super(uri);
		logMessage(super.registrationOutboundPort.getPortURI());
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws Exception {
		logMessage("Debut Execute TerminalNode");
		super.registrationOutboundPort.registerTerminalNode(super.addr, "", super.pos, 100.);
		super.registrationOutboundPort.unregister(super.addr);
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

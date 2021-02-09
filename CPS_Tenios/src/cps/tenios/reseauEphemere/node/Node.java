package cps.tenios.reseauEphemere.node;

import cps.tenios.interfaces.CommunicationCI;
import cps.tenios.interfaces.RegistrationCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;

@OfferedInterfaces(offered = {CommunicationCI.class})
@RequiredInterfaces(required = {CommunicationCI.class, RegistrationCI.class})
public class Node extends AbstractComponent{
	
	protected NodeOutboundPort outboundPort;
	protected NodeInboundPort inboundPort;

	protected Node() {
		super(0, 1);
		// TODO Auto-generated constructor stub
	}
	
	

}

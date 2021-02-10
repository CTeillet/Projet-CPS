package cps.tenios.reseauEphemere.gestionnaireReseau;

import java.util.Set;

import cps.tenios.reseauEphemere.interfaces.ConnectionInfoI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;

@OfferedInterfaces(offered = {RegistrationCI.class})
public class GestionnaireReseau extends AbstractComponent {
	
	public static final String  INBOUNDPORT_URI = "registrationInboundPort-uri";
	private RegistrationInboundPort registrationInboundPort;

	private Set<ConnectionInfoI> table;
	protected GestionnaireReseau() {
		super(1, 0);
		this.registrationInboundPort = new RegistrationInboundPort(INBOUNDPORT_URI, this);
		this.registrationInboundPort.publishPort();
	}
	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.registrationInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

}

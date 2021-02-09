package cps.tenios.reseauEphemere.gestionnaireReseau;

import java.util.Set;

import cps.tenios.reseauEphemere.interfaces.ConnectionInfoI;
import cps.tenios.reseauEphemere.interfaces.RegistrationCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;

@OfferedInterfaces(offered = {RegistrationCI.class})
public class GestionnaireReseau extends AbstractComponent {
	
	private Set<ConnectionInfoI> table;
	private RegistrationInboundPort inboundPort;

	protected GestionnaireReseau() {
		super(1, 0);
		// TODO Auto-generated constructor stub
	}

}

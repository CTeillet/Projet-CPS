package cps.tenios.gestionnaireReseau;

import java.util.Set;

import cps.tenios.interfaces.ConnectionInfoI;
import cps.tenios.interfaces.RegistrationCI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;

@OfferedInterfaces(offered = {RegistrationCI.class})
public class GestionnaireReseau extends AbstractComponent {
	
	private Set<ConnectionInfoI> table;

	protected GestionnaireReseau(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}

}

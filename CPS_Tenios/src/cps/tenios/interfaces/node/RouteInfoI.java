package cps.tenios.interfaces.node;

import cps.tenios.interfaces.gestionnaireReseau.AddressI;

public interface RouteInfoI {
	AddressI getDestination();
	int getNumberOfHops();
}

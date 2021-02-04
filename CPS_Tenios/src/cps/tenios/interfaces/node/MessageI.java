package cps.tenios.interfaces.node;

import java.io.Serializable;

import cps.tenios.interfaces.gestionnaireReseau.AddressI;

public interface MessageI {
	AddressI getAddress();
	Serializable getContent();
	boolean stillAlive();
	void decrementsGops();
}

package cps.tenios.interfaces;

import java.io.Serializable;

public interface MessageI {
	AddressI getAddress();
	Serializable getContent();
	boolean stillAlive();
	void decrementsGops();
}

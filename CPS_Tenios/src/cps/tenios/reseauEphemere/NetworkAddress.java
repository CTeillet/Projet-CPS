package cps.tenios.reseauEphemere;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.NetworkAddressI;

/**
 * 
 * @author Tenios
 *
 */
public class NetworkAddress implements AddressI {
	/**
	 * Addresse
	 */
	public final int addr;
	/**
	 * Identifiant machine
	 */
	public final int idMachine;
	
	/**
	 * Constructeur de NetworkAddress
	 * @param addr Adresse
	 */
	public NetworkAddress(int idMachine, int addr) {
		this.idMachine = idMachine;
		this.addr = addr;
	}

	@Override
	public boolean equals(AddressI a) {
		if (a.isNetworkAddress()) {
			NetworkAddress n = (NetworkAddress) a;
			return n.addr==this.addr && n.idMachine == this.idMachine;
		}
		return false;
	}

	@Override
	public boolean isNodeAddress() {
		return false;
	}

	@Override
	public boolean isNetworkAddress() {
		return true;
	}

}

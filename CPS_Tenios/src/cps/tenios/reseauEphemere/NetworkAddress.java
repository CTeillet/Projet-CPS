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
	 * Adresse 
	 */
	public final int addr;
	
	/**
	 * Constructeur de NetworkAddress
	 * @param addr Adresse
	 */
	public NetworkAddress(int addr) {
		this.addr = addr;
	}

	@Override
	public boolean equals(AddressI a) {
		if (a.isNetworkAddress()) {
			NetworkAddress n = (NetworkAddress) a;
			return n.addr==this.addr;
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

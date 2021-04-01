package cps.tenios.reseauEphemere;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;

/**
 * 
 * @author Tenios
 *
 */
public class NodeAddress implements AddressI {
	/**
	 * Addresse
	 */
	public final int addr;

	/**
	 * Constructeur de NodeAddress
	 * @param addr Addresse
	 */
	public NodeAddress(int addr) {
		this.addr = addr;
	}

	@Override
	public boolean isNodeAddress() {
		return true;
	}

	@Override
	public boolean isNetworkAddress() {
		return false;
	}

	@Override
	public boolean equals(AddressI a) {
		if (a.isNodeAddress()) {
			NodeAddress n = (NodeAddress) a;
			return n.addr==this.addr;
		}
		return false;
	}

}

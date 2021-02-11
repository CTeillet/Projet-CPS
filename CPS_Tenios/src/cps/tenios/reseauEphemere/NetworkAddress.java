package cps.tenios.reseauEphemere;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.NetworkAddressI;

public class NetworkAddress implements NetworkAddressI {
	public final int addr;

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

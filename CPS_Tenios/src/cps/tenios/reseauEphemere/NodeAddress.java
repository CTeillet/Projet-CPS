package cps.tenios.reseauEphemere;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.NodeAddressI;

public class NodeAddress implements NodeAddressI {
	public final int addr;

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

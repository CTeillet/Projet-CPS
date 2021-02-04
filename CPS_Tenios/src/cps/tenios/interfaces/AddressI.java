package cps.tenios.interfaces;

public interface AddressI {
	boolean isNodeAddress();
	boolean isNetworkAddress();
	boolean equals(AddressI a);
}

package cps.tenios.interfaces.gestionnaireReseau;

public interface AddressI {
	boolean isNodeAddress();
	boolean isNetworkAddress();
	boolean equals(AddressI a);

}

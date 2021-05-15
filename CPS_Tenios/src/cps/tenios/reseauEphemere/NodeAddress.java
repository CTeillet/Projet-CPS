package cps.tenios.reseauEphemere;


import cps.tenios.reseauEphemere.interfaces.AddressI;

/**
 * 
 * @author Tenios
 *
 */
public class NodeAddress implements AddressI {
	/**
	 * Utilisé pour la Serialization
	 */
	private static final long serialVersionUID = 6287121261640088195L;
	/**
	 * Addresse
	 */
	public final int addr;
	/**
	 * Identifiant machine
	 */
	public final int idMachine;

	/**
	 * Constructeur de NodeAddress
	 * @param addr Addresse
	 */
	public NodeAddress(int idMachine ,int addr) {
		this.idMachine = idMachine;
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
			return n.addr==this.addr && n.idMachine == this.idMachine;
		}
		return false;
	}

	@Override
	public String toString() {
		return "NodeAddress [addr=" + addr + ", idMachine=" + idMachine + "]";
	}
	
	

}

package cps.tenios.reseauEphemere.node;

public class Triplet <E, T, V> {

	private E label;
	private T node;
	private V rout;

	public Triplet(E label, T node, V rout) {
		super();
		this.label = label;
		this.node = node;
		this.rout = rout;
	}

	public E getLabel() {
		return label;
	}
	
	public T getNode() {
		return node;
	}
	
	public void setLabel(E label) {
		this.label = label;
	}
	
	public void setNode(T node) {
		this.node = node;
	}

	public V getRout() {
		return rout;
	}

	public void setRout(V rout) {
		this.rout = rout;
	}

}

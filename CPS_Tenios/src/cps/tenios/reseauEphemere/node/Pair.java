package cps.tenios.reseauEphemere.node;

public class Pair <E, T>{

	private E label;
	private T node;
	
	public Pair(E label, T node) {
		this.label = label;
		this.node = node;
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

}

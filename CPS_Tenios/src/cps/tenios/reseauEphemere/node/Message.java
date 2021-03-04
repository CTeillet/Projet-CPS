package cps.tenios.reseauEphemere.node;

import java.io.Serializable;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.MessageI;

public class Message implements MessageI {
	private AddressI address;
	private Serializable content;
	private int gops;

	
	
	public Message(AddressI address, Serializable content, int gops) {
		this.address = address;
		this.content = content;
		this.gops = gops;
	}

	public Message(Message m1) {
		this.address = m1.address;
		this.content = m1.content;
		this.gops = m1.gops;
	}

	@Override
	public boolean stillAlive() {
		return gops>0;
	}

	@Override
	public void decrementsGops() {
		gops--;
	}
	

	@Override
	public AddressI getAddress() {
		return address;
	}
	
	@Override
	public Serializable getContent() {
		return content;
	}
	
	public int aSupprimer() {
		return gops;
	}
	

}

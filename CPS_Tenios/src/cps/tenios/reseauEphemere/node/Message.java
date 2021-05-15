package cps.tenios.reseauEphemere.node;

import java.io.Serializable;

import cps.tenios.reseauEphemere.interfaces.AddressI;
import cps.tenios.reseauEphemere.interfaces.MessageI;

/**
 * Class represantant les messages dans le reseau
 * @author Tenios
 *
 */
public class Message implements MessageI {
	/**
	 * Addresse de destination du message
	 */
	private AddressI address;
	/**
	 * Contenue du message
	 */
	private Serializable content;
	/**
	 * nombre de saut avant destruction
	 */
	private int gops;


	/**
	 * Permet de construire un message
	 * @param address Addresse de destination du message
	 * @param content Contenue du message
	 * @param gops Nombre de saut avant destruction
	 */
	public Message(AddressI address, Serializable content, int gops) {
		this.address = address;
		this.content = content;
		this.gops = gops;
	}

	/**
	 * Constructeur par copie
	 * @param m1 message a copier
	 */
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
}

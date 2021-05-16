package cps.tenios.reseauEphemere.node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class TousChemins {

	private List<Chemin> all;
	
	public TousChemins() {
		all = new ArrayList<Chemin>();
	}
	
	public TousChemins(CommunicationOutboundPort cop, int nbHops) {
		all = new ArrayList<Chemin>();
		all.add(new Chemin(cop, nbHops));
	}
	
	public class ComparatorChemin implements Comparator<Chemin> {
		@Override
	    public int compare(Chemin c1, Chemin c2){
	        return Integer.compare(c1.getNumberOfHops(), c2.getNumberOfHops());
		}
	}
	
	public boolean add(CommunicationOutboundPort cop, int nbHops) throws Exception {
		int index = 0;
		for (Chemin chemin : all) {
			if(chemin.getNext().getClientPortURI() == cop.getClientPortURI()) {
				break;
			}
			index++;
		}
		
		if (index < all.size()) {
			Chemin c = all.get(index);
			if ( c.getNumberOfHops() > nbHops) {
				c.setNumberOfHops(nbHops);
				all.sort(new ComparatorChemin());
				return true;
			}
		} else {
			all.add(new Chemin(cop, nbHops));
			all.sort(new ComparatorChemin());
			return true;
		}
		
		return false;
	}
	
	public boolean add(Chemin c) throws Exception {
		return this.add(c.getNext(), c.getNumberOfHops());
	}
	
	public Chemin getChemin(CommunicationOutboundPort cop) throws Exception {
		for (Chemin chemin : all) {
			if(chemin.getNext().getClientPortURI() == cop.getClientPortURI()) {
				return chemin;
			}
		}
		return null;
	}
	
	public Chemin getFirstChemin() {
		return all.get(0);
	}
	
	public void delete(CommunicationOutboundPort cop) {
		ArrayList<Chemin> toRemove = new ArrayList<Chemin>();
		for (Chemin chemin : all) {
			try {
				if (chemin.getNext().getClientPortURI() == cop.getClientPortURI()) {
					toRemove.add(chemin);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		all.removeAll(toRemove);
	}

}

package cn.nutkc.domain;

import java.util.LinkedList;

public class PokerBox {
	private LinkedList<Poker> pokers = new LinkedList<Poker>();

	public PokerBox() {
		reset();
	}
	
	
	public void showPokers(){
		for(Poker poker : pokers) {
			System.out.println(poker);
		}
	}
	
	
	public int getSize() {
		return pokers.size();
	}


	public LinkedList<Poker> getPokers() {
		return pokers;
	}


	public void reset() {
		pokers.clear();
		for(int i=1; i<=15; i++) {
			if(i>13) {
				Poker poker = new Poker();
				poker.setType(PokerType.JOKER);
				poker.setValue(i);
				pokers.add(poker);
				continue;
			}
			for(int j=0; j<4; j++) {
				Poker poker = new Poker();
				poker.setValue(i);
				poker.setType(PokerType.values()[j].name());
				pokers.add(poker);
			}
		}
	}
		
}

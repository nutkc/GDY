package cn.nutkc.domain;

import java.util.Comparator;

public class PokerComparator implements Comparator<Poker> {

	@Override
	public int compare(Poker p1, Poker p2) {
		// TODO Auto-generated method stub
		int result = p1.getValue() - p2.getValue();
		if(result == 0) {
			result = -1;
		}
		return result;
	}
}

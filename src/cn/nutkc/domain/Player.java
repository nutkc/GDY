package cn.nutkc.domain;

import java.util.LinkedList;

public class Player {
	private LinkedList<Poker> pokers;
	private boolean banker;
	private String id;
	private boolean winner;
	//���췽��
	public Player(String id) {
		this.id = id;
		pokers = new LinkedList<Poker>();
	}
	
	//���õ�������ID
	public void reset() {
		this.clear();
		setWinner(false);
	}
	
	//���� ������������Ƴ�Ҫ�����Ʋ�������Щ��
	public boolean produce(LinkedList<Poker> pokers) {
		return this.pokers.removeAll(pokers);
	}
	
	//�������
	private void clear() {
		pokers.clear();
	}

	
	/**
	 *  GET/SET   
	 */
	//ID
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	//Banker
	public boolean isBanker() {
		return banker;
	}

	public void setBanker(boolean banker) {
		this.banker = banker;
	}

	//Pokers
	public LinkedList<Poker> getPokers() {
		return pokers;
	}

	public void setPokers(LinkedList<Poker> pokers) {
		this.pokers = pokers;
	}
	
	//Winner
	public void setWinner(boolean b) {
		this.winner = b;
	}
	
	public boolean isWinner() {
		return this.winner;
	}

	@Override
	public String toString() {
		return "��ǰ���ID��" + id + ",ׯ�ң�" + banker + "Ӯ�ң�"  + winner 
				+ "�����ƹ���" + pokers.size() + "��\r\npokers=" + pokers;
	}
}

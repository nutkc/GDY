package cn.nutkc.domain;

import java.util.LinkedList;

public class Player {
	private LinkedList<Poker> pokers;
	private boolean banker;
	private String id;
	private boolean winner;
	//构造方法
	public Player(String id) {
		this.id = id;
		pokers = new LinkedList<Poker>();
	}
	
	//重置但不更改ID
	public void reset() {
		this.clear();
		setWinner(false);
	}
	
	//出牌 从玩家手牌中移除要出的牌并返回这些牌
	public boolean produce(LinkedList<Poker> pokers) {
		return this.pokers.removeAll(pokers);
	}
	
	//清除手牌
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
		return "当前玩家ID：" + id + ",庄家：" + banker + "赢家："  + winner 
				+ "，手牌共有" + pokers.size() + "张\r\npokers=" + pokers;
	}
}

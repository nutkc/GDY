package cn.nutkc.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class Table {
	private PokerBox pokerBox;
	private HashMap<String,Player> players;
	private GameRule rule;
	private boolean gameover;
	private boolean playing;
	private int mult = 1;
	public boolean isPlaying() {
		return playing;
	}
	public Table() {
		pokerBox = new PokerBox();
		players = new HashMap<String, Player>();
		rule = new GameRule();
	}
	public boolean produce(Player p, LinkedList<Poker> pokers) {
		if(rule.compare(pokers)) { //检查是否符合出牌规则
			if(rule.checkBomb(pokers)) mult *= 2;
			return p.produce(pokers);
		}
		return false;
	}
	public boolean addPlayer(boolean banker,String id) {
		Player player = new Player(id);
		if(banker) player.setBanker(true);
		return players.put(id,player) == null ;
	}
	
	/**
	 * 重置桌面
	 * 1.重置pokerBox
	 * 2.重置rule记录
	 * 3.重置玩家手牌
	 */
	public void reset() {
		pokerBox.reset();
		rule.clear();
		for(Entry<String, Player> e : players.entrySet()) {
			e.getValue().reset();
		}
		playing = false;
		setGameover(false);
		mult = 1;
	}
	/**
	 * 开局
	 * 按照玩家个数随即选出手牌并分发给玩家
	 */
	public void opening() {
		setGameover(false);
		LinkedList<Poker> pokers = pokerBox.getPokers();
		for(Entry<String, Player> e : players.entrySet()) {
			Player player = e.getValue();
			int pokerNum = 5;
			if(player.isBanker()) pokerNum = 6;
			LinkedList<Poker> player_pokers = player.getPokers();
			for(int i=0; i<pokerNum; i++) {
				int index = (int) (Math.random() * pokers.size());
				Poker poker = pokers.get(index);
				if(pokers.remove(poker)) player_pokers.add(poker);
			}
		}
		playing = true;
	}
	
	public boolean addAPoker(String id) {
		LinkedList<Poker> pokers = pokerBox.getPokers();
		if(pokers.size() > 0) {
			Player p = getPlayer(id);
			LinkedList<Poker> player_pokers = p.getPokers();
			int index = (int) (Math.random() * pokers.size());
			Poker poker = pokers.get(index);
			if(pokers.remove(poker)) {
				player_pokers.add(poker);
				return true;
			}
		} 
		return false;
	}
	
	public PokerBox getPokerBox() {
		return pokerBox;
	}

	public HashMap<String, Player> getPlayers() {
		return players;
	}

	public void setPlayers(HashMap<String, Player> players) {
		this.players = players;
	}

	public GameRule getRule() {
		return rule;
	}
	public void setRule(GameRule rule) {
		this.rule = rule;
	}
	public void setPokerBox(PokerBox pokerBox) {
		this.pokerBox = pokerBox;
	}
	public Player getPlayer(String id) {
		return players.get(id);
	}
	/*
	 * 清除出牌记录
	 * 
	 */
	public void clearRecord() {
		rule.clear();
	}
	public boolean isGameover() {
		return gameover;
	}
	public void setGameover(boolean gameover) {
		this.gameover = gameover;
	}
	public HashMap<String, Integer> getScore() {
		HashMap<String, Integer> score = new HashMap<String, Integer>();
		Set<String> ps = players.keySet();
		String win = null;
		int sum = 0;
		for(String p : ps) {
			int s = players.get(p).getPokers().size();
			if(s == 0) {
				win = p;
			}
			if(s == 5) s *= 2 ;
			sum += s;
			score.put(p, -s);
		}
		score.put(win, sum);
		for(Entry<String, Integer> e : score.entrySet()) {
			score.put(e.getKey(), e.getValue() * mult);
		}
		return score;
	}
	public void setBnaker(String id) {
		players.get(id).setBanker(true);
	}
	public String getBanker() {
		Set<Entry<String, Player>> pes = players.entrySet();
		String banker = null;
		for(Entry<String, Player> pe : pes) {
			if(pe.getValue().isBanker()) {
				banker = pe.getKey();
				break;
			}
		}
		return banker;
	}
	
}
